package pl.asie.computronics.util.sound;

import pl.asie.computronics.reference.Config;

import java.util.List;

/**
 * @author Vexatos
 */
public class AudioUtil {

	public enum Gate {
		Open {
			@Override
			public int getValue(List<State> states, State state, int baseAmplitude) {
				if(state.isAmpMod || state.isFreqMod) {
					return 0;
				}
				double value = state.wave.type.generate(state.wave.offset);
				if(state.freqMod != null) {
					value = state.freqMod.getModifiedValue(states, state, value);
				}
				if(state.ampMod != null) {
					value = state.ampMod.getModifiedValue(states, state, value);
				}
				if(state.envelope != null) {
					value = state.envelope.getModifiedValue(state, value);
				}
				return ((byte) (value * baseAmplitude)) ^ 0x80;
			}
		},
		Closed {
			@Override
			public int getValue(List<State> states, State state, int baseAmplitude) {
				return 0;
			}
		};

		public abstract int getValue(List<State> states, State state, int baseAmplitude);
	}

	public static abstract class Modulation {

		public abstract double getModifiedValue(List<State> states, State state, double value);
	}

	public static class FrequencyModulation extends Modulation {

		private final int modulatorIndex;
		private final int index;

		public FrequencyModulation(int modulatorIndex, int index) {
			this.modulatorIndex = modulatorIndex;
			this.index = index;
		}

		@Override
		public double getModifiedValue(List<State> states, State state, double value) {
			State mstate = states.get(modulatorIndex);
			if(mstate.gate == Gate.Closed) {
				return value;
			}
			Wave modulator = mstate.wave;
			Wave carrier = state.wave;
			modulator.offset += modulator.frequencyInHz / ((float) Config.SOUND_SAMPLE_RATE);
			if(modulator.offset > 1) {
				modulator.offset -= 1;
			}
			double deviation = modulator.type.generate(modulator.offset) * index * modulator.frequencyInHz;
			carrier.offset += (carrier.frequencyInHz + deviation) / ((float) Config.SOUND_SAMPLE_RATE);
			return value;
		}
	}

	public static class AmplitudeModulation extends Modulation {

		private final int modulatorIndex;

		public AmplitudeModulation(int modulatorIndex) {
			this.modulatorIndex = modulatorIndex;
		}

		@Override
		public double getModifiedValue(List<State> states, State state, double value) {
			State mstate = states.get(modulatorIndex);
			if(mstate.gate == Gate.Closed) {
				return value;
			}
			Wave modulator = mstate.wave;
			return value * (1 + modulator.type.generate(mstate.wave.offset));
		}
	}

	public static class ADSR {

		private int attackDuration;
		private int decayDuration;
		private float attenuation;
		private int releaseDuration;
		private Phase phase = Phase.Attack;
		private int progress;

		public ADSR(int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			this.attackDuration = Math.min(attackDuration * Config.SOUND_SAMPLE_RATE / 1000, 1);
			this.decayDuration = Math.min(decayDuration * Config.SOUND_SAMPLE_RATE / 1000, 1);
			this.attenuation = attenuation;
			this.releaseDuration = Math.min(releaseDuration * Config.SOUND_SAMPLE_RATE / 1000, 1);
		}

		public double getModifiedValue(State state, double value) {
			if(phase == null) {
				return 0;
			}
			if(state.gate == Gate.Closed && phase != Phase.Release) {
				phase = Phase.Release;
				progress = 0;
			}
			switch(phase) {
				case Attack: {
					value = value * (progress / attackDuration);
					if(++progress >= attackDuration) {
						nextPhase(state);
					}
					return value;
				}
				case Decay: {
					value = value * (((attenuation - 1) * (progress / decayDuration)) + 1);
					if(++progress >= decayDuration) {
						nextPhase(state);
					}
					return value;
				}
				case Sustain: {
					return value * attenuation;
				}
				case Release: {
					value = value * (((-attenuation) * (progress / releaseDuration)) + attenuation);
					if(++progress >= releaseDuration) {
						phase = null;
						progress = 0;
					}
					return value;
				}
			}
			return value;
		}

		private void nextPhase(State state) {
			phase = phase.next();
			progress = 0;
		}

		private enum Phase {
			Attack, Decay, Sustain, Release;

			public static final Phase[] VALUES = values();

			public Phase next() {
				int ordinal = ordinal();
				return ordinal < VALUES.length ? VALUES[ordinal + 1] : null;
			}
		}
	}

	public static class State {

		public final Wave wave;
		public final int channelIndex;
		public int delay = 0;

		public Gate gate = Gate.Closed;
		public FrequencyModulation freqMod;
		public AmplitudeModulation ampMod;
		public ADSR envelope;

		public boolean isFreqMod, isAmpMod;

		public State(Wave wave, int channelIndex) {
			this.wave = wave;
			this.channelIndex = channelIndex;
		}
	}

	public static class Wave {

		public float frequencyInHz;
		public float offset;
		public AudioType type;

		public Wave(float frequencyInHz, float offset, AudioType type) {
			this.frequencyInHz = frequencyInHz;
			this.offset = offset;
			this.type = type;
		}
	}
}
