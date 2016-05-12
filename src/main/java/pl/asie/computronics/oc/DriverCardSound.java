package pl.asie.computronics.oc;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.sound.AudioType;
import pl.asie.computronics.util.sound.AudioUtil;
import pl.asie.computronics.util.sound.Instruction;
import pl.asie.computronics.util.sound.Instruction.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Vexatos, gamax92
 */
public class DriverCardSound extends ManagedEnvironment implements IAudioSource {

	protected final EnvironmentHost host;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("sound").
			withConnector(Config.SOUND_ENERGY_COST * 42).
			create());
		buildBuffer = new LinkedList<Instruction>();
		process = new AudioUtil.AudioProcess(8);

		codecId = Computronics.opencomputers.audio.newPlayer();
		Computronics.opencomputers.audio.getPlayer(codecId);
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(ForgeDirection side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return host.world();
		}

		@Override
		public int getSoundX() {
			return (int)Math.floor(host.xPosition());
		}

		@Override
		public int getSoundY() {
			return (int)Math.floor(host.yPosition());
		}

		@Override
		public int getSoundZ() {
			return (int)Math.floor(host.zPosition());
		}

		@Override
		public int getSoundDistance() {
			return Config.SOUND_RADIUS;
		}

		@Override
		public void receivePacket(AudioPacket packet, ForgeDirection direction) {
			packet.addReceiver(this);
		}
	};

	private AudioUtil.AudioProcess process;
	private Queue<Instruction> buildBuffer;
	private Queue<Instruction> nextBuffer;

	private int buildDelay = 0;
	private int nextDelay = 0;
	private long timeout = System.currentTimeMillis();
	private int soundVolume = 127;
	private int codecId;

	private int packetSizeMS = 500;
	private int maxInstructions = Integer.MAX_VALUE;
	private int maxDelayMS = Integer.MAX_VALUE;

	@Override
	public boolean canUpdate() {
		return !host.world().isRemote;
	}

	@Override
	public void update() {
		if (nextBuffer != null && System.currentTimeMillis() >= timeout - 100) {
			sendSound(nextDelay, nextBuffer);
			timeout = timeout + nextDelay;
			nextBuffer = null;
		}
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(nbt.hasKey("bbuffer")) {
			buildBuffer = Instruction.fromNBT(nbt.getTagList("bbuffer", Constants.NBT.TAG_COMPOUND));
			buildDelay = 0;
			for (Instruction inst : buildBuffer) {
				if(inst instanceof Delay) {
					buildDelay += ((Delay)inst).delay;
				}
			}
		}
		if(nbt.hasKey("nbuffer")) {
			nextBuffer = Instruction.fromNBT(nbt.getTagList("bbuffer", Constants.NBT.TAG_COMPOUND));
			nextDelay = 0;
			for (Instruction inst : nextBuffer) {
				if(inst instanceof Delay) {
					nextDelay += ((Delay)inst).delay;
				}
			}
		}
		if(nbt.hasKey("volume")) {
			soundVolume = nbt.getByte("volume");
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		if(buildBuffer != null) {
			NBTTagList buildNBT = new NBTTagList();
			Instruction.toNBT(buildNBT, buildBuffer);
			nbt.setTag("bbuffer", buildNBT);
		}
		if(nextBuffer != null) {
			NBTTagList nextNBT = new NBTTagList();
			Instruction.toNBT(nextNBT, nextBuffer);
			nbt.setTag("nbuffer", nextNBT);
		}
		nbt.setByte("volume", (byte) soundVolume);
	}

	private Object[] tryAdd(Instruction inst) {
		if(buildBuffer.size() >= maxInstructions) {
			return new Object[] {false, "too many instructions"};
		}
		buildBuffer.add(inst);
		return new Object[] {true};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setMusicVolume(Context context, Arguments args) {
		double volume = args.checkDouble(0);
		if(volume < 0.0F) volume = 0.0F;
		if(volume > 1.0F) volume = 1.0F;
		this.soundVolume = (int) Math.floor(volume * 127.0F);
		return new Object[] {};
	}

	@Callback(doc = "function(); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] clear(Context context, Arguments args) {
		buildBuffer.clear();
		buildDelay = 0;
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] open(Context context, Arguments args) {
		return tryAdd(new Open(args.checkInteger(0)));
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] close(Context context, Arguments args) {
		return tryAdd(new Close(args.checkInteger(0)));
	}

	@Callback(doc = "function(channel:number, audiotype:string, frequency:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setWave(Context context, Arguments args) {
		return tryAdd(new SetWave(args.checkInteger(0), AudioType.valueOf(args.checkString(1)), (float) args.checkDouble(2)));
	}

	@Callback(doc = "function(duration:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] delay(Context context, Arguments args) {
		int duration = args.checkInteger(0);
		if (buildDelay + duration > maxDelayMS)
			return new Object[] {false, "too many delays"};
		buildDelay += duration;
		return tryAdd(new Delay(duration));
	}

	@Callback(doc = "function(channel:number, modIndex:number, index:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFM(Context context, Arguments args) {
		return tryAdd(new SetFM(args.checkInteger(0), args.checkInteger(1), (float) args.checkDouble(2)));
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetFM(Context context, Arguments args) {
		return tryAdd(new ResetFM(args.checkInteger(0)));
	}

	@Callback(doc = "function(channel:number, modIndex:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setAM(Context context, Arguments args) {
		return tryAdd(new SetAM(args.checkInteger(0), args.checkInteger(1)));
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetAM(Context context, Arguments args) {
		return tryAdd(new ResetAM(args.checkInteger(0)));
	}

	@Callback(doc = "function(channel:number, attack:number, decay:number, sustain:number, release:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setADSR(Context context, Arguments args) {
		return tryAdd(new SetADSR(args.checkInteger(0), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetEnvelope(Context context, Arguments args) {
		return tryAdd(new ResetEnvelope(args.checkInteger(0)));
	}

	@Callback(doc = "function(channel:number, volume:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		return tryAdd(new SetVolume(args.checkInteger(0), (float) args.checkDouble(1)));
	}

	@Callback(doc = "function(); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] process(Context context, Arguments args) {
		if (buildBuffer.size() == 0)
			return new Object[] {true};
		if (nextBuffer == null) {
			nextBuffer = buildBuffer;
			nextDelay = buildDelay;
			buildBuffer = new LinkedList<Instruction>();
			buildDelay = 0;
			if (System.currentTimeMillis() > timeout)
				timeout = System.currentTimeMillis();
			return new Object[] {true};
		} else
			return new Object[] {false,System.currentTimeMillis(),timeout};
	}

	private void sendMusicPacket(int delay, Queue<Instruction> instructions) {
		SoundCardPacket pkt = new SoundCardPacket(this, (byte) soundVolume, node().address(), delay, instructions);
		internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);
		pkt.sendPacket();
	}

	protected void sendSound(int delay, Queue<Instruction> buffer) {
		int counter = 0;
		Queue<Instruction> sendBuffer = new LinkedList<Instruction>();
		while(!buffer.isEmpty() || process.delay > 0) {
			if(process.delay > 0) {
				if(counter + process.delay < packetSizeMS) {
					sendBuffer.add(new Delay(process.delay));
					counter += process.delay;
				} else {
					while(process.delay > 0) {
						int remove = Math.min(process.delay, packetSizeMS-counter);
						sendBuffer.add(new Delay(remove));
						if(remove+counter >= packetSizeMS) {
							sendMusicPacket(remove+counter, sendBuffer);
							sendBuffer.clear();
							counter = 0;
						} else {
							counter += remove;
						}
						process.delay -= remove;
					}
				}
				process.delay = 0;
			} else {
				Instruction inst = buffer.poll();
				inst.encounter(process);
				if (!(inst instanceof Delay)) {
					sendBuffer.add(inst);
				}
			}
		}
		if(sendBuffer.size() > 0) {
			sendMusicPacket(counter, sendBuffer);
		}
	}

	@Override
	public boolean connectsAudio(ForgeDirection side) {
		return false;
	}

	@Override
	public int getSourceId() {
		return codecId;
	}
}
