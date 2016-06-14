package pl.asie.computronics.integration.charset.audio;

import io.netty.buffer.ByteBuf;
import pl.asie.charset.api.audio.AudioData;

public class AudioDataDummy extends AudioData {
    @Override
    public int getTime() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void readData(ByteBuf byteBuf) {

    }

    @Override
    public void writeData(ByteBuf byteBuf) {

    }
}
