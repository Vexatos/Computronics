package pl.asie.computronics.integration.charset.audio;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pl.asie.charset.api.audio.AudioSink;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.audio.AudioUtils;

import javax.annotation.Nullable;

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
	public Vec3d getSoundPos() {
		return sink.getPos();
	}

	@Override
	public int getSoundDistance() {
		return Math.round(sink.getDistance());
	}

	@Override
	public void receivePacket(AudioPacket packet, @Nullable EnumFacing side) {

	}

	@Override
	public String getID() {
		return AudioUtils.positionId(sink.getPos());
	}

	@Override
	public boolean connectsAudio(EnumFacing side) {
		return false;
	}
}
