package pl.asie.computronics.oc;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.world.World;
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

import java.io.IOException;
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

	private long buildDelay = 0;
	private long nextDelay = 0;
	private long timeout = System.currentTimeMillis();
	private int soundVolume = 127;
	private int codecId;

	private int packetSizeMS = 500;
	private int maxInstructions = Integer.MAX_VALUE;
	private int maxDelay = Integer.MAX_VALUE;

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
		buildBuffer.add(new Open(args.checkInteger(0)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] close(Context context, Arguments args) {
		buildBuffer.add(new Close(args.checkInteger(0)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number, audiotype:string, frequency:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setWave(Context context, Arguments args) {
		buildBuffer.add(new SetWave(args.checkInteger(0), AudioType.valueOf(args.checkString(1)), (float) args.checkDouble(2)));
		return new Object[] {};
	}

	@Callback(doc = "function(duration:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] delay(Context context, Arguments args) {
		int duration = args.checkInteger(0);
		buildDelay += duration;
		buildBuffer.add(new Delay(duration));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number, modIndex:number, index:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFM(Context context, Arguments args) {
		buildBuffer.add(new SetFM(args.checkInteger(0), args.checkInteger(1), (float) args.checkDouble(2)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetFM(Context context, Arguments args) {
		buildBuffer.add(new ResetFM(args.checkInteger(0)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number, modIndex:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setAM(Context context, Arguments args) {
		buildBuffer.add(new SetAM(args.checkInteger(0), args.checkInteger(1)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetAM(Context context, Arguments args) {
		buildBuffer.add(new ResetAM(args.checkInteger(0)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number, attack:number, decay:number, sustain:number, release:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setADSR(Context context, Arguments args) {
		buildBuffer.add(new SetADSR(args.checkInteger(0), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetEnvelope(Context context, Arguments args) {
		buildBuffer.add(new ResetEnvelope(args.checkInteger(0)));
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number, volume:number); ", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		buildBuffer.add(new SetVolume(args.checkInteger(0), (float) args.checkDouble(1)));
		return new Object[] {};
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

	private void sendMusicPacket(long delay, Queue<Instruction> instructions) {
		SoundCardPacket pkt = new SoundCardPacket(this, (byte) soundVolume, node().address(), delay, instructions);
		internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);
		pkt.sendPacket();
	}

	protected void sendSound(long delay, Queue<Instruction> buffer) {
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
