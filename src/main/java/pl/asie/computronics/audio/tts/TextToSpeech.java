package pl.asie.computronics.audio.tts;

import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.registry.GameRegistry;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.server.Mary;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.block.Block;
import pl.asie.computronics.Computronics;

import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vexatos
 */
public class TextToSpeech {
	private MaryInterface marytts;
	private ExecutorService ttsThreads;

	public void say(String text) {
		if(marytts == null || ttsThreads == null) {
			return;
		}
		try {
			AudioInputStream audio = marytts.generateAudio(text);
			AudioPlayer player = new AudioPlayer(audio);
			ttsThreads.submit(player);
		} catch(SynthesisException e) {
			Computronics.log.error("Text To Speech synthesis failed");
			e.printStackTrace();
		}
	}

	public Block ttsBox;

	public void preInit(Computronics computronics) {
		if(computronics.isEnabled("ttsBox", ModAPIManager.INSTANCE.hasAPI("computronics|marytts"))) {
			Computronics.log.info("Initializing Text To Speech");
			try {
				marytts = new LocalMaryInterface();
				Set<String> voices = marytts.getAvailableVoices();
				marytts.setVoice(voices.iterator().next());
				ttsThreads = Executors.newCachedThreadPool();
			} catch(MaryConfigurationException e) {
				Computronics.log.error("Text To Speech initialization failed, you will not be able to hear anything");
				if(Mary.currentState() == 2) {
					Mary.shutdown();
					marytts = null;
				}
				e.printStackTrace();
				e.getCause().printStackTrace();
				e.getCause().getCause().printStackTrace();
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
			System.exit(0);
		} catch(MaryConfigurationException e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
			e.getCause().getCause().printStackTrace();
			Mary.shutdown();
		} catch(SynthesisException e) {
			e.printStackTrace();
			Mary.shutdown();
		} catch(InterruptedException e) {
			e.printStackTrace();
			Mary.shutdown();
		} catch(ExecutionException e) {
			e.printStackTrace();
			Mary.shutdown();
		}
	}
}
