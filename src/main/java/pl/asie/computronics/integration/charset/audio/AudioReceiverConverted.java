package pl.asie.computronics.integration.charset.audio;


import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.charset.api.audio.AudioSink;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;

/**
 * Created by asie on 6/14/16.
 */
public class AudioReceiverConverted implements IAudioReceiver {
    private final AudioSink sink;

    public AudioReceiverConverted(AudioSink sink) {
        this.sink = sink;
    }

    @Override
    public World getSoundWorld() {
        return sink.getWorld();
    }

    @Override
    public BlockPos getSoundPos() {
        return new BlockPos(sink.getPos());
    }

    @Override
    public int getSoundDistance() {
        return Math.round(sink.getDistance());
    }

    @Override
    public void receivePacket(AudioPacket packet, EnumFacing side) {

    }

    @Override
    public boolean connectsAudio(EnumFacing side) {
        return false;
    }
}
