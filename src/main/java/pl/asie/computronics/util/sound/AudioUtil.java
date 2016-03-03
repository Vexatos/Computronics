package pl.asie.computronics.util.sound;

import com.google.common.collect.ImmutableList;
import pl.asie.computronics.reference.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class AudioUtil {

	public enum Gate {
		Open {
			@Override
			public double getValue(AudioState process, State state) {
				if(state.isAmpMod || state.isFreqMod) {
					return 0;
				}
				double value = state.wave.type.generate(state.wave.offset);
				state.wave.offset += state.wave.frequencyInHz / ((float) Config.SOUND_SAMPLE_RATE);
				if(state.wave.offset > 1) {
					state.wave.offset -= 1;
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
				return value;
			}
		},
		Closed {
			@Override
			public double getValue(AudioState process, State state) {
				return 0;
			}
		};

		public abstract double getValue(AudioState process, State state);
	}

	public static abstract class Modulation {

		public abstract double getModifiedValue(AudioState process, State state, double value);
	}

	public static class FrequencyModulation extends Modulation {

		public final int modulatorIndex;
		public final float index;

		public FrequencyModulation(int modulatorIndex, float index) {
			this.modulatorIndex = modulatorIndex;
			this.index = index;
		}

		@Override
		public double getModifiedValue(AudioState process, State state, double value) {
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
		public double getModifiedValue(AudioState process, State state, double value) {
			State mstate = process.states.get(modulatorIndex);
			if(mstate.gate == Gate.Closed) {
				return value;
			}
			Wave modulator = mstate.wave;
			return value * (1 + mstate.gate.getValue(process, mstate));
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
	}

	public static class State {

		public final Wave wave;
		public final int channelIndex;

		public Gate gate = Gate.Closed;
		public FrequencyModulation freqMod;
		public AmplitudeModulation ampMod;
		public ADSR envelope;

		public boolean isFreqMod, isAmpMod;
		public final List<Byte> data = new ArrayList<Byte>();

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

	public static class AudioState {

		public final ImmutableList<State> states;
		public int delay = 0;

		public AudioState(int channelCount) {
			ArrayList<State> states = new ArrayList<State>(channelCount);
			for(int i = 0; i < 8; i++) {
				states.add(new AudioUtil.State(i));
			}
			this.states = ImmutableList.copyOf(states);
		}
	}
}
