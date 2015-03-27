package pl.asie.computronics.audio.tts.core;

import cpw.mods.fml.common.registry.GameRegistry;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.server.Mary;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.block.Block;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.audio.tts.BlockTTSBox;
import pl.asie.computronics.audio.tts.TileTTSBox;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.audio.StreamingPlaybackManager;

import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vexatos
 */
public class TextToSpeech extends StreamingPlaybackManager {
	private MaryInterface marytts;

	//private ExecutorService ttsThreads;

	public TextToSpeech(boolean isClient) {
		super(isClient);
	}

	@Override
	public StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(32768, false, false, Math.round(Config.TAPEDRIVE_BUFFER_MS / 250));
	}

	public byte[] say(int x, int y, int z, String text) {
		if(marytts == null /*|| ttsThreads == null*/) {
			return new byte[0];
		}
		try {
			AudioInputStream audio = marytts.generateAudio(text);
			//TODO Turn this into an OpenAL-compatible byte array
			/*WaveData data = WaveData.create(audio);
			ArrayList<Byte> byteData = new ArrayList<Byte>();
			while(data.data.hasRemaining()) {
				byteData.add(data.data.get());
			}
			Byte[] array = byteData.toArray(new Byte[byteData.size()]);
			byte[] result = new byte[array.length];
			for(int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
			return result;*/
			return new byte[0];
			//AudioPlayer player = new AudioPlayer(audio);
			//ttsThreads.submit(player);
		} catch(SynthesisException e) {
			Computronics.log.error("Text To Speech synthesis failed");
			e.printStackTrace();
		}
		return null;
	}

	public Block ttsBox;

	public void preInit(Computronics computronics) {
		if(computronics.isEnabled("ttsBox", Mods.API.hasClass("marytts.LocalMaryInterface"))) {
			Computronics.log.info("Initializing Text To Speech");
			try {
				marytts = new LocalMaryInterface();
				Set<String> voices = marytts.getAvailableVoices();
				marytts.setVoice(voices.iterator().next());
				//ttsThreads = Executors.newCachedThreadPool();
			} catch(Exception e) {
				Computronics.log.error("Text To Speech initialization failed, you will not be able to hear anything");
				if(Mary.currentState() == 2) {
					Mary.shutdown();
					marytts = null;
				}
				e.printStackTrace();
				e.getCause().printStackTrace();
				return;
			}
			ttsBox = new BlockTTSBox();
			GameRegistry.registerBlock(ttsBox, "computronics.ttsBox");
			GameRegistry.registerTileEntity(TileTTSBox.class, "computronics.ttsBox");
		}
	}

	public static void main(String[] args) {
		try {
			MaryInterface marytts = new LocalMaryInterface();
			Set<String> voices = marytts.getAvailableVoices();
			marytts.setVoice(voices.iterator().next());
			marytts.setLocale(Locale.US);
			AudioInputStream audio = marytts.generateAudio("I am a pastry fork.");
			AudioPlayer player = new AudioPlayer(audio);
			ExecutorService service = Executors.newCachedThreadPool();
			service.submit(player).get();
		} catch(MaryConfigurationException e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
			e.getCause().getCause().printStackTrace();
		} catch(SynthesisException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(ExecutionException e) {
			e.printStackTrace();
		}
	}

	/*static{
		ClassLoader classLoader = TextToSpeech.class.getClassLoader();
		System.out.println("Classloader: " + classLoader.toString());
	}*/
}
