package pl.asie.computronics.oc.driver;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.audio.SoundCardPacketClientHandler;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.sound.AudioType;
import pl.asie.computronics.util.sound.AudioUtil;
import pl.asie.computronics.util.sound.Instruction;
import pl.asie.computronics.util.sound.Instruction.Close;
import pl.asie.computronics.util.sound.Instruction.Delay;
import pl.asie.computronics.util.sound.Instruction.Open;
import pl.asie.computronics.util.sound.Instruction.ResetAM;
import pl.asie.computronics.util.sound.Instruction.ResetEnvelope;
import pl.asie.computronics.util.sound.Instruction.ResetFM;
import pl.asie.computronics.util.sound.Instruction.SetADSR;
import pl.asie.computronics.util.sound.Instruction.SetAM;
import pl.asie.computronics.util.sound.Instruction.SetFM;
import pl.asie.computronics.util.sound.Instruction.SetVolume;
import pl.asie.computronics.util.sound.Instruction.SetWave;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Vexatos, gamax92
 */
public class DriverCardSound extends ManagedEnvironmentWithComponentConnector implements IAudioSource {

	protected final EnvironmentHost host;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("sound").
			withConnector().
			create());
		process = new AudioUtil.AudioProcess(Config.SOUND_CARD_CHANNEL_COUNT);

		if(host.world().isRemote) {
			SyncHandler.envs.add(this);
			buildBuffer = null;
			nextBuffer = null;
		} else {
			buildBuffer = new ArrayDeque<Instruction>();
			nextBuffer = new ArrayDeque<Instruction>();
		}
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
		public Vec3 getSoundPos() {
			return Vec3.createVectorHelper(host.xPosition(), host.yPosition(), host.zPosition());
		}

		@Override
		public int getSoundDistance() {
			return Config.SOUND_RADIUS;
		}

		@Override
		public void receivePacket(AudioPacket packet, ForgeDirection direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return host instanceof TileEntity ? AudioUtils.positionId(host.xPosition(), host.yPosition(), host.zPosition()) : "";
		}

	};

	private AudioUtil.AudioProcess process;
	private final ArrayDeque<Instruction> buildBuffer;
	private final ArrayDeque<Instruction> nextBuffer;

	private int buildDelay = 0;
	private int nextDelay = 0;
	private long timeout = System.currentTimeMillis();
	private int soundVolume = 127;
	private Integer codecId;
	private String clientAddress;

	private final int soundTimeoutMS = 250;

	public static class SyncHandler {

		static Set<DriverCardSound> envs = Collections.newSetFromMap(new WeakHashMap<DriverCardSound, Boolean>());

		@SideOnly(Side.CLIENT)
		private static SoundCardPacketClientHandler getHandler() {
			return (SoundCardPacketClientHandler) AudioPacketRegistry.INSTANCE.getClientHandler(AudioPacketRegistry.INSTANCE.getId(SoundCardPacket.class));
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload evt) {
			for(DriverCardSound env : envs) {
				if(env.host.world() == evt.world && evt.getChunk().isAtLocation(MathHelper.floor_double(env.host.xPosition()) >> 4, MathHelper.floor_double(env.host.zPosition()) >> 4)) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload evt) {
			for(DriverCardSound env : envs) {
				if(env.host.world() == evt.world) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}
	}

	@Override
	public boolean canUpdate() {
		return !host.world().isRemote;
	}

	@Override
	public void update() {
		if(nextBuffer != null && !nextBuffer.isEmpty() && System.currentTimeMillis() >= timeout - 100) {
			final ArrayDeque<Instruction> clone;
			synchronized(nextBuffer) {
				clone = nextBuffer.clone();
				timeout = timeout + nextDelay;
				nextBuffer.clear();
			}
			sendSound(clone);
		} else if(codecId != null && System.currentTimeMillis() >= timeout + soundTimeoutMS) {
			AudioUtils.removePlayer(Computronics.opencomputers.managerId, codecId);
			codecId = null;
		}
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(nbt.hasKey("process")) {
			process.load(nbt.getCompoundTag("process"));
		}
		if(host.world().isRemote) {
			if(nbt.hasKey("node")) {
				NBTTagCompound nodeNBT = nbt.getCompoundTag("node");
				if(nodeNBT.hasKey("address")) {
					clientAddress = nodeNBT.getString("address");
					SyncHandler.getHandler().setProcess(clientAddress, process);
				}
			}
		} else {
			if(nbt.hasKey("bbuffer")) {
				if(buildBuffer != null) {
					synchronized(buildBuffer) {
						buildBuffer.clear();
						buildBuffer.addAll(Instruction.fromNBT(nbt.getTagList("bbuffer", Constants.NBT.TAG_COMPOUND)));
						buildDelay = 0;
						for(Instruction inst : buildBuffer) {
							if(inst instanceof Delay) {
								buildDelay += ((Delay) inst).delay;
							}
						}
					}
				}
			}
			if(nbt.hasKey("nbuffer")) {
				if(nextBuffer != null) {
					synchronized(nextBuffer) {
						nextBuffer.clear();
						nextBuffer.addAll(Instruction.fromNBT(nbt.getTagList("nbuffer", Constants.NBT.TAG_COMPOUND)));
						nextDelay = 0;
						for(Instruction inst : nextBuffer) {
							if(inst instanceof Delay) {
								nextDelay += ((Delay) inst).delay;
							}
						}
					}
				}
			}
			if(nbt.hasKey("volume")) {
				soundVolume = nbt.getByte("volume");
			}
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		if(!host.world().isRemote) {
			NBTTagCompound processNBT = new NBTTagCompound();
			nbt.setTag("process", processNBT);
			process.save(processNBT);
			if(buildBuffer != null && !buildBuffer.isEmpty()) {
				NBTTagList buildNBT = new NBTTagList();
				synchronized(buildBuffer) {
					Instruction.toNBT(buildNBT, buildBuffer);
				}
				nbt.setTag("bbuffer", buildNBT);
			}
			if(nextBuffer != null && !nextBuffer.isEmpty()) {
				NBTTagList nextNBT = new NBTTagList();
				synchronized(nextBuffer) {
					Instruction.toNBT(nextNBT, nextBuffer);
				}
				nbt.setTag("nbuffer", nextNBT);
			}
			nbt.setByte("volume", (byte) soundVolume);
		}
	}

	protected void clearAndStop() {
		if(buildBuffer != null && !buildBuffer.isEmpty()) {
			synchronized(buildBuffer) {
				buildBuffer.clear();
			}
		}
		if(nextBuffer != null && !nextBuffer.isEmpty()) {
			synchronized(nextBuffer) {
				nextBuffer.clear();
			}
		}
		buildDelay = 0;
		if(codecId != null) {
			AudioUtils.removePlayer(Computronics.opencomputers.managerId, codecId);
			codecId = null;
		}
	}

	@Override
	public void onMessage(Message message) {
		if((message.name().equals("computer.stopped")
			|| message.name().equals("computer.started"))
			&& node().isNeighborOf(message.source())) {
			clearAndStop();
		}
	}

	private Object[] tryAdd(Instruction inst) {
		synchronized(buildBuffer) {
			if(buildBuffer.size() >= Config.SOUND_CARD_QUEUE_SIZE) {
				return new Object[] { false, "too many instructions" };
			}
			buildBuffer.add(inst);
		}
		return new Object[] { true };
	}

	private static HashMap<Object, Object> modes;

	private static HashMap<Object, Object> compileModes() {
		if(modes == null) {
			HashMap<Object, Object> modes = new HashMap<Object, Object>(AudioType.VALUES.length * 2);
			for(AudioType value : AudioType.VALUES) {
				String name = value.name().toLowerCase(Locale.ENGLISH);
				modes.put(value.ordinal() + 1, name);
				modes.put(name, value.ordinal() + 1);
			}
			// Adding white noise
			modes.put("noise", -1);
			modes.put(-1, "noise");
			DriverCardSound.modes = modes;
		}
		return modes;
	}

	protected int checkChannel(Arguments args, int index) {
		int channel = args.checkInteger(index) - 1;
		if(channel >= 0 && channel < process.states.size()) {
			return channel;
		}
		throw new IllegalArgumentException("invalid channel: " + (channel + 1));
	}

	protected int checkChannel(Arguments args) {
		return checkChannel(args, 0);
	}

	@Callback(doc = "This is a bidirectional table of all valid modes.", direct = true, getter = true)
	public Object[] modes(Context context, Arguments args) {
		return new Object[] { compileModes() };
	}

	@Callback(doc = "This is the number of channels this card provides.", direct = true, getter = true)
	public Object[] channel_count(Context context, Arguments args) {
		return new Object[] { process.states.size() };
	}

	@Callback(doc = "function(volume:number); Sets the general volume of the entire sound card to a value between 0 and 1. Not an instruction, this affects all channels directly.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setTotalVolume(Context context, Arguments args) {
		double volume = args.checkDouble(0);
		if(volume < 0.0F) {
			volume = 0.0F;
		}
		if(volume > 1.0F) {
			volume = 1.0F;
		}
		this.soundVolume = MathHelper.floor_double(volume * 127.0F);
		return new Object[] {};
	}

	@Callback(doc = "function(); Clears the instruction queue.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] clear(Context context, Arguments args) {
		synchronized(buildBuffer) {
			buildBuffer.clear();
		}
		buildDelay = 0;
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); Instruction; Opens the specified channel, allowing sound to be generated.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] open(Context context, Arguments args) {
		return tryAdd(new Open(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number); Instruction; Closes the specified channel, stopping sound from being generated.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] close(Context context, Arguments args) {
		return tryAdd(new Close(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, type:number); Instruction; Sets the wave type on the specified channel.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setWave(Context context, Arguments args) {
		int channel = checkChannel(args);
		int mode = args.checkInteger(1) - 1;
		switch(mode) {
			case -2:
				return tryAdd(new Instruction.SetWhiteNoise(channel));
			default:
				if(mode >= 0 && mode < AudioType.VALUES.length) {
					return tryAdd(new SetWave(channel, AudioType.fromIndex(mode)));
				}
		}
		throw new IllegalArgumentException("invalid mode: " + (mode + 1));
	}

	@Callback(doc = "function(channel:number, frequency:number); Instruction; Sets the frequency on the specified channel.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFrequency(Context context, Arguments args) {
		return tryAdd(new Instruction.SetFrequency(checkChannel(args), (float) args.checkDouble(1)));
	}

	@Callback(doc = "function(channel:number, initial:number, mask:number); Instruction; Makes the specified channel generate LFSR noise. Functions like a wave type.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setLFSR(Context context, Arguments args) {
		return tryAdd(new Instruction.SetLFSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2)));
	}

	@Callback(doc = "function(duration:number); Instruction; Adds a delay of the specified duration in milliseconds, allowing sound to generate.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] delay(Context context, Arguments args) {
		int duration = args.checkInteger(0);
		if(duration < 0 || duration > Config.SOUND_CARD_MAX_DELAY) {
			throw new IllegalArgumentException("invalid duration. must be between 0 and " + Config.SOUND_CARD_MAX_DELAY);
		}
		if(buildDelay + duration > Config.SOUND_CARD_MAX_DELAY) {
			return new Object[] { false, "too many delays in queue" };
		}
		buildDelay += duration;
		return tryAdd(new Delay(duration));
	}

	@Callback(doc = "function(channel:number, modIndex:number, intensity:number); Instruction; Assigns a frequency modulator channel to the specified channel with the specified intensity.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFM(Context context, Arguments args) {
		return tryAdd(new SetFM(checkChannel(args), checkChannel(args, 1), (float) args.checkDouble(2)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes the specified channel's frequency modulator.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetFM(Context context, Arguments args) {
		return tryAdd(new ResetFM(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, modIndex:number); Instruction; Assigns an amplitude modulator channel to the specified channel.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setAM(Context context, Arguments args) {
		return tryAdd(new SetAM(checkChannel(args), checkChannel(args, 1)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes the specified channel's amplitude modulator.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetAM(Context context, Arguments args) {
		return tryAdd(new ResetAM(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, attack:number, decay:number, attenuation:number, release:number); Instruction; Assigns ADSR to the specified channel with the specified phase durations in milliseconds and attenuation between 0 and 1.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setADSR(Context context, Arguments args) {
		return tryAdd(new SetADSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes ADSR from the specified channel.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetEnvelope(Context context, Arguments args) {
		return tryAdd(new ResetEnvelope(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, volume:number); Instruction; Sets the volume of the channel between 0 and 1.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		return tryAdd(new SetVolume(checkChannel(args), (float) args.checkDouble(1)));
	}

	@Callback(doc = "function(); Starts processing the queue; Returns true is processing began, false if there is still a queue being processed.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] process(Context context, Arguments args) {
		synchronized(buildBuffer) {
			if(nextBuffer != null && nextBuffer.isEmpty()) {
				if(buildBuffer.size() == 0) {
					return new Object[] { true };
				}
				if(!node.tryChangeBuffer(-Config.SOUND_CARD_ENERGY_COST * (buildDelay / 1000D))) {
					return new Object[] { false, "not enough energy" };
				}
				synchronized(nextBuffer) {
					nextBuffer.addAll(new ArrayDeque<Instruction>(buildBuffer));
				}
				nextDelay = buildDelay;
				buildBuffer.clear();
				buildDelay = 0;
				if(System.currentTimeMillis() > timeout) {
					timeout = System.currentTimeMillis();
				}
				return new Object[] { true };
			} else {
				return new Object[] { false, System.currentTimeMillis() - timeout };
			}
		}
	}

	private void sendMusicPacket(Queue<Instruction> instructions) {
		if(codecId == null) {
			codecId = Computronics.opencomputers.audio.newPlayer();
			Computronics.opencomputers.audio.getPlayer(codecId);
		}
		SoundCardPacket pkt = new SoundCardPacket(this, (byte) soundVolume, node().address(), instructions);
		internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);
		pkt.sendPacket();
	}

	protected void sendSound(Queue<Instruction> buffer) {
		Queue<Instruction> sendBuffer = new ArrayDeque<Instruction>();
		while(!buffer.isEmpty() || process.delay > 0) {
			if(process.delay > 0) {
				process.delay = 0;
			} else {
				Instruction inst = buffer.poll();
				inst.encounter(process);
				sendBuffer.add(inst);
			}
		}
		if(sendBuffer.size() > 0) {
			sendMusicPacket(sendBuffer);
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

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Audio interface",
			OCUtils.Vendors.Yanaki,
			"MinoSound 244-X"
		);
	}
}
