package pl.asie.computronics.util.sound;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.audio.SoundCardPacketClientHandler;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.sound.AudioUtil.AudioProcess;
import pl.asie.computronics.util.sound.Instruction.Delay;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Vexatos
 */
public class SoundBoard {

	public SoundBoard(ISoundHost host) {
		process = new AudioProcess(Config.SOUND_CARD_CHANNEL_COUNT);
		this.host = host;
		initBuffers();
	}

	private ISoundHost host;
	public AudioProcess process;
	private ArrayDeque<Instruction> buildBuffer;
	private ArrayDeque<Instruction> nextBuffer;

	private int buildDelay = 0;
	private int nextDelay = 0;
	private long timeout = System.currentTimeMillis();
	private int soundVolume = 127;
	public Integer codecId;
	private String clientAddress = null;

	private final int soundTimeoutMS = 250;

	public static class SyncHandler {

		static Set<SoundBoard> envs = Collections.newSetFromMap(new WeakHashMap<SoundBoard, Boolean>());

		@SideOnly(Side.CLIENT)
		private static SoundCardPacketClientHandler getHandler() {
			return (SoundCardPacketClientHandler) AudioPacketRegistry.INSTANCE.getClientHandler(AudioPacketRegistry.INSTANCE.getId(SoundCardPacket.class));
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload evt) {
			for(SoundBoard env : envs) {
				Vec3d pos = env.host.position();
				if(env.host.world() == evt.getWorld() && evt.getChunk().isAtLocation(MathHelper.floor(pos.x) >> 4, MathHelper.floor(pos.z) >> 4)) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload evt) {
			for(SoundBoard env : envs) {
				if(env.host.world() == evt.getWorld()) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}
	}

	private boolean bufferInit = false;

	private void initBuffers() {
		if(bufferInit) {
			return;
		}
		World world = host.world();
		if(world == null) {
			return;
		}
		if(world.isRemote) {
			SyncHandler.envs.add(this);
			buildBuffer = null;
			nextBuffer = null;
			if(clientAddress != null) {
				SyncHandler.getHandler().setProcess(clientAddress, process);
			}
		} else {
			buildBuffer = new ArrayDeque<Instruction>();
			nextBuffer = new ArrayDeque<Instruction>();
		}
		bufferInit = true;
	}

	private boolean dirty = false;

	public void update() {
		initBuffers();
		if(!host.world().isRemote) {
			if(nextBuffer != null && !nextBuffer.isEmpty() && System.currentTimeMillis() >= timeout - 100) {
				final ArrayDeque<Instruction> clone;
				synchronized(nextBuffer) {
					clone = nextBuffer.clone();
					timeout = timeout + nextDelay;
					nextBuffer.clear();
				}
				sendSound(clone);
				dirty = true;
			} else if(codecId != null && System.currentTimeMillis() >= timeout + soundTimeoutMS) {
				AudioUtils.removePlayer(Computronics.instance.soundCardManagerId, codecId);
				codecId = null;
			}
			if(dirty) {
				host.setDirty();
			}
		}
	}

	public void load(NBTTagCompound nbt) {
		if(nbt.hasKey("process")) {
			process.load(nbt.getCompoundTag("process"));
		}
		if(nbt.hasKey("node")) {
			NBTTagCompound nodeNBT = nbt.getCompoundTag("node");
			if(nodeNBT.hasKey("address")) {
				clientAddress = nodeNBT.getString("address");
			}
		}
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

	public void save(NBTTagCompound nbt) {
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

	public void clearAndStop() {
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
			AudioUtils.removePlayer(Computronics.instance.soundCardManagerId, codecId);
			codecId = null;
		}
		dirty = true;
	}

	public Object[] tryAdd(Instruction inst) {
		synchronized(buildBuffer) {
			if(buildBuffer.size() >= Config.SOUND_CARD_QUEUE_SIZE) {
				return new Object[] { false, "too many instructions" };
			}
			buildBuffer.add(inst);
		}
		dirty = true;
		return new Object[] { true };
	}

	private static HashMap<Object, Object> modes;

	public static HashMap<Object, Object> compileModes() {
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
			SoundBoard.modes = modes;
		}
		return modes;
	}

	public int checkChannel(int channel) {
		channel--;
		if(channel >= 0 && channel < process.states.size()) {
			return channel;
		}
		throw new IllegalArgumentException("invalid channel: " + (channel + 1));
	}

	public void clear() {
		synchronized(buildBuffer) {
			buildBuffer.clear();
		}
		buildDelay = 0;
		dirty = true;
	}

	public Object[] delay(int duration) {
		if(duration < 0 || duration > Config.SOUND_CARD_MAX_DELAY) {
			throw new IllegalArgumentException("invalid duration. must be between 0 and " + Config.SOUND_CARD_MAX_DELAY);
		}
		if(buildDelay + duration > Config.SOUND_CARD_MAX_DELAY) {
			return new Object[] { false, "too many delays in queue" };
		}
		buildDelay += duration;
		return tryAdd(new Delay(duration));
	}

	public void setTotalVolume(double volume) {
		if(volume < 0.0F) {
			volume = 0.0F;
		}
		if(volume > 1.0F) {
			volume = 1.0F;
		}
		soundVolume = MathHelper.floor(volume * 127.0F);
	}

	public Object[] setWave(int channel, int mode) {
		channel = checkChannel(channel);
		mode--;
		switch(mode) {
			case -2:
				return tryAdd(new Instruction.SetWhiteNoise(channel));
			default:
				if(mode >= 0 && mode < AudioType.VALUES.length) {
					return tryAdd(new Instruction.SetWave(channel, AudioType.fromIndex(mode)));
				}
		}
		throw new IllegalArgumentException("invalid mode: " + (mode + 1));
	}

	public Object[] process() {
		synchronized(buildBuffer) {
			if(nextBuffer != null && nextBuffer.isEmpty()) {
				if(buildBuffer.size() == 0) {
					return new Object[] { true };
				}
				if(!host.tryConsumeEnergy(Config.SOUND_CARD_ENERGY_COST * (buildDelay / 1000D))) {
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
				dirty = true;
				return new Object[] { true };
			} else {
				return new Object[] { false, System.currentTimeMillis() - timeout };
			}
		}
	}

	private void sendMusicPacket(Queue<Instruction> instructions) {
		if(codecId == null) {
			codecId = Computronics.instance.soundCardAudio.newPlayer();
			Computronics.instance.soundCardAudio.getPlayer(codecId);
		}
		SoundCardPacket pkt = new SoundCardPacket(host, (byte) soundVolume, host.address(), instructions);
		host.sendMusicPacket(pkt);
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

	public interface ISoundHost extends IAudioSource {

		World world();

		boolean tryConsumeEnergy(double energy);

		String address();

		Vec3d position();

		void sendMusicPacket(SoundCardPacket pkt);

		void setDirty();
	}

}
