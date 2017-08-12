package pl.asie.computronics.integration.charset.audio;

import io.netty.buffer.ByteBuf;
import pl.asie.charset.api.audio.IAudioDataPCM;
import pl.asie.lib.audio.DFPWM;

public class AudioDataDFPWM extends AudioDataDummy implements IAudioDataPCM {

	protected byte[] decodedData;
	private byte[] data;
	private int time;
	private static final DFPWM dfpwm = new DFPWM();

	public AudioDataDFPWM() {
		super(null);
	}

	public AudioDataDFPWM(pl.asie.computronics.api.audio.AudioPacket wrapped, byte[] data, int time) {
		super(wrapped);
		this.data = data;
		this.time = time;
	}

	@Override
	public int getSampleRate() {
		int samples = data.length * 8;
		return samples * 1000 / time;
	}

	@Override
	public int getSampleSize() {
		return 1;
	}

	@Override
	public boolean isSampleSigned() {
		return true;
	}

	@Override
	public boolean isSampleBigEndian() {
		return false;
	}

	@Override
	public byte[] getSamplePCMData() {
		if(decodedData == null) {
			decodedData = new byte[data.length * 8];
			dfpwm.decompress(decodedData, data, 0, 0, data.length);
		}

		return decodedData;
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void readData(ByteBuf buf) {
		time = buf.readUnsignedMedium();
		data = new byte[buf.readUnsignedShort()];
		buf.readBytes(data);
	}

	@Override
	public void writeData(ByteBuf buf) {
		buf.writeMedium(time);
		buf.writeShort(data.length);
		buf.writeBytes(data);
	}
}
