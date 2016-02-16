package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.beep.AudioType;

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
			withConnector(Config.SOUND_ENERGY_COST * 42).
			create());
		types = new AudioType[8];
		for(int i = 0; i < types.length; i++) {
			types[i] = AudioType.Square;
		}
	}

	protected final AudioType[] types;

	@Override
	protected AudioType getMode(int channel) {
		return channel >= 0 && channel < types.length ? types[channel] : AudioType.Square;
	}

	@Callback(doc = "function(channel:number):number; returns the current mode of the specified channel", direct = true)
	public Object[] getMode(Context context, Arguments args) {
		int channel = args.checkInteger(0) - 1;
		if(channel >= 0 && channel < types.length) {
			return new Object[] { getMode(channel).ordinal() + 1 };
		}
		throw new IllegalArgumentException("invalid channel");
	}

	@Callback(doc = "function(channel:number, mode:number):boolean; Sets the audio mode of the specified channel.", direct = true)
	public Object[] setMode(Context context, Arguments args) {
		int channel = args.checkInteger(0) - 1;
		int mode = args.checkInteger(1) - 1;
		if(channel >= 0 && channel < types.length) {
			if(mode >= 0 && mode < AudioType.VALUES.length) {
				types[channel] = AudioType.fromIndex(mode);
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

	@Callback(doc = "function():number; returns the amount of channels currently in use", direct = true, limit = 10)
	public Object[] getActiveChannels(Context context, Arguments args) {
		return new Object[] { getActiveChannelCount() };
	}

	@Callback(doc = "function():boolean; returns true if the card is not already processing a command", direct = true)
	public Object[] isReady(Context context, Arguments args) {
		return new Object[] { sendBuffer != null };
	}

	@Callback(doc = "function(channels:table):boolean; table must have 8 or fewer entries. Each entry must be a table containing a frequency and a duration as values; plays each frequency for the specified duration. Returns true on success.", direct = true, limit = 10)
	public Object[] play(Context context, Arguments args) throws Exception {
		if(sendBuffer != null) {
			return new Object[] { false, "already processing" };
		}
		Map map = args.checkTable(0);
		if(map.size() > 8) {
			return new Object[] { false, "table must not contain more than 8 frequencies" };
		}
		FreqPair[] freqPairs = new FreqPair[8];
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
				freqPairs[index] = new FreqPair(frequency, durationInMilliseconds);
			}
		}
		return tryQueueSound(freqPairs, new Object[] { true }, Config.SOUND_ENERGY_COST * getNonNullCount(freqPairs) * (longest / 1000D), playMethodName);
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(nbt.hasKey("types")) {
			int[] iTypes = nbt.getIntArray("types");
			int size = Math.min(iTypes.length, types.length);
			for(int i = 0; i < size; i++) {
				types[i] = AudioType.fromIndex(iTypes[i]);
			}
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		int[] iTypes = new int[types.length];
		for(int i = 0; i < types.length; i++) {
			iTypes[i] = types[i].ordinal();
		}
		nbt.setIntArray("types", iTypes);
	}
}
