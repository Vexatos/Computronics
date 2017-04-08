package pl.asie.computronics.audio.tts.synth;

import marytts.exceptions.SynthesisException;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.audio.tts.core.TextToSpeech;
import pl.asie.computronics.audio.tts.core.TextToSpeech.Result;
import pl.asie.lib.audio.DFPWM;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author Vexatos
 */
public class SynthesizeTask implements Callable<Result> {

	private final String text;
	private final int dimID;
	private final int x, y, z;

	public SynthesizeTask(String text, int dimID, int x, int y, int z) {
		this.text = text;
		this.dimID = dimID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Result call() throws Exception {
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

			return new Result(out.toByteArray(), dimID, x, y, z);
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
