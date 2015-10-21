package pl.asie.computronics.audio.tts.synth;

import marytts.exceptions.SynthesisException;
import net.minecraft.client.Minecraft;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.audio.tts.core.TextToSpeech;
import pl.asie.computronics.audio.tts.core.TextToSpeech.Result;
import pl.asie.computronics.audio.tts.core.TextToSpeechLoader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
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
			TextToSpeechLoader.INSTANCE.out.write("text:" + text);
			AudioInputStream audio = Computronics.tts.marytts.generateAudio(text);
			//AudioFormat audioFormat = audio.getFormat();
			//AudioFormat e = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), 2, audioFormat.getFrameSize(), audioFormat.getSampleRate(), audioFormat.isBigEndian());
			//audio = AudioSystem.getAudioInputStream(e, audio);
			//WaveData data = WaveData.create(audio);*/
			File tmpDir = TextToSpeechLoader.ttsDir.exists() ? new File(TextToSpeechLoader.ttsDir, "synth_tmp") : new File(Minecraft.getMinecraft().mcDataDir, "marytts_tmp");
			if(!tmpDir.exists()) {
				tmpDir.mkdir();
			}
			File tempFile = File.createTempFile("tts_", ".wav", tmpDir);
			tempFile.deleteOnExit();
			AudioSystem.write(audio, AudioFileFormat.Type.WAVE, tempFile);
			
			//AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(tempFile);
			//sndSys.quickPlay(false, tempFile.toURI().toURL(), tempFile.getName(), false, x, y, z, SoundSystemConfig.ATTENUATION_LINEAR, 16);
			//SoundSystem sndSys = getSoundSystem();
			//sndSys.newSource(false, tempFile.getName(), tempFile.toURI().toURL(), tempFile.getName(), false, x, y, z, 2, 16);
			//sndSys.play(tempFile.getName());
			//AudioPlayer player = new AudioPlayer(audio);
			//ttsThreads.submit(player);
			if(tempFile.exists()) {
				return new Result(tempFile, dimID, x, y, z);
			}
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
