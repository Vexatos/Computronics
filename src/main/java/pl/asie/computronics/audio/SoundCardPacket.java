package pl.asie.computronics.audio;

import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.util.sound.AudioUtil.ADSR;
import pl.asie.computronics.util.sound.Instruction;
import pl.asie.computronics.util.sound.Instruction.Close;
import pl.asie.computronics.util.sound.Instruction.Delay;
import pl.asie.computronics.util.sound.Instruction.Open;
import pl.asie.computronics.util.sound.Instruction.ResetAM;
import pl.asie.computronics.util.sound.Instruction.ResetEnvelope;
import pl.asie.computronics.util.sound.Instruction.ResetFM;
import pl.asie.computronics.util.sound.Instruction.SetADSR;
import pl.asie.computronics.util.sound.Instruction.SetAM;
import pl.asie.computronics.util.sound.Instruction.SetFM;
import pl.asie.computronics.util.sound.Instruction.SetFrequency;
import pl.asie.computronics.util.sound.Instruction.SetLFSR;
import pl.asie.computronics.util.sound.Instruction.SetVolume;
import pl.asie.computronics.util.sound.Instruction.SetWave;
import pl.asie.computronics.util.sound.Instruction.SetWhiteNoise;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.Queue;

/**
 * @author gamax92
 */
public class SoundCardPacket extends AudioPacket {

	public final String address;
	public final Queue<Instruction> instructions;

	public SoundCardPacket(IAudioSource source, byte volume, String address, Queue<Instruction> instructions) {
		super(source, volume);
		this.address = address;
		this.instructions = instructions;
	}

	@Override
	protected void writeData(Packet packet) throws IOException {
		packet
			.writeString(address)
			.writeInt(instructions.size());
		for(Instruction instruction : instructions) {
			if(instruction instanceof Open) {
				packet
					.writeByte((byte) 0)
					.writeByte((byte) ((Open) instruction).channelIndex);
			} else if(instruction instanceof Close) {
				packet
					.writeByte((byte) 1)
					.writeByte((byte) ((Close) instruction).channelIndex);
			} else if(instruction instanceof SetWave) {
				packet
					.writeByte((byte) 2)
					.writeByte((byte) ((SetWave) instruction).channelIndex)
					.writeInt(((SetWave) instruction).type.ordinal());
			} else if(instruction instanceof Delay) {
				packet
					.writeByte((byte) 3)
					.writeInt(((Delay) instruction).delay);
			} else if(instruction instanceof SetFM) {
				packet
					.writeByte((byte) 4)
					.writeByte((byte) ((SetFM) instruction).channelIndex)
					.writeInt(((SetFM) instruction).freqMod.modulatorIndex)
					.writeFloat(((SetFM) instruction).freqMod.index);
			} else if(instruction instanceof ResetFM) {
				packet
					.writeByte((byte) 5)
					.writeByte((byte) ((ResetFM) instruction).channelIndex);
			} else if(instruction instanceof SetAM) {
				packet
					.writeByte((byte) 6)
					.writeByte((byte) ((SetAM) instruction).channelIndex)
					.writeInt(((SetAM) instruction).ampMod.modulatorIndex);
			} else if(instruction instanceof ResetAM) {
				packet
					.writeByte((byte) 7)
					.writeByte((byte) ((ResetAM) instruction).channelIndex);
			} else if(instruction instanceof SetADSR) {
				ADSR envelope = ((SetADSR) instruction).envelope;
				packet
					.writeByte((byte) 8)
					.writeByte((byte) ((SetADSR) instruction).channelIndex)
					.writeInt(envelope.attackDuration)
					.writeInt(envelope.decayDuration)
					.writeFloat(envelope.attenuation)
					.writeInt(envelope.releaseDuration);
			} else if(instruction instanceof ResetEnvelope) {
				packet
					.writeByte((byte) 9)
					.writeByte((byte) ((ResetEnvelope) instruction).channelIndex);
			} else if(instruction instanceof SetVolume) {
				packet
					.writeByte((byte) 10)
					.writeByte((byte) ((SetVolume) instruction).channelIndex)
					.writeFloat(((SetVolume) instruction).volume);
			} else if(instruction instanceof SetFrequency) {
				packet
					.writeByte((byte) 11)
					.writeByte((byte) ((SetFrequency) instruction).channelIndex)
					.writeFloat(((SetFrequency) instruction).frequency);
			} else if(instruction instanceof SetWhiteNoise) {
				packet
					.writeByte((byte) 12)
					.writeByte((byte) ((SetWhiteNoise) instruction).channelIndex);
			} else if(instruction instanceof SetLFSR) {
				packet
					.writeByte((byte) 13)
					.writeByte((byte) ((SetLFSR) instruction).channelIndex)
					.writeInt(((SetLFSR) instruction).initial)
					.writeInt(((SetLFSR) instruction).mask);
			}
		}
	}
}
