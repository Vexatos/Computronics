package pl.asie.computronics.audio;

import pl.asie.computronics.Computronics;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.audio.StreamingPlaybackManager;

public class DFPWMPlaybackManager extends StreamingPlaybackManager {
	public DFPWMPlaybackManager(boolean isClient) {
		super(isClient);
	}

	public StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(32768, false, false, (int)Math.round(Computronics.TAPEDRIVE_BUFFER_MS / 250));
	}
}
