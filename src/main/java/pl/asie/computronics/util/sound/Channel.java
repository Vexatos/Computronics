package pl.asie.computronics.util.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class Channel {

	public final List<WaveEntry> entries = new ArrayList<WaveEntry>(8);
	public AudioType type = AudioType.Square;

	public Channel() {

	}

	public void addWaveEntry(float frequency, int durationInMilliseconds, int delayInMilliseconds) {
		if(entries.size() < 8) {
			entries.add(new WaveEntry(new FreqPair(frequency, durationInMilliseconds), delayInMilliseconds));
		}
	}

	public static class WaveEntry {

		public final FreqPair freqPair;
		public final int initialDelay;

		public WaveEntry(FreqPair freqPair, int initialDelay) {
			this.freqPair = freqPair;
			this.initialDelay = initialDelay;
		}
	}

	public static class FreqPair {

		public final float frequency;
		public final int duration;

		public FreqPair(float frequency, int duration) {
			this.frequency = frequency;
			this.duration = duration;
		}
	}
}
