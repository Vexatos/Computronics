package pl.asie.computronics.audio;

import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.audio.StreamingPlaybackManager;

/**
 * @author gamax92
 */
public class SoundCardPlaybackManager extends StreamingPlaybackManager {

	public SoundCardPlaybackManager(boolean isClient) {
		super(isClient);
	}

	@Override
	public StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(false, false, -1);
	}
}
