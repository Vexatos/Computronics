package pl.asie.computronics.integration.charset.audio;

import io.netty.buffer.ByteBuf;
import pl.asie.charset.api.audio.AudioData;
import pl.asie.charset.api.audio.AudioPacket;
import pl.asie.charset.api.audio.AudioSink;

import javax.annotation.Nullable;

public class AudioDataDummy extends AudioData {

	private final pl.asie.computronics.api.audio.AudioPacket wrapped;

	public AudioDataDummy(@Nullable pl.asie.computronics.api.audio.AudioPacket wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public int getTime() {
		return 0;
	}

	@Override
	public void readData(ByteBuf byteBuf) {

	}

	@Override
	public void writeData(ByteBuf byteBuf) {

	}

	@Override
	protected void sendClient(AudioPacket audioPacket) {
		for(AudioSink sink : audioPacket.getSinks()) {
			wrapped.addReceiver(new AudioReceiverConverted(sink));
		}
		wrapped.sendPacket();
	}
}
