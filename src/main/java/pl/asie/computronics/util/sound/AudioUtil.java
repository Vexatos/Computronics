package pl.asie.computronics.util.sound;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.reference.Config;

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
				double value = state.generator instanceof Noise ? ((Noise) state.generator).noiseOutput :
					state.generator instanceof Wave ? ((Wave) state.generator).type.generate(state.offset) : 0;
				if(state.freqMod != null && !state.isFreqMod) {
					value = state.freqMod.getModifiedValue(process, state, value);
				} else {
					state.offset += state.frequencyInHz / Config.SOUND_SAMPLE_RATE;
				}
				if(state.offset > 1) {
					state.offset %= 1.0F;
					if(state.generator instanceof Noise) {
						((Noise) state.generator).updateModifier(state);
					}
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

		public static final Gate[] VALUES = values();

		public static Gate fromIndex(int index) {
			return index > 0 && index < VALUES.length ? VALUES[index] : Closed;
		}
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
			double deviation = mstate.gate.getValue(process, mstate) * index * mstate.frequencyInHz;
			state.offset += (state.frequencyInHz + deviation) / Config.SOUND_SAMPLE_RATE;
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
			return value * (1 + mstate.gate.getValue(process, mstate));
		}
	}

	public static class ADSR {

		public int attackDuration;
		public int decayDuration;
		public float attenuation;
		public int releaseDuration;
		public Phase phase = Phase.Attack;
		public double progress = 0;
		private final Phase initialPhase;

		// Precalculated speeds
		private double attackSpeed;
		private double decaySpeed;
		private double releaseSpeed;

		public ADSR(int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			this.attackDuration = Math.max(attackDuration, 0);
			this.decayDuration = Math.max(decayDuration, 0);
			this.attenuation = Math.min(Math.max(attenuation, 0), 1);
			this.releaseDuration = Math.max(releaseDuration, 0);

			this.attackSpeed = 1000D / (this.attackDuration * Config.SOUND_SAMPLE_RATE);
			if(this.attackDuration == 0) {
				this.phase = Phase.Decay;
				this.initialPhase = Phase.Decay;
			} else {
				this.initialPhase = Phase.Attack;
			}
			this.decaySpeed = ((this.attenuation - 1D) * 1000D) / (this.decayDuration * Config.SOUND_SAMPLE_RATE);
			this.releaseSpeed = (-this.attenuation * 1000D) / (this.releaseDuration * Config.SOUND_SAMPLE_RATE);
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

			public static Phase fromIndex(int index) {
				return index > 0 && index < VALUES.length ? VALUES[index] : Attack;
			}
		}
	}

	public static abstract class Generator {

	}

	public static class Wave extends Generator {

		public AudioType type = AudioType.Square;

		public Wave() {

		}

		public Wave(AudioType type) {
			this.type = type;
		}
	}

	public static abstract class Noise extends Generator {

		public double noiseOutput = Math.random();

		public void updateModifier(State state) {
			this.noiseOutput = generate(state);
		}

		protected abstract double generate(State state);

		public static Noise load(NBTTagCompound nbt) {
			final Noise noise;
			switch(nbt.getByte("t")) {
				default:
					noise = new WhiteNoise();
					break;
				case 1:
					noise = new LFSR(nbt.getInteger("initial"), nbt.getInteger("period"), nbt.getInteger("mask"));
					break;
			}
			noise.noiseOutput = nbt.getDouble("o");
			return noise;
		}

		protected abstract void save(NBTTagCompound nbt);

	}

	public static class WhiteNoise extends Noise {

		@Override
		protected double generate(State state) {
			return Math.random();
		}

		@Override
		protected void save(NBTTagCompound nbt) {
			nbt.setByte("t", (byte) 0);
			nbt.setDouble("o", noiseOutput);
		}
	}

	public static class LFSR extends Noise {

		private final byte[] noise;
		private final int initial, period, mask;
		private int position = 0;

		public LFSR(int v, int period, int mask) {
			this.initial = v;
			this.period = period;
			this.mask = mask;
			this.noise = new byte[period];
			for(int i = 0; i < this.noise.length; ++i) {
				if((v & 1) != 0) {
					v = (v >>> 1) ^ mask;
					this.noise[i] = 1;
				} else {
					v >>>= 1;
					this.noise[i] = -1;
				}
			}
		}

		@Override
		protected double generate(State state) {
			int val = position++;
			position %= noise.length;
			return noise[val];
		}

		@Override
		protected void save(NBTTagCompound nbt) {
			nbt.setByte("t", (byte) 1);
			nbt.setDouble("o", noiseOutput);
			nbt.setInteger("initial", initial);
			nbt.setInteger("period", period);
			nbt.setInteger("mask", mask);
		}
	}

	public static class State {

		public Generator generator;
		public final int channelIndex;

		public float frequencyInHz;
		public float offset;
		public Gate gate = Gate.Closed;
		public FrequencyModulation freqMod;
		public AmplitudeModulation ampMod;
		public ADSR envelope;
		public float volume = 1;

		public boolean isFreqMod, isAmpMod;

		public State(int channelIndex) {
			this.generator = new Wave();
			this.channelIndex = channelIndex;
		}

		public void load(NBTTagCompound nbt) {
			if(nbt.hasKey("wavehz")) {
				frequencyInHz = nbt.getFloat("wavehz");
			}
			if(nbt.hasKey("offset")) {
				offset = nbt.getFloat("offset");
			}
			if(nbt.hasKey("type")) {
				this.generator = new Wave(AudioType.fromIndex(nbt.getInteger("type")));
			}
			if(nbt.hasKey("noise")) {
				this.generator = Noise.load(nbt.getCompoundTag("noise"));
			}
			if(nbt.hasKey("gate")) {
				gate = Gate.fromIndex(nbt.getInteger("gate"));
			}
			if(nbt.hasKey("fmodi") && nbt.hasKey("findex")) {
				freqMod = new FrequencyModulation(nbt.getInteger("fmodi"), nbt.getFloat("findex"));
			}
			if(nbt.hasKey("amodi")) {
				ampMod = new AmplitudeModulation(nbt.getInteger("amodi"));
			}
			if(nbt.hasKey("a")) {
				envelope = new ADSR(nbt.getInteger("a"), nbt.getInteger("d"), nbt.getFloat("s"), nbt.getInteger("r"));
				envelope.phase = ADSR.Phase.fromIndex(nbt.getInteger("p"));
				envelope.progress = nbt.getDouble("o");
			}
			if(nbt.hasKey("vol")) {
				volume = nbt.getFloat("vol");
			}

			if(nbt.hasKey("isfmod")) {
				isFreqMod = nbt.getBoolean("isfmod");
			}
			if(nbt.hasKey("isamod")) {
				isAmpMod = nbt.getBoolean("isamod");
			}
		}

		public void save(NBTTagCompound nbt) {
			nbt.setFloat("wavehz", frequencyInHz);
			nbt.setFloat("offset", offset);
			if(generator instanceof Wave && ((Wave) generator).type != null) {
				nbt.setInteger("type", ((Wave) generator).type.ordinal());
			}
			if(generator instanceof Noise) {
				NBTTagCompound data = new NBTTagCompound();
				((Noise) generator).save(data);
				nbt.setTag("noise", data);
			}
			nbt.setInteger("gate", gate.ordinal());
			if(freqMod != null) {
				nbt.setInteger("fmodi", freqMod.modulatorIndex);
				nbt.setFloat("findex", freqMod.index);
			}
			if(ampMod != null) {
				nbt.setInteger("amodi", ampMod.modulatorIndex);
			}
			if(envelope != null) {
				nbt.setInteger("a", envelope.attackDuration);
				nbt.setInteger("d", envelope.decayDuration);
				nbt.setFloat("s", envelope.attenuation);
				nbt.setInteger("r", envelope.releaseDuration);
				nbt.setInteger("p", envelope.phase.ordinal());
				nbt.setDouble("o", envelope.progress);
			}
			nbt.setFloat("vol", volume);
			nbt.setBoolean("isfmod", isFreqMod);
			nbt.setBoolean("isamod", isAmpMod);
		}
	}

	public static class AudioProcess {

		public final ImmutableList<State> states;
		public int delay = 0;

		public AudioProcess(int channelCount) {
			ArrayList<State> states = new ArrayList<State>(channelCount);
			for(int i = 0; i < channelCount; i++) {
				states.add(new AudioUtil.State(i));
			}
			this.states = ImmutableList.copyOf(states);
		}

		public void load(NBTTagCompound nbt) {
			for(int i = 0; i < states.size(); i++) {
				if(nbt.hasKey("ch" + i)) {
					states.get(i).load(nbt.getCompoundTag("ch" + i));
				}
			}
			if(nbt.hasKey("delay")) {
				delay = nbt.getInteger("delay");
			}
		}

		public void save(NBTTagCompound nbt) {
			for(int i = 0; i < states.size(); i++) {
				NBTTagCompound stateNBT = new NBTTagCompound();
				nbt.setTag("ch" + i, stateNBT);
				states.get(i).save(stateNBT);
			}
			nbt.setInteger("delay", delay);
		}
	}
}
