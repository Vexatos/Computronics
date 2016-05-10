package pl.asie.computronics.util.sound;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import pl.asie.computronics.util.sound.AudioUtil.AmplitudeModulation;
import pl.asie.computronics.util.sound.AudioUtil.AudioProcess;

import java.util.ArrayDeque;
import java.util.Queue;

import static pl.asie.computronics.util.sound.AudioUtil.ADSR;
import static pl.asie.computronics.util.sound.AudioUtil.FrequencyModulation;
import static pl.asie.computronics.util.sound.AudioUtil.Gate;
import static pl.asie.computronics.util.sound.AudioUtil.State;

/**
 * @author Vexatos
 */
public abstract class Instruction {

	public static class Open extends ChannelSpecific {

		public Open(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.gate = Gate.Open;
			if(state.envelope != null) {
				state.envelope.reset();
			}
		}
	}

	public static class Close extends ChannelSpecific {

		public Close(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.gate = Gate.Closed;
		}
	}

	public static class SetWave extends ChannelSpecific {

		public final AudioType type;
		public final float frequency;

		public SetWave(int channelIndex, AudioType type, float frequency) {
			super(channelIndex);
			this.type = type;
			this.frequency = frequency;
		}

		public SetWave(NBTTagCompound tag) {
			super(tag.getInteger("c"));
			this.type = AudioType.fromIndex(tag.getByte("t"));
			this.frequency = tag.getFloat("f");
		}

		@Override
		public void encounter(AudioProcess process, State state) {
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
		public void encounter(AudioProcess process) {
			process.delay = this.delay;
		}
	}

	public static class SetFM extends ChannelSpecific {

		public final FrequencyModulation freqMod;

		public SetFM(int channelIndex, int modulatorIndex, float index) {
			super(channelIndex);
			this.freqMod = new FrequencyModulation(modulatorIndex, index);
		}

		public SetFM(NBTTagCompound tag) {
			super(tag.getInteger("c"));
			this.freqMod = new FrequencyModulation(
				tag.getInteger("m"),
				tag.getFloat("i")
			);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.isFreqMod) {
				return;
			}
			if(state.freqMod != null) {
				State mstate = process.states.get(state.freqMod.modulatorIndex);
				if(mstate != null) {
					mstate.isFreqMod = false;
				}
			}
			State mstate = process.states.get(this.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = true;
				state.freqMod = this.freqMod;
			}
		}
	}

	public static class ResetFM extends ChannelSpecific {

		public ResetFM(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.freqMod == null) {
				return;
			}
			State mstate = process.states.get(state.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = false;
			}
			state.freqMod = null;
		}
	}

	public static class SetAM extends ChannelSpecific {

		public final AmplitudeModulation ampMod;

		public SetAM(int channelIndex, int modulatorIndex) {
			super(channelIndex);
			this.ampMod = new AmplitudeModulation(modulatorIndex);
		}

		public SetAM(NBTTagCompound tag) {
			super(tag.getInteger("c"));
			this.ampMod = new AmplitudeModulation(tag.getInteger("m"));
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.isAmpMod) {
				return;
			}
			if(state.ampMod != null) {
				State mstate = process.states.get(state.ampMod.modulatorIndex);
				if(mstate != null) {
					mstate.isAmpMod = false;
				}
			}
			State mstate = process.states.get(this.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = true;
				state.ampMod = this.ampMod;
			}
		}
	}

	public static class ResetAM extends ChannelSpecific {

		public ResetAM(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.ampMod == null) {
				return;
			}
			State mstate = process.states.get(state.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = false;
			}
			state.ampMod = null;
		}
	}

	public static class SetADSR extends ChannelSpecific {

		private final ADSR envelope;
		public int attackDuration;
		public int decayDuration;
		public float attenuation;
		public int releaseDuration;

		public SetADSR(int channelIndex, int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			super(channelIndex);
			this.envelope = new ADSR(attackDuration, decayDuration, attenuation, releaseDuration);
			this.attackDuration = attackDuration;
			this.decayDuration = decayDuration;
			this.attenuation = attenuation;
			this.releaseDuration = releaseDuration;
		}

		public SetADSR(NBTTagCompound tag) {
			super(tag.getInteger("c"));
			this.envelope = new ADSR(
				tag.getInteger("a"),
				tag.getInteger("d"),
				tag.getFloat("s"),
				tag.getInteger("r")
			);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if (state.envelope != null)
				this.envelope.copy(state.envelope);
			state.envelope = this.envelope;
		}
	}

	public static class ResetEnvelope extends ChannelSpecific {

		public ResetEnvelope(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.envelope = null;
		}
	}

	public static class SetVolume extends ChannelSpecific {

		public final float volume;

		public SetVolume(int channelIndex, float volume) {
			super(channelIndex);
			this.volume = Math.min(Math.max(volume, 0), 1);
		}

		public SetVolume(NBTTagCompound tag) {
			super(tag.getInteger("c"));
			this.volume = tag.getFloat("v");
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.volume = this.volume;
		}
	}

	public static abstract class ChannelSpecific extends Instruction {

		public final int channelIndex;

		public ChannelSpecific(int channelIndex) {
			this.channelIndex = channelIndex;
		}

		@Override
		public final void encounter(AudioProcess process) {
			this.encounter(process, process.states.get(this.channelIndex));
		}

		public abstract void encounter(AudioProcess process, State state);
	}

	public abstract void encounter(AudioProcess process);

	public interface Ticking {

	}

	public static Instruction load(NBTTagCompound tag) {
		int type = tag.getInteger("t");
		switch(type) {
			case 0: {
				return new Open(tag.getInteger("c"));
			}
			case 1: {
				return new Close(tag.getInteger("c"));
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
				return new ResetFM(tag.getInteger("c"));
			}
			case 6: {
				return new SetAM(tag.getCompoundTag("d"));
			}
			case 7: {
				return new ResetAM(tag.getInteger("c"));
			}
			case 8: {
				return new SetADSR(tag.getCompoundTag("d"));
			}
			case 9: {
				return new ResetEnvelope(tag.getInteger("c"));
			}
			case 10: {
				return new SetVolume(tag.getCompoundTag("d"));
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
