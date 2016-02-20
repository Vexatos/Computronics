package pl.asie.computronics.util.sound;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import pl.asie.computronics.util.sound.AudioUtil.AmplitudeModulation;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static pl.asie.computronics.util.sound.AudioUtil.ADSR;
import static pl.asie.computronics.util.sound.AudioUtil.FrequencyModulation;
import static pl.asie.computronics.util.sound.AudioUtil.Gate;
import static pl.asie.computronics.util.sound.AudioUtil.State;

/**
 * @author Vexatos
 */
public abstract class Instruction {

	public static class Open extends Instruction {

		@Override
		public void encounter(List<State> states, State state) {
			state.gate = Gate.Open;
		}
	}

	public static class Close extends Instruction implements Ticking {

		@Override
		public void encounter(List<State> states, State state) {
			state.gate = Gate.Closed;
		}
	}

	public static class SetWave extends Instruction {

		public final AudioType type;
		public final float frequency;

		public SetWave(AudioType type, float frequency) {
			this.type = type;
			this.frequency = frequency;
		}

		public SetWave(NBTTagCompound tag) {
			this.type = AudioType.fromIndex(tag.getByte("t"));
			this.frequency = tag.getFloat("f");
		}

		@Override
		public void encounter(List<State> states, State state) {
			state.wave.type = type;
			state.wave.frequencyInHz = frequency;
		}
	}

	public static class Delay extends Instruction implements Ticking {

		public final int delay;

		public Delay(int delay) {
			this.delay = delay;
		}

		public Delay(NBTTagCompound tag) {
			this.delay = tag.getInteger("d");
		}

		@Override
		public void encounter(List<State> states, State state) {
			state.delay = this.delay;
		}
	}

	public static class SetFM extends Instruction {

		private final FrequencyModulation freqMod;

		public SetFM(int modulatorIndex, float index) {
			this.freqMod = new FrequencyModulation(modulatorIndex, index);
		}

		public SetFM(NBTTagCompound tag) {
			this.freqMod = new FrequencyModulation(
				tag.getInteger("m"),
				tag.getFloat("i")
			);
		}

		@Override
		public void encounter(List<State> states, State state) {
			if(state.isFreqMod) {
				return;
			}
			if(state.freqMod != null) {
				State mstate = states.get(state.freqMod.modulatorIndex);
				if(mstate != null) {
					mstate.isFreqMod = false;
				}
			}
			State mstate = states.get(this.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = true;
				state.freqMod = this.freqMod;
			}
		}
	}

	public static class ResetFM extends Instruction {

		@Override
		public void encounter(List<State> states, State state) {
			if(state.freqMod == null) {
				return;
			}
			State mstate = states.get(state.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = false;
			}
			state.freqMod = null;
		}
	}

	public static class SetAM extends Instruction {

		private final AmplitudeModulation ampMod;

		public SetAM(int modulatorIndex, float index) {
			this.ampMod = new AmplitudeModulation(modulatorIndex);
		}

		public SetAM(NBTTagCompound tag) {
			this.ampMod = new AmplitudeModulation(tag.getInteger("m"));
		}

		@Override
		public void encounter(List<State> states, State state) {
			if(state.isAmpMod) {
				return;
			}
			if(state.ampMod != null) {
				State mstate = states.get(state.ampMod.modulatorIndex);
				if(mstate != null) {
					mstate.isAmpMod = false;
				}
			}
			State mstate = states.get(this.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = true;
				state.ampMod = this.ampMod;
			}
		}
	}

	public static class ResetAM extends Instruction {

		@Override
		public void encounter(List<State> states, State state) {
			if(state.ampMod == null) {
				return;
			}
			State mstate = states.get(state.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = false;
			}
			state.ampMod = null;
		}
	}

	public static class SetADSR extends Instruction {

		private final ADSR envelope;

		public SetADSR(int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			this.envelope = new ADSR(attackDuration, decayDuration, attenuation, releaseDuration);
		}

		public SetADSR(NBTTagCompound tag) {
			this.envelope = new ADSR(
				tag.getInteger("a"),
				tag.getInteger("d"),
				tag.getFloat("s"),
				tag.getInteger("r")
			);
		}

		@Override
		public void encounter(List<State> states, State state) {
			state.envelope = this.envelope;
		}
	}

	public static class ResetEnvelope extends Instruction {

		@Override
		public void encounter(List<State> states, State state) {
			state.envelope = null;
		}
	}

	public abstract void encounter(List<State> states, State state);

	public interface Ticking {

	}

	public static Instruction load(NBTTagCompound tag) {
		int type = tag.getInteger("t");
		switch(type) {
			case 0: {
				return new Open();
			}
			case 1: {
				return new Close();
			}
			case 2: {
				return new SetWave(tag.getCompoundTag("d"));
			}
			case 3: {
				return new Delay(tag.getCompoundTag("d"));
			}
			case 4: {
				return new SetFM(tag.getCompoundTag("d"));
			}
			case 5: {
				return new ResetFM();
			}
			case 6: {
				return new SetAM(tag.getCompoundTag("d"));
			}
			case 7: {
				return new ResetAM();
			}
			case 8: {
				return new SetADSR(tag.getCompoundTag("d"));
			}
			case 9: {
				return new ResetEnvelope();
			}
		}
		return null;
	}

	public static Queue<Instruction> fromNBT(NBTTagList l) {
		ArrayDeque<Instruction> instructions = new ArrayDeque<Instruction>();
		for(int i = 0; i < l.tagCount(); i++) {
			NBTTagCompound tag = l.getCompoundTagAt(i);
			if(!tag.hasNoTags()) {
				Instruction instr = load(tag);
				if(instr != null) {
					instructions.add(instr);
				}
			}
		}
		return instructions;
	}

}
