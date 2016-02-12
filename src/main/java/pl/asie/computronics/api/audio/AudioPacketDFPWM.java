package pl.asie.computronics.api.audio;

import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * NOTE: Using this packet type requires Computronics to be present!
 * The client-side implementation of the packet is left to be internal.
 */
public class AudioPacketDFPWM extends AudioPacket {
	public final int frequency;
	public final byte[] data;

	public AudioPacketDFPWM(IAudioSource source, byte volume, int frequency, byte[] data) {
		super(source, volume);
		this.frequency = frequency;
		this.data = data;
	}

	@Override
	protected void writeData(Packet p) throws IOException {
		p.writeInt(frequency).writeShort((short) data.length).writeByteArrayData(data);
	}
}
