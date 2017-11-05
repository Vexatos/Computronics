package pl.asie.computronics.oc.driver;

import cpw.mods.fml.common.network.NetworkRegistry;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.sound.Audio;
import pl.asie.computronics.util.sound.AudioType;
import pl.asie.computronics.util.sound.Channel;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverCardNoise extends DriverCardSoundBase {

	public DriverCardNoise(EnvironmentHost host) {
		super(host, "play");
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("noise").
			withConnector().
			create());
		channels = new Channel[8];
		for(int i = 0; i < channels.length; i++) {
			channels[i] = new Channel();
		}
	}

	protected final Channel[] channels;
	protected Channel[] channelSendBuffer;

	@Override
	public void update() {
		super.update();
		if(channelSendBuffer != null) {
			try {
				sendSound(host.world(), host.xPosition(),
					host.yPosition(), host.zPosition(), channelSendBuffer);
			} catch(Exception e) {
				e.printStackTrace();
			}
			channelSendBuffer = null;
		}
	}

	@Override
	protected AudioType getMode(int channel) {
		return channel >= 0 && channel < channels.length ? channels[channel].type : AudioType.Square;
	}

	@Callback(doc = "This is the number of channels this card provides.", direct = true, getter = true)
	public Object[] channel_count(Context context, Arguments args) {
		return new Object[] { channels.length };
	}

	@Callback(doc = "function(channel:number):number; returns the current mode of the specified channel", direct = true)
	public Object[] getMode(Context context, Arguments args) {
		int channel = args.checkInteger(0) - 1;
		if(channel >= 0 && channel < channels.length) {
			return new Object[] { getMode(channel).ordinal() + 1 };
		}
		throw new IllegalArgumentException("invalid channel");
	}

	@Callback(doc = "function(channel:number, mode:number):boolean; Sets the audio mode of the specified channel.", direct = true)
	public Object[] setMode(Context context, Arguments args) {
		int channel = args.checkInteger(0) - 1;
		int mode = args.checkInteger(1) - 1;
		if(channel >= 0 && channel < channels.length) {
			if(mode >= 0 && mode < AudioType.VALUES.length) {
				channels[channel].type = AudioType.fromIndex(mode);
				return new Object[] { true };
			}
			throw new IllegalArgumentException("invalid mode");
		}
		throw new IllegalArgumentException("invalid channel");
	}

	@Callback(doc = "This is a bidirectional table of all valid modes.", direct = true, getter = true)
	public Object[] modes(Context context, Arguments args) {
		return new Object[] { compileModes() };
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
			DriverCardNoise.modes = modes;
		}
		return modes;
	}

	@Callback(doc = "function(channel:number, frequency:number, duration:number [, initialDelay:number]):boolean; Adds the frequency played with the duration to the channel's buffer, optionally with a delay before playing. Returns true on success.", direct = true, limit = 50)
	public Object[] add(Context context, Arguments args) {
		int index = args.checkInteger(0) - 1;
		if(index >= 0 && index < channels.length) {
			Channel channel = channels[index];
			if(channel.entries.size() < 8) {
				double frequency = args.checkDouble(1);
				if(frequency < 20 || frequency > 2000) {
					throw new IllegalArgumentException("invalid frequency, must be in [20, 2000]");
				}
				double duration = args.checkDouble(2);
				double delay = args.optDouble(3, 0);
				int durationInMilliseconds = Math.max(50, Math.min(5000, (int) (duration * 1000)));
				int delayInMilliseconds = Math.max(0, Math.min(16000, (int) (delay * 1000)));
				channel.addWaveEntry((float) frequency, durationInMilliseconds, delayInMilliseconds);
				return new Object[] { true };
			}
			throw new IllegalStateException("channel " + (index + 1) + " full");
		}
		throw new IllegalArgumentException("invalid channel");
	}

	@Callback(doc = "function(channel:number); Clears the buffer.", direct = true, limit = 50)
	public Object[] clear(Context context, Arguments args) {
		for(Channel channel : channels) {
			channel.entries.clear();
		}
		return new Object[] {};
	}

	@Callback(doc = "function():boolean; Starts processing the buffer and clears it. Returns true on success.", direct = true, limit = 10)
	public Object[] process(Context context, Arguments args) {
		if(channelSendBuffer != null) {
			return new Object[] { false, "already processing!" };
		}
		channelSendBuffer = new Channel[8];
		double longest = 0;
		final long currentTickTime = host.world().getTotalWorldTime();
		for(int i = 0; i < channels.length; i++) {
			Channel channel = channels[i];
			if(channel.entries.size() == 0) {
				continue;
			}
			channelSendBuffer[i] = new Channel();
			channelSendBuffer[i].type = channel.type;
			final long ticksRemaining = expirationList[i] == null ? 0 : expirationList[i];
			int totalDelay = 0;
			for(Channel.WaveEntry entry : channel.entries) {
				int ticksToAdd = (entry.initialDelay + entry.freqPair.duration) / 1000 * 20;
				int totalDelayTicks = totalDelay / 1000 * 20;
				if(currentTickTime + totalDelayTicks <= ticksRemaining) {
					continue;
				}
				expirationList[i] = currentTickTime + totalDelayTicks + ticksToAdd;
				channelSendBuffer[i].addWaveEntry(entry.freqPair.frequency, entry.freqPair.duration, entry.initialDelay + totalDelay);
				totalDelay += entry.initialDelay + entry.freqPair.duration;
			}
			longest = Math.max(longest, Math.max(50, Math.min(5000, (totalDelay / 1000D))));
		}
		Object[] error = tryConsumeEnergy(Config.BEEP_ENERGY_COST * getNonNullCount(channelSendBuffer) * longest, "process");
		if(error != null) {
			channelSendBuffer = null;
			return error;
		}
		for(Channel channel : channels) {
			channel.entries.clear();
		}
		return new Object[] { true };
	}

	@Callback(doc = "function():number; returns the amount of channels currently in use", direct = true, limit = 10)
	public Object[] getActiveChannels(Context context, Arguments args) {
		return new Object[] { getActiveChannelCount() };
	}

	@Callback(doc = "function():boolean; returns true if the card is not already processing a command", direct = true)
	public Object[] isReady(Context context, Arguments args) {
		return new Object[] { sendBuffer != null && channelSendBuffer != null };
	}

	@Callback(doc = "function(channels:table):boolean; table must have 8 or fewer entries. Each entry must be a table containing a frequency and a duration as values; plays each frequency for the specified duration. Returns true on success.", direct = true, limit = 1)
	public Object[] play(Context context, Arguments args) throws Exception {
		if(sendBuffer != null) {
			return new Object[] { false, "already processing" };
		}
		Map map = args.checkTable(0);
		if(map.size() > 8) {
			return new Object[] { false, "table must not contain more than 8 frequencies" };
		}
		Channel.FreqPair[] freqPairs = new Channel.FreqPair[8];
		double longest = 0.0;
		for(int channel = 1; channel <= 8; channel++) {
			Object freqDurPair = map.get(channel);
			if(freqDurPair == null) {
				continue;
			} else if(!(freqDurPair instanceof Map)) {
				throw new IllegalArgumentException("frequency-duration pair '" + String.valueOf(freqDurPair) + "' is not a table");
			}

			Map freqDurMap = (Map) freqDurPair;
			if(!freqDurMap.containsKey(1)) {
				continue;
			}
			Object freqObj = freqDurMap.get(1);
			if(!(freqObj instanceof Number)) {
				throw new IllegalArgumentException("frequency " + String.valueOf(freqObj) + " on channel " + String.valueOf(channel) + " is not a number");
			}
			//Object durObj = map.get(freqObj);
			Object durObj = freqDurMap.get(2);
			if(durObj != null && !(durObj instanceof Number)) {
				throw new IllegalArgumentException("duration '" + String.valueOf(durObj) + " on channel " + String.valueOf(channel) + " is not a number");
			}
			int frequency = ((Number) freqObj).intValue();
			if(frequency < 20 || frequency > 2000) {
				throw new IllegalArgumentException("invalid frequency, must be in [20, 2000]");
			}
			double duration = optDouble(durObj != null ? (Number) durObj : null, 0.1);
			int durationInMilliseconds = Math.max(50, Math.min(5000, (int) (duration * 1000)));
			longest = Math.max(longest, Math.max(50, Math.min(5000, (duration * 1000))));
			long time = host.world().getTotalWorldTime() + (long) (durationInMilliseconds / 1000 * 20);
			int index = channel - 1;
			if(expirationList[index] == null) {
				expirationList[index] = time;
				freqPairs[index] = new Channel.FreqPair(frequency, durationInMilliseconds);
			}
		}
		return tryQueueSound(freqPairs, new Object[] { true }, Config.BEEP_ENERGY_COST * getNonNullCount(freqPairs) * (longest / 1000D), playMethodName);
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		int[] iTypes = nbt.getIntArray("types");
		NBTTagList frequencies = nbt.getTagList("freqs", Constants.NBT.TAG_FLOAT);
		int[] durations = nbt.getIntArray("dur");
		int[] delays = nbt.getIntArray("delays");
		int size = min(iTypes.length, channels.length, frequencies.tagCount(), durations.length, delays.length);
		for(int i = 0; i < size; i++) {
			channels[i].type = AudioType.fromIndex(iTypes[i]);
			channels[i].entries.add(new Channel.WaveEntry(new Channel.FreqPair(frequencies.func_150308_e(i), durations[i]), delays[i]));
		}
	}

	protected static <T extends Comparable<? super T>> T min(T... vals) {
		T min = null;
		for(T val : vals) {
			if(val == null) {
				continue;
			}
			if(min == null || val.compareTo(min) < 0) {
				min = val;
			}
		}
		return min;
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		int[] iTypes = new int[channels.length];
		NBTTagList freqList = new NBTTagList();
		int[] durations = new int[channels.length];
		int[] delays = new int[channels.length];
		for(int i = 0; i < channels.length; i++) {
			iTypes[i] = channels[i].type.ordinal();
			for(Channel.WaveEntry entry : channels[i].entries) {
				freqList.appendTag(new NBTTagFloat(entry.freqPair.frequency));
				durations[i] = entry.freqPair.duration;
				delays[i] = entry.initialDelay;
			}
		}
		nbt.setIntArray("types", iTypes);
		nbt.setTag("freqs", freqList);
		nbt.setIntArray("dur", durations);
		nbt.setIntArray("delays", delays);
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Audio interface",
			OCUtils.Vendors.Yanaki,
			"SQ1289-4"
		);
	}

	protected void sendSound(World world, double x, double y, double z, Channel[] channels) throws Exception {
		final int size = Math.min(channels.length, 8);
		byte hits = 0;
		for(int i = 0; i < size; i++) {
			if(channels[i] != null) {
				hits |= 1 << i;
			}
		}
		Packet packet = Computronics.packet.create(PacketType.COMPUTER_NOISE.ordinal())
			.writeInt(world.provider.dimensionId)
			.writeFloat((float) x)
			.writeFloat((float) y)
			.writeFloat((float) z)
			.writeByte(hits);
		for(Channel channel : channels) {
			if(channel != null) {
				packet.writeByte((byte) channel.entries.size());
				for(int i = 0; i < channel.entries.size(); i++) {
					Channel.WaveEntry entry = channel.entries.get(i);
					if(entry != null && entry.freqPair != null) {
						packet
							.writeByte((byte) getMode(i).ordinal())
							.writeFloat(entry.freqPair.frequency)
							.writeShort((short) entry.freqPair.duration)
							.writeShort((short) entry.initialDelay);
					}
				}
			}
		}
		Computronics.packet.sendToAllAround(packet, new NetworkRegistry.TargetPoint(
			world.provider.dimensionId, x, y, z, Config.SOUND_RADIUS));
	}

	public static void onSound(Packet packet, EntityPlayer player) throws IOException {
		int dimension = packet.readInt();
		if(isInDimension(player, dimension)) {
			float x = packet.readFloat();
			float y = packet.readFloat();
			float z = packet.readFloat();
			int hits = packet.readUnsignedByte();
			for(int i = 0; i < 8; i++) {
				if(((hits >> i) & 1) == 1) {
					int entries = packet.readUnsignedByte();
					for(int j = 0; j < entries; j++) {
						AudioType type = AudioType.fromIndex(packet.readUnsignedByte());
						float frequency = packet.readFloat();
						short duration = packet.readShort();
						short initialDelay = packet.readShort();
						Audio.instance().play(x, y, z, type, frequency, duration & 0xFFFF, initialDelay);
					}
				}
			}
		}
	}

}
