package pl.asie.computronics.util.sound;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import pl.asie.computronics.util.sound.AudioUtil.State;

import java.util.ArrayDeque;
import java.util.Queue;

import static pl.asie.computronics.util.sound.AudioUtil.Gate;

/**
 * @author Vexatos
 */
public abstract class Instruction {

	public static class Open extends Instruction {

		@Override
		public void encounter(State state) {
			state.gate = Gate.Open;
		}
	}

	public static class Close extends Instruction implements Ticking {

		@Override
		public void encounter(State state) {
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

		@Override
		public void encounter(State state) {
			state.wave.type = type;
			state.wave.frequencyInHz = frequency;
		}
	}

	public static class Delay extends Instruction implements Ticking {

		@Override
		public void encounter(State state) {

		}
	}

	public static class SetFM extends Instruction {

		@Override
		public void encounter(State state) {

		}
	}

	public static class ResetFM extends Instruction {

	}

	public static class SetAM extends Instruction {

	}

	public static class ResetAM extends Instruction {

	}

	public static class SetADSR extends Instruction {

	}

	public static class ResetEnvelope extends Instruction {

	}

	public abstract void encounter(State state);

	public interface Ticking {

	}

	public static Instruction load(NBTTagCompound tag) {
		int type = tag.getInteger("type");
		switch(type) {
			case 0: {
				return new Open();
			}
			case 1: {
				return new Close();
			}
			case 2: {
				return new SetWave();
			}
			case 3: {
				return new Delay();
			}
			case 4: {
				return new SetFM();
			}
			case 5: {
				return new ResetFM();
			}
			case 6: {
				return new SetAM();
			}
			case 7: {
				return new ResetAM();
			}
			case 8: {
				return new SetADSR();
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
