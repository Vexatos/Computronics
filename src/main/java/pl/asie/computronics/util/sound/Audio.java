package pl.asie.computronics.util.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * @author Sangar, Vexatos
 */
@SideOnly(Side.CLIENT)
public class Audio {

	private final int sampleRate;

	private final int amplitude;

	private final float maxDistance;

	private final Set<Source> sources = new HashSet<Source>();

	private float volume() {
		return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS);
	}

	private boolean disableAudio = false;

	public void play(float x, float y, float z, AudioType type, float frequencyInHz, int durationInMilliseconds) {
		play(x, y, z, ".", type, frequencyInHz, durationInMilliseconds);
	}

	public void play(float x, float y, float z, String pattern, AudioType type) {
		play(x, y, z, pattern, type, 1000, 200);
	}

	public void play(float x, float y, float z, String pattern, AudioType type, float frequencyInHz) {
		play(x, y, z, pattern, type, frequencyInHz, 200);
	}

	public void play(float x, float y, float z, String pattern, AudioType type, float frequencyInHz, int durationInMilliseconds) {
		play(x, y, z, pattern, type, frequencyInHz, durationInMilliseconds, 0);
	}

	public void play(float x, float y, float z, AudioType type, float frequencyInHz, int durationInMilliseconds, int initialDelayInMilliseconds) {
		play(x, y, z, ".", type, frequencyInHz, durationInMilliseconds, initialDelayInMilliseconds);
	}

	public void play(float x, float y, float z, String pattern, AudioType type, float frequencyInHz, int durationInMilliseconds, int initialDelayInMilliseconds) {
		Minecraft mc = Minecraft.getMinecraft();
		float distanceBasedGain = ((float) Math.max(0, 1 - mc.player.getDistance(x, y, z) / maxDistance));
		//float gain = distanceBasedGain * volume();
		float gain = volume();
		if(gain <= 0 || amplitude <= 0) {
			return;
		}

		if(disableAudio) {
			// Fallback audio generation, using built-in Minecraft sound. This can be
			// necessary on certain systems with audio cards that do not have enough
			// memory. May still fail, but at least we can say we tried!
			// Valid range is 20-2000Hz, clamp it to that and get a relative value.
			// MC's pitch system supports a minimum pitch of 0.5, however, so up it
			// by that.
			float clampedFrequency = Math.min(Math.max(frequencyInHz - 20, 0), 1980) / 1980f + 0.5f;
			int delay = 0;
			for(char ch : pattern.toCharArray()) {
				PositionedSoundRecord record = new PositionedSoundRecord(SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, distanceBasedGain * gain, clampedFrequency, x, y, z);
				if(delay == 0) {
					mc.getSoundHandler().playSound(record);
				} else {
					mc.getSoundHandler().playDelayedSound(record, delay);
				}
				delay += Math.max((ch == '.' ? durationInMilliseconds : 2 * durationInMilliseconds) * 20 / 1000, 1);
			}
		} else {
			if(AL.isCreated()) {
				char[] chars = pattern.toCharArray();
				int[] sampleCounts = new int[chars.length];
				for(int i = 0; i < chars.length; i++) {
					sampleCounts[i] = (chars[i] == '.' ? durationInMilliseconds : 2 * durationInMilliseconds) * sampleRate / 1000;
				}

				// 50ms pause between pattern parts.
				int pauseSampleCount = 50 * sampleRate / 1000;
				int sampleSum = 0;
				for(int i : sampleCounts) {
					sampleSum += i;
				}
				int initialDelay = initialDelayInMilliseconds * sampleRate / 1000;

				ByteBuffer data = BufferUtils.createByteBuffer(initialDelay + sampleSum + (sampleCounts.length - 1) * pauseSampleCount);

				// Add the initial delay
				for(int sample = 0; sample < initialDelay; sample++) {
					data.put((byte) 127);
				}
				float step = frequencyInHz / ((float) sampleRate);
				float offset = 0f;
				double noiseOutput = Math.random();
				for(int sampleCount : sampleCounts) {
					for(int sample = 0; sample < sampleCount; sample++) {
						//double angle = 2 * Math.PI * offset;
						//int value = ((byte) (Math.signum(Math.sin(angle)) * amplitude())) ^ 0x80;
						int value = (byte) (type.generate(offset) * amplitude) ^ 0x80;
						offset += step;
						if(offset > 1) {
							offset %= 1.0F;
						}
						data.put((byte) value);
					}
					if(data.hasRemaining()) {
						for(int sample = 0; sample < pauseSampleCount; sample++) {
							data.put((byte) 127);
						}
					}
				}
				data.rewind();

				// Watch out for sound cards running out of memory... this apparently
				// really does happen. I'm assuming this is due to too many sounds being
				// kept loaded, since from what I can see OC's releasing its audio
				// memory as it should.
				try {
					synchronized(sources) {
						sources.add(new Source(x, y, z, data, gain));
					}
				} catch(LessUselessOpenALException e) {
					if(e.errorCode == AL10.AL_OUT_OF_MEMORY) {
						// Well... let's just stop here.
						Computronics.log.info("Couldn't play computer speaker sound because your sound card ran out of memory. Either your sound card is just really low-end, or there are just too many sounds in use already by other mods. Disabling computer speakers to avoid spamming your log file now.");
						disableAudio = true;
					} else {
						Computronics.log.warn("Error playing computer speaker sound.", e);
					}
				}
			}
		}
	}

	private final Set<Source> toRemove = new HashSet<Source>();

	private void update() {
		if(!disableAudio) {
			synchronized(sources) {
				for(Source source : sources) {
					if(source.checkFinished()) {
						toRemove.add(source);
					}
				}
				sources.removeAll(toRemove);
				toRemove.clear();
			}

			// Clear error stack.
			if(AL.isCreated()) {
				try {
					AL10.alGetError();
				} catch(UnsatisfiedLinkError e) {
					Computronics.log.warn("Negotiations with OpenAL broke down, disabling sounds.");
					disableAudio = true;
				}
			}
		}
	}

	private class Source {

		private int source;
		private IntBuffer[] buffers;
		private final Queue<ByteBuffer> bufferData;

		Source(float x, float y, float z, ByteBuffer data, float gain) {

			// Clear error stack.
			AL10.alGetError();

			buffers = new IntBuffer[2];
			buffers[0] = BufferUtils.createIntBuffer(1);
			buffers[1] = BufferUtils.createIntBuffer(1);
			AL10.alGenBuffers(buffers[0]);
			checkALError();

			bufferData = new ArrayDeque<ByteBuffer>(data.capacity() / sampleRate);
			for(int i = 0; i * sampleRate < data.capacity(); i++) {
				/*ByteBuffer buf = ByteBuffer.allocateDirect(Math.min(2 * sampleRate, bytes.length - (i * 2 * sampleRate)));
				buf.put(bytes, i * 2 * sampleRate, buf.capacity());
				buf.rewind();
				bufferData.add(buf);*/
				data.limit(i * sampleRate + Math.min(sampleRate, data.capacity() - (i * sampleRate)))
					.position(i * sampleRate);
				bufferData.add(data.slice());
			}

			try {
				AL10.alBufferData(buffers[0].get(0), AL10.AL_FORMAT_MONO8, bufferData.poll(), sampleRate);
				checkALError();
				if(!bufferData.isEmpty()) {
					AL10.alGenBuffers(buffers[1]);
					checkALError();
					AL10.alBufferData(buffers[1].get(0), AL10.AL_FORMAT_MONO8, bufferData.poll(), sampleRate);
					checkALError();
				}

				int source = AL10.alGenSources();
				checkALError();

				try {
					AL10.alSourceQueueBuffers(source, buffers[0]);
					checkALError();
					if(AL10.alIsBuffer(buffers[1].get(0))) {
						AL10.alSourceQueueBuffers(source, buffers[1]);
						checkALError();
					}

					AL10.alSource3f(source, AL10.AL_POSITION, x, y, z);
					//AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, maxDistance);
					//AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, maxDistance);
					AL10.alSourcef(source, AL10.AL_GAIN, gain * 0.3f);
					AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, (24F * 0.25F) / maxDistance); // At a distance of 24, a rolloff factor of 0.25 sounds good enough.
					checkALError();

					AL10.alSourcePlay(source);
					checkALError();

					this.source = source;
					//this.buffers = buffer;
				} catch(Throwable t) {
					AL10.alDeleteSources(source);
					throw t;
				}
			} catch(Throwable t) {
				for(IntBuffer b : buffers) {
					if(AL10.alIsBuffer(b.get(0))) {
						AL10.alDeleteBuffers(b);
					}
				}
				throw t;
			}
		}

		private int index = 0;

		public boolean checkFinished() {
			if(AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED) > 0) {
				int oldIndex = index;
				AL10.alSourceUnqueueBuffers(source, buffers[oldIndex]);
				checkALError();
				ByteBuffer buf = bufferData.poll();
				if(buf != null) {
					AL10.alBufferData(buffers[oldIndex].get(0), AL10.AL_FORMAT_MONO8, buf, sampleRate);
					AL10.alSourceQueueBuffers(source, buffers[oldIndex]);
					checkALError();
					index = (index + 1) % 2;
				}
				return false;
			}
			if(AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
				AL10.alDeleteSources(source);
				for(IntBuffer buffer : buffers) {
					if(AL10.alIsBuffer(buffer.get(0))) {
						AL10.alDeleteBuffers(buffer);
					}
				}
				return true;
			}
			return false;
		}
	}

	// Having the error code in an accessible way is really cool, you know.
	class LessUselessOpenALException extends OpenALException {

		final int errorCode;

		LessUselessOpenALException(int errorCode) {
			super(errorCode);
			this.errorCode = errorCode;
		}

	}

	// Custom implementation of Util.checkALError() that uses our custom exception.
	void checkALError() {
		int errorCode = AL10.alGetError();
		if(errorCode != AL10.AL_NO_ERROR) {
			throw new LessUselessOpenALException(errorCode);
		}
	}

	private Audio() {
		sampleRate = Config.SOUND_SAMPLE_RATE;
		amplitude = Config.SOUND_VOLUME;
		maxDistance = Config.SOUND_RADIUS;
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		update();
	}

	private static Audio INSTANCE;

	public static void init() {
		if(INSTANCE == null) {
			INSTANCE = new Audio();
			MinecraftForge.EVENT_BUS.register(INSTANCE);
		}
	}

	public static Audio instance() {
		if(INSTANCE == null) {
			init();
		}
		return INSTANCE;
	}
}
