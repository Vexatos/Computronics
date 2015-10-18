package pl.asie.computronics.audio.tts.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.server.Mary;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.audio.tts.BlockTTSBox;
import pl.asie.computronics.audio.tts.TileTTSBox;
import pl.asie.computronics.audio.tts.synth.SynthesizeTask;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.network.Packet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
	public final ArrayList<SoundPos> playingSounds = new ArrayList<SoundPos>();
	public static Logger log;

	public TextToSpeech() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		log = LogManager.getLogger(Mods.Computronics + "-text-to-speech");

		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				synchronized(playingSounds) {
					updateVolume();
				}
			}
		}, 500, 50);
	}

	public void say(String text, int dimID, int x, int y, int z) {
		Future<Result> fut = ttsThreads.submit(new SynthesizeTask(text, dimID, x, y, z));
		processes.add(fut);
	}

	float lastVolume = FMLClientHandler.instance().getClient().gameSettings.getSoundLevel(SoundCategory.BLOCKS);
	private Timer updateTimer = new Timer("Computronics-SoundUpdater", true);

	private void updateVolume() {
		float volume;
		boolean paused = false;
		if(isGamePaused()) {
			volume = 0f;
			paused = true;
		} else {
			volume = FMLClientHandler.instance().getClient().gameSettings.getSoundLevel(SoundCategory.BLOCKS);
		}
		if(volume != lastVolume) {
			lastVolume = volume;
			SoundSystem sndSys = getSoundSystem();
			if(sndSys != null) {
				synchronized(playingSounds) {
					for(SoundPos sound : playingSounds) {
						sndSys.setVolume(sound.soundName, volume);
						if(paused) {
							if(!sound.isPaused()) {
								sndSys.pause(sound.soundName);
								sound.setPaused(true);
							}
						} else {
							if(sound.isPaused()) {
								sndSys.play(sound.soundName);
								sound.setPaused(false);
							}
						}
					}
				}
			}
		}
	}

	public static boolean isGamePaused() {
		if(MinecraftServer.getServer() != null && !MinecraftServer.getServer().isDedicatedServer()) {
			if(MinecraftServer.getServer() instanceof IntegratedServer) {
				return Minecraft.getMinecraft().isGamePaused();
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if(e.phase == Phase.END) {
			SoundSystem sndSys = getSoundSystem();
			if(sndSys != null) {
				ArrayList<Integer> toRemove = new ArrayList<Integer>();
				for(int i = 0; i < processes.size(); i++) {
					Future<Result> process = processes.get(i);
					if(process.isDone()) {
						try {
							Result result = process.get();
							if(result != null) {
								File file = result.file;
								if(file != null) {
									AudioFileFormat format = AudioSystem.getAudioFileFormat(file);
									if(format != null) {
										int time = (int) (((double) format.getFrameLength() / (double) format.getFormat().getFrameRate()) * 20D);
										if(time > 0) {
											World world = AsieLibMod.proxy.getWorld(result.dimID);
											if(world != null) {
												TileEntity tile = world.getTileEntity(result.x, result.y, result.z);
												if(tile instanceof TileTTSBox) {
													String name = file.getName();
													sndSys.newSource(false, name, file.toURI().toURL(), name,
														false, result.x, result.y, result.z, SoundSystemConfig.ATTENUATION_LINEAR, 16);
													sndSys.setVolume(name, lastVolume);
													sndSys.activate(name);
													sndSys.play(name);
													playingSounds.add(new SoundPos(name, result.dimID, result.x, result.y, result.z));
													Packet packet = Computronics.packet.create(Packets.PACKET_TTS)
														.writeTileLocation(tile)
														.writeInt(time);
													Computronics.packet.sendToServer(packet);
													((TileTTSBox) tile).setLocked(time);
												}
											}
										}
									}
								}
							}
						} catch(Throwable t) {
							log.error("Error while playing text to speech", t);
						}
						toRemove.add(i);
					}
				}
				for(int i : toRemove) {
					processes.remove(i);
				}
				for(int i = 0; i < playingSounds.size(); i++) {
					SoundPos sound = playingSounds.get(i);
					if(!sndSys.playing(sound.soundName) && !sound.isPaused()) {
						playingSounds.remove(i);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e) {
		SoundSystem soundSystem = getSoundSystem();
		for(Future<Result> process : processes) {
			process.cancel(true);
		}
		processes.clear();
		if(soundSystem != null) {
			for(SoundPos sound : playingSounds) {
				if(sound != null) {
					try {
						soundSystem.stop(sound.soundName);
						soundSystem.removeSource(sound.soundName);
					} catch(Throwable t) {
						// NO-OP
					}
				}
			}
			playingSounds.clear();
		}
	}

	public void stopSource(TileEntity tile) {
		int toRemove = -1;
		for(int i = 0; i < playingSounds.size(); i++) {
			SoundPos sound = playingSounds.get(i);
			if(sound.matches(tile)) {
				SoundSystem soundSystem = getSoundSystem();
				if(soundSystem != null) {
					soundSystem.stop(sound.soundName);
					soundSystem.removeSource(sound.soundName);
					toRemove = i;
					break;
				}
			}
		}
		if(toRemove >= 0) {
			playingSounds.remove(toRemove);
		}
	}

	public static class Result {
		private final File file;
		private final int dimID;
		private final int x;
		private final int y;
		private final int z;

		public Result(File file, int dimID, int x, int y, int z) {
			this.file = file;
			this.dimID = dimID;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public static class SoundPos {
		private final String soundName;
		private final int dimID;
		private final int x;
		private final int y;
		private final int z;
		private boolean paused = false;

		public SoundPos(String soundName, int dimID, int x, int y, int z) {
			this.soundName = soundName;
			this.dimID = dimID;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public boolean matches(TileEntity tile) {
			return x == tile.xCoord && y == tile.yCoord && z == tile.zCoord && dimID == tile.getWorldObj().provider.dimensionId;
		}

		public boolean isPaused() {
			return paused;
		}

		public void setPaused(boolean paused) {
			this.paused = paused;
		}
	}

	//private static Field sndSysField = null;
	public static SoundManager manager;

	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public void onSoundLoad(SoundLoadEvent e) {
		manager = e.manager;
	}

	@SubscribeEvent
	public void onSoundSetup(SoundSetupEvent e) {
		try {
			SoundSystemConfig.setCodec("wav", CodecWav.class);
		} catch(SoundSystemException t) {
			log.error("Error trying to add wavesound codec.", t);
		}
	}

	public static SoundSystem getSoundSystem() {
		/*SoundSystem sndSys = null;
		if(sndSysField == null) {
			try {
				sndSysField = SoundManager.class.getField("sndSystem");
				sndSysField.setAccessible(true);
			} catch(Exception t) {
				Computronics.log.error("Text To Speech sound system stuff failed", t);
			}
		}
		if(sndSysField != null) {
			try {
				sndSys = (SoundSystem) sndSysField.get(Computronics.tts.manager);
			} catch(IllegalAccessException e) {
				Computronics.log.error("Unable to access sound system.", e);
				sndSys = null;
			}
		}
		return sndSys;*/
		return manager != null ? manager.sndSystem : null;
	}

	public Block ttsBox;

	public void preInit(Computronics computronics) {
		if(computronics.isEnabled("ttsBox", Mods.isClassLoaded("marytts.LocalMaryInterface"))) {
			log.info("Initializing Text To Speech");
			try {
				marytts = new LocalMaryInterface();
				//Set<String> voices = marytts.getAvailableVoices();
				marytts.setStreamingAudio(true);
				//marytts.setLocale(Locale.US);
				//marytts.setVoice(voices.iterator().next());
				marytts.setOutputType("AUDIO");
				ttsThreads = Executors.newCachedThreadPool();
			} catch(Exception e) {
				log.error("Text To Speech initialization failed, you will not be able to hear anything", e);
				if(Mary.currentState() == 2) {
					Mary.shutdown();
					marytts = null;
				}
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
