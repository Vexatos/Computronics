package pl.asie.computronics.audio;

import java.io.IOException;
import java.util.Queue;

import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.util.sound.Instruction;
import pl.asie.computronics.util.sound.Instruction.*;
import pl.asie.lib.network.Packet;

/**
 * @author gamax92
 */
public class SoundCardPacket extends AudioPacket {
	public final String address;
	public final long delay;
	public final Queue<Instruction> instructions;

	public SoundCardPacket(IAudioSource source, byte volume, String address, long delay, Queue<Instruction> instructions) {
		super(source, volume);
		this.address = address;
		this.delay = delay;
		this.instructions = instructions;
	}

	@Override
	protected void writeData(Packet packet) throws IOException {
		packet
			.writeString(address)
			.writeLong(delay)
			.writeInt(instructions.size());
		for (Instruction instruction : instructions) {
			if (instruction instanceof Open) {
				packet
					.writeByte((byte) 0)
					.writeByte((byte) ((Open) instruction).channelIndex);
			} else if (instruction instanceof Close) {
				packet
					.writeByte((byte) 1)
					.writeByte((byte) ((Close) instruction).channelIndex);
			} else if (instruction instanceof SetWave) {
				packet
					.writeByte((byte) 2)
					.writeByte((byte) ((SetWave) instruction).channelIndex)
					.writeInt(((SetWave) instruction).type.ordinal())
					.writeFloat(((SetWave) instruction).frequency);
			} else if (instruction instanceof Delay) {
				packet
					.writeByte((byte) 3)
					.writeInt(((Delay) instruction).delay);
			} else if (instruction instanceof SetFM) {
				packet
					.writeByte((byte) 4)
					.writeByte((byte) ((SetFM) instruction).channelIndex)
					.writeInt(((SetFM) instruction).freqMod.modulatorIndex)
					.writeFloat(((SetFM) instruction).freqMod.index);
			} else if (instruction instanceof ResetFM) {
				packet
					.writeByte((byte) 5)
					.writeByte((byte) ((ResetFM) instruction).channelIndex);
			} else if (instruction instanceof SetAM) {
				packet
					.writeByte((byte) 6)
					.writeByte((byte) ((SetAM) instruction).channelIndex)
					.writeInt(((SetAM) instruction).ampMod.modulatorIndex);
			} else if (instruction instanceof ResetAM) {
				packet
					.writeByte((byte) 7)
					.writeByte((byte) ((ResetAM) instruction).channelIndex);
			} else if (instruction instanceof SetADSR) {
				packet
					.writeByte((byte) 8)
					.writeByte((byte) ((SetADSR) instruction).channelIndex)
					.writeInt(((SetADSR) instruction).attackDuration)
					.writeInt(((SetADSR) instruction).decayDuration)
					.writeFloat(((SetADSR) instruction).attenuation)
					.writeInt(((SetADSR) instruction).releaseDuration);
			} else if (instruction instanceof ResetEnvelope) {
				packet
					.writeByte((byte) 9)
					.writeByte((byte) ((ResetEnvelope) instruction).channelIndex);
			} else if (instruction instanceof SetVolume) {
				packet
					.writeByte((byte) 10)
					.writeByte((byte) ((SetVolume) instruction).channelIndex)
					.writeFloat(((SetVolume) instruction).volume);
			}
		}
	}
}
