package pl.asie.computronics.audio.tts;

import marytts.exceptions.SynthesisException;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.audio.tts.TextToSpeech.ICanSpeak;
import pl.asie.lib.audio.DFPWM;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * @author Vexatos
 */
public class SynthesizeTask implements Callable<TextToSpeech.Result> {

	private final WeakReference<ICanSpeak> device;
	private final String text;

	public SynthesizeTask(ICanSpeak device, String text) {
		this.device = new WeakReference<ICanSpeak>(device);
		this.text = text;
	}

	@Nullable
	@Override
	public TextToSpeech.Result call() throws Exception {
		if(Computronics.tts.marytts == null /*|| ttsThreads == null*/) {
			return null;
		}
		try {
			AudioInputStream audio = Computronics.tts.marytts.generateAudio(text);
			AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 32768, 8, 1, 1, 32768, false);
			AudioInputStream inFile = AudioSystem.getAudioInputStream(convertFormat, audio);
			byte[] readBuffer = new byte[1024];
			byte[] outBuffer = new byte[readBuffer.length / 8];
			DFPWM converter = new DFPWM();
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int read;
			do {
				for(read = 0; read < readBuffer.length; ) {
					int amt = inFile.read(readBuffer, read, readBuffer.length - read);
					if(amt == -1) {
						break;
					}
					read += amt;
				}
				read &= ~0x07;
				converter.compress(outBuffer, readBuffer, 0, 0, read / 8);
				out.write(outBuffer, 0, read / 8);
			} while(read == readBuffer.length);

			// Need to add some padding due to the system immediately stopping once the end is reached.
			outBuffer = new byte[4096];
			converter.compress(outBuffer, new byte[32768], 0, 0, 4096);
			out.write(outBuffer);

			return new TextToSpeech.Result(device, out.toByteArray());
		} catch(SynthesisException e) {
			TextToSpeech.log.error("Text To Speech synthesis failed");
			e.printStackTrace();
		} catch(IOException e) {
			TextToSpeech.log.error("Text To Speech synthesis failed");
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
