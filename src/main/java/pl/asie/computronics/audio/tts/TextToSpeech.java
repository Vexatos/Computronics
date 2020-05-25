package pl.asie.computronics.audio.tts;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.server.Mary;
import marytts.util.data.audio.AudioPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.reference.Mods;

import javax.sound.sampled.AudioInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Vexatos
 */
public class TextToSpeech {

	public MaryInterface marytts;

	private ExecutorService ttsThreads;
	public final ArrayList<Future<Result>> processes = new ArrayList<Future<Result>>();
	public static final Logger log = LogManager.getLogger(Mods.Computronics + "-text-to-speech");

	public TextToSpeech() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void say(ICanSpeak device, String text) {
		Future<Result> fut = ttsThreads.submit(new SynthesizeTask(device, text));
		processes.add(fut);
	}

	public interface ICanSpeak {

		void startTalking(byte[] data);

		boolean isValid();
	}

	@SubscribeEvent
	public void onTick(ServerTickEvent e) {
		if(e.phase == Phase.START) {
			ArrayList<Future<Result>> toRemove = new ArrayList<Future<Result>>();
			for(Future<Result> process : processes) {
				if(process.isDone()) {
					try {
						Result result = process.get();
						if(result != null) {
							ICanSpeak device = result.device.get();
							if(device != null && device.isValid()) {
								device.startTalking(result.data);
							}
						}
					} catch(Throwable t) {
						log.error("Error while playing text to speech", t);
					}
					toRemove.add(process);
				}
			}
			processes.removeAll(toRemove);
		}
	}

	public static class Result {

		private final WeakReference<ICanSpeak> device;
		private final byte[] data;

		public Result(WeakReference<ICanSpeak> device, byte[] data) {
			this.device = device;
			this.data = data;
		}
	}

	public boolean preInit() {
		try {
			marytts = new LocalMaryInterface();
			//Set<String> voices = marytts.getAvailableVoices();
			marytts.setStreamingAudio(true);
			String voice = marytts.getVoice();
			if(voice == null) {
				Set<Locale> availableLocales = marytts.getAvailableLocales();
				for(Locale locale : availableLocales) {
					marytts.setLocale(locale);
					voice = marytts.getVoice();
					if(voice != null) {
						break;
					}
				}
				if(voice == null) {
					log.error("No voice found for available locales, please install a matching voice file. Available locales: " + StringUtils.join(availableLocales, ", "));
					if(Mary.currentState() == 2) {
						Mary.shutdown();
					}
					marytts = null;
					return false;
				}
			}
			//marytts.setLocale(Locale.US);
			//marytts.setVoice(voices.iterator().next());
			marytts.setOutputType("AUDIO");
			ttsThreads = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setPriority(Thread.MIN_PRIORITY).build());
			log.info("Text To Speech successfully initialized with language " + marytts.getLocale() + " and voice " + voice + ".");
			return true;
		} catch(Exception e) {
			log.error("Text To Speech initialization failed, you will not be able to hear anything", e);
			if(Mary.currentState() == 2) {
				Mary.shutdown();
			}
			marytts = null;
			return false;
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
