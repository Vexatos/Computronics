package pl.asie.computronics.util.sound;

import com.google.common.collect.ImmutableList;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.sound.AudioUtil.ADSR;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class AudioUtil {

	public enum Gate {
		Open {
			@Override
			public double getValue(AudioProcess process, State state) {
				if(state.isAmpMod || state.isFreqMod) {
					return 0;
				}
				double value = state.wave.type == AudioType.Noise ? state.noiseOutput : state.wave.type.generate(state.wave.offset);
				state.wave.offset += state.wave.frequencyInHz / ((float) Config.SOUND_SAMPLE_RATE);
				if(state.wave.offset > 1) {
					state.wave.offset -= 1;
					if (state.wave.type == AudioType.Noise)
						state.noiseOutput = Math.random();
				}
				if(state.freqMod != null && !state.isFreqMod) {
					value = state.freqMod.getModifiedValue(process, state, value);
				}
				if(state.ampMod != null && !state.isAmpMod) {
					value = state.ampMod.getModifiedValue(process, state, value);
				}
				if(state.envelope != null) {
					value = state.envelope.getModifiedValue(state, value);
				}
				return value * state.volume;
			}
		},
		Closed {
			@Override
			public double getValue(AudioProcess process, State state) {
				if(state.envelope != null && state.envelope.phase != null) {
					return Open.getValue(process, state);
				}
				return 0;
			}
		};

		public abstract double getValue(AudioProcess process, State state);
	}

	public static abstract class Modulation {

		public abstract double getModifiedValue(AudioProcess process, State state, double value);
	}

	public static class FrequencyModulation extends Modulation {

		public final int modulatorIndex;
		public final float index;

		public FrequencyModulation(int modulatorIndex, float index) {
			this.modulatorIndex = modulatorIndex;
			this.index = index;
		}

		@Override
		public double getModifiedValue(AudioProcess process, State state, double value) {
			State mstate = process.states.get(modulatorIndex);
			if(mstate.gate == Gate.Closed) {
				return value;
			}
			Wave carrier = state.wave;
			double deviation = mstate.gate.getValue(process, mstate) * index * mstate.wave.frequencyInHz;
			carrier.offset += (carrier.frequencyInHz + deviation) / ((float) Config.SOUND_SAMPLE_RATE);
			return value;
		}
	}

	public static class AmplitudeModulation extends Modulation {

		public final int modulatorIndex;

		public AmplitudeModulation(int modulatorIndex) {
			this.modulatorIndex = modulatorIndex;
		}

		@Override
		public double getModifiedValue(AudioProcess process, State state, double value) {
			State mstate = process.states.get(modulatorIndex);
			if(mstate.gate == Gate.Closed) {
				return value;
			}
			return value * (1 + mstate.gate.getValue(process, mstate));
		}
	}

	public static class ADSR {

		private double attackSpeed;
		private double decaySpeed;
		private float attenuation;
		private double releaseSpeed;
		private Phase phase = Phase.Attack;
		private final Phase initialPhase;
		private double progress = 0;

		public ADSR(int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			this.attenuation = Math.min(Math.max(attenuation, 0), 1);
			this.attackSpeed = 1000D / (double) (Math.max(attackDuration, 0) * Config.SOUND_SAMPLE_RATE);
			if(attackDuration == 0) {
				this.phase = Phase.Decay;
				this.initialPhase = Phase.Decay;
			} else {
				this.initialPhase = Phase.Attack;
			}
			this.decaySpeed = ((this.attenuation - 1D) * 1000D) / (double) (Math.max(decayDuration, 0) * Config.SOUND_SAMPLE_RATE);
			this.releaseSpeed = (-this.attenuation * 1000D) / (double) (Math.max(releaseDuration, 0) * Config.SOUND_SAMPLE_RATE);
		}

		public double getModifiedValue(State state, double value) {
			if(phase == null) {
				return 0;
			}
			if(state.gate == Gate.Closed && phase != Phase.Release) {
				phase = Phase.Release;
			}
			switch(phase) {
				case Attack: {
					// value = value * (progress / (double) attackSpeed);
					if((progress += attackSpeed) >= 1) {
						progress = 1;
						nextPhase();
					}
					break;
				}
				case Decay: {
					// value = value * (1 + ((attenuation - 1) * (progress / (double) decaySpeed)));
					if((progress += decaySpeed) <= attenuation) {
						progress = attenuation;
						nextPhase();
					}
					break;
				}
				case Release: {
					// value = value * (attenuation * (1 - (progress / (double) releaseSpeed)));
					if((progress += releaseSpeed) <= 0) {
						phase = null;
						progress = 0;
					}
					break;
				}
			}
			value *= progress;
			return value;
		}

		private void nextPhase() {
			phase = phase.next();
		}

		public void reset() {
			phase = this.initialPhase;
		}

		private enum Phase {
			Attack,
			Decay,
			Sustain,
			Release;

			public static final Phase[] VALUES = values();

			public Phase next() {
				int ordinal = ordinal();
				return ordinal < VALUES.length ? VALUES[ordinal + 1] : null;
			}
		}

		public void copy(ADSR envelope) {
			this.progress = envelope.progress;
			this.phase = envelope.phase;
		}
	}

	public static class State {

		public final Wave wave;
		public final int channelIndex;

		public Gate gate = Gate.Closed;
		public FrequencyModulation freqMod;
		public AmplitudeModulation ampMod;
		public ADSR envelope;
		public float volume = 1;
		public double noiseOutput = Math.random();

		public boolean isFreqMod, isAmpMod;

		public State(int channelIndex) {
			this.wave = new Wave();
			this.channelIndex = channelIndex;
		}
	}

	public static class Wave {

		public float frequencyInHz;
		public float offset;
		public AudioType type;

		public Wave() {

		}

		public Wave(float frequencyInHz, float offset, AudioType type) {
			this.frequencyInHz = frequencyInHz;
			this.offset = offset;
			this.type = type;
		}
	}

	public static class AudioProcess {

		public final ImmutableList<State> states;
		public int delay = 0;

		public AudioProcess(int channelCount) {
			ArrayList<State> states = new ArrayList<State>(channelCount);
			for(int i = 0; i < 8; i++) {
				states.add(new AudioUtil.State(i));
			}
			this.states = ImmutableList.copyOf(states);
		}
	}
}
