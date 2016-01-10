package pl.asie.lib.audio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.util.Vec3;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import pl.asie.lib.AsieLibMod;

public class StreamingAudioPlayer extends DFPWM {
	public class SourceEntry {
		public final int x, y, z;
		public final IntBuffer src;
		public int receivedPackets;

		public SourceEntry(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			src = BufferUtils.createIntBuffer(1);
			AL10.alGenSources(src);
		}
	}

	public int lastPacketId;

	private boolean isInitializedClient = false;
	private Set<SourceEntry> sources = new HashSet<SourceEntry>();
	private IntBuffer buffer;
	private ArrayList<IntBuffer> buffersPlayed;
	private int SAMPLE_RATE;
	private final int BUFFER_PACKETS, FORMAT;
	private float volume = 1.0F;
	private float distance = 24.0F;
	
	public StreamingAudioPlayer(int sampleRate, boolean sixteenBit, boolean stereo, int bufferPackets) {
		super();
		lastPacketId = -9000;
		BUFFER_PACKETS = bufferPackets;
		SAMPLE_RATE = sampleRate;
		if(sixteenBit) {
			FORMAT = stereo ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
		} else {
			FORMAT = stereo ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_MONO8;
		}
	}
	
	public void setDistance(float dist) {
		this.distance = dist;
	}
	
	public void setVolume(float vol) {
		this.volume = vol;
	}
	
	public void setSampleRate(int rate) {
		SAMPLE_RATE = rate;
	}
	
	@SideOnly(Side.CLIENT)
	public void reset() {
		buffersPlayed = new ArrayList<IntBuffer>();
		lastPacketId = -9000;
		stopClient();
	}
	
	@SideOnly(Side.CLIENT)
	public boolean initClient() {
	    this.isInitializedClient = true;
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public float getDistance(int x, int y, int z) {
		Vec3 pos = Minecraft.getMinecraft().thePlayer.getPositionVector();
		double dx = pos.xCoord - x;
		double dy = pos.yCoord - y;
		double dz = pos.zCoord - z;
				
		float distance = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
		return distance;
	}

	@SideOnly(Side.CLIENT)
	public void queueData(byte[] data) {
		if(!isInitializedClient) {
			reset();
			initClient();
		}

		// Prepare buffers
		if (buffer == null) {
			buffer = BufferUtils.createIntBuffer(1);
		} else {
			for (SourceEntry source : sources) {
				int processed = AL10.alGetSourcei(source.src.get(0), AL10.AL_BUFFERS_PROCESSED);
				if (processed > 0) {
					AL10.alSourceUnqueueBuffers(source.src.get(0), buffer);
				}
			}
		}

		AL10.alGenBuffers(buffer);
		AL10.alBufferData(buffer.get(0), FORMAT, (ByteBuffer) (BufferUtils.createByteBuffer(data.length).put(data).flip()), SAMPLE_RATE);

		synchronized(buffersPlayed) {
			buffersPlayed.add(buffer);
		}
	}

	@SideOnly(Side.CLIENT)
	public void playPacket(int x, int y, int z) {
		if(!isInitializedClient) {
			reset();
			initClient();
		}

		FloatBuffer sourcePos = (FloatBuffer)(BufferUtils.createFloatBuffer(3).put(new float[] { x, y, z }).rewind());
		FloatBuffer sourceVel = (FloatBuffer)(BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind());

		SourceEntry source = null;
		for (SourceEntry entry : sources) {
			if (entry.x == x && entry.y == y && entry.z == z) {
				source = entry;
				continue;
			}
		}
		if (source == null) {
			source = new SourceEntry(x, y, z);
			sources.add(source);
		}

		// Calculate distance
		float playerDistance = getDistance(x, y, z);
		float distanceUsed = distance * (0.2F + (volume * 0.8F));
		float distanceReal = 1 - (playerDistance / distanceUsed);

		float gain = distanceReal * volume * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS);
		if (gain < 0.0F) {
			gain = 0.0F;
		} else if (gain > 1.0F) {
			gain = 1.0F;
		}

		// Set settings
		AL10.alSourcei(source.src.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
	    AL10.alSourcef(source.src.get(0), AL10.AL_PITCH,    1.0f);
	    AL10.alSourcef(source.src.get(0), AL10.AL_GAIN,     gain);
	    AL10.alSource (source.src.get(0), AL10.AL_POSITION, sourcePos);
	    AL10.alSource (source.src.get(0), AL10.AL_VELOCITY, sourceVel);
	    AL10.alSourcef(source.src.get(0), AL10.AL_ROLLOFF_FACTOR, 0.0f);

	    // Play audio
	    AL10.alSourceQueueBuffers(source.src.get(0), buffer);

	    int state = AL10.alGetSourcei(source.src.get(0), AL10.AL_SOURCE_STATE);

	    if(source.receivedPackets > BUFFER_PACKETS && state != AL10.AL_PLAYING) AL10.alSourcePlay(source.src.get(0));
	    else if(source.receivedPackets <= BUFFER_PACKETS) AL10.alSourcePause(source.src.get(0));

		source.receivedPackets++;
	}
	
	@SideOnly(Side.CLIENT)
	private void stopClient() {
		int scount = sources.size();
		for (SourceEntry source : sources) {
			AL10.alSourceStop(source.src.get(0));
			AL10.alDeleteSources(source.src.get(0));
		}
		sources.clear();

		int count = 0;
		if (buffersPlayed != null) {
			synchronized (buffersPlayed) {
				if (buffer != null) {
					buffersPlayed.add(buffer);
				}
				for (IntBuffer b : buffersPlayed) {
					b.rewind();
					for (int i = 0; i < b.limit(); i++) {
						int buffer = b.get(i);
						if (AL10.alIsBuffer(buffer)) {
							AL10.alDeleteBuffers(buffer);
							count++;
						}
					}
				}
				buffersPlayed.clear();
			}
		}

		AsieLibMod.log.debug("Cleaned " + count + " buffers and " + scount + " sources.");
	}
	
	public void stop() {
		stopClient();
	    this.isInitializedClient = false;
	}
}
