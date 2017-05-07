package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.sound.AudioType;
import pl.asie.computronics.util.sound.Channel;

import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverCardBeep extends DriverCardSoundBase {

	public DriverCardBeep(EnvironmentHost host) {
		super(host, "beep");
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("beep").
			withConnector().
			create());
	}

	@Override
	protected AudioType getMode(int channel) {
		return AudioType.Square;
	}

	@Callback(doc = "function():number; returns the amount of beeps currently being played", direct = true, limit = 10)
	public Object[] getBeepCount(Context context, Arguments args) {
		return new Object[] { getActiveChannelCount() };
	}

	@Callback(doc = "function(frequencyDurationTable:table):boolean; table needs to contain frequency-duration pairs; plays each frequency for the specified duration. Returns true on success.", direct = true, limit = 10)
	public Object[] beep(Context context, Arguments args) throws Exception {
		Map map = args.checkTable(0);
		if(map.size() > 8) {
			return new Object[] { false, "table must not contain more than 8 frequencies" };
		}
		if(getActiveChannelCount() + map.size() > 8) {
			return new Object[] { false, "already too many sounds playing, maximum is 8" };
		}
		Channel.FreqPair[] freqPairs = new Channel.FreqPair[8];
		double longest = 0.0;
		for(Object entryObj : map.entrySet()) {
			if(entryObj instanceof Map.Entry) {
				Object freqObj = ((Map.Entry) entryObj).getKey();
				if(!(freqObj instanceof Number)) {
					throw new IllegalArgumentException("frequency '" + String.valueOf(freqObj) + "' is not a number");
				}
				//Object durObj = map.get(freqObj);
				Object durObj = ((Map.Entry) entryObj).getValue();
				if(durObj != null && !(durObj instanceof Number)) {
					throw new IllegalArgumentException("duration '" + String.valueOf(durObj) + "' is not a number");
				}
				double frequency = ((Number) freqObj).doubleValue();
				if(frequency < 20 || frequency > 2000) {
					throw new IllegalArgumentException("invalid frequency, must be in [20, 2000]");
				}
				double duration = optDouble(durObj != null ? (Number) durObj : null, 0.1);
				int durationInMilliseconds = Math.max(50, Math.min(5000, (int) (duration * 1000)));
				longest = Math.max(longest, Math.max(50, Math.min(5000, (duration * 1000))));
				long time = host.world().getTotalWorldTime() + (long) (durationInMilliseconds / 1000 * 20);
				for(int i = 0; i < expirationList.length; i++) {
					if(expirationList[i] == null) {
						expirationList[i] = time;
						freqPairs[i] = new Channel.FreqPair((float) frequency, durationInMilliseconds);
						break;
					}
				}
			}
		}
		return tryQueueSound(freqPairs, new Object[] { true }, Config.BEEP_ENERGY_COST * getNonNullCount(freqPairs) * (longest / 1000D), playMethodName);
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Audio interface",
			OCUtils.Vendors.Yanaki,
			"SQ532"
		);
	}
}
