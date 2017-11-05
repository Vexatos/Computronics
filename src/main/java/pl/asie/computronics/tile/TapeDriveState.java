package pl.asie.computronics.tile;

import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.api.tape.ITapeStorage;
import pl.asie.computronics.audio.AudioUtils;

import java.util.Arrays;

public class TapeDriveState {
	public enum State {
		STOPPED,
		PLAYING,
		REWINDING,
		FORWARDING;

		public static final State[] VALUES = values();
	}

	private State state = State.STOPPED;
	private int codecId;//, packetId;
	private long lastCodecTime;
	public int packetSize = 1024;
	public int soundVolume = 127;
	private ITapeStorage storage;

	public ITapeStorage getStorage() { return storage; }

	public void setStorage(ITapeStorage storage) {
		this.storage = storage;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean setSpeed(float speed) {
		if(speed < 0.25F || speed > 2.0F) return false;
		this.packetSize = Math.round(1024*speed);
		return true;
	}

	public int getId() {
		return codecId;
	}

	public byte getVolume() {
		return (byte) soundVolume;
	}

	public void setVolume(float volume) {
		if(volume < 0.0F) volume = 0.0F;
		if(volume > 1.0F) volume = 1.0F;
		this.soundVolume = (int) Math.floor(volume * 127.0F);
	}

	public void switchState(World worldObj, State newState) {
		if(worldObj.isRemote) { // Client-side happening
			if(newState == state) return;
		}
		if(!worldObj.isRemote) { // Server-side happening
			if(this.storage == null) newState = State.STOPPED;
			if(state == State.PLAYING) { // State is playing - stop playback
				AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
			}
			if(newState == State.PLAYING) { // Time to play again!
				codecId = Computronics.instance.audio.newPlayer();
				Computronics.instance.audio.getPlayer(codecId);
				lastCodecTime = System.nanoTime();
			}
		}
		state = newState;
		//sendState();
	}

	public State getState() {
		return state;
	}

	private AudioPacket createMusicPacket(IAudioSource source, World worldObj) {
		byte[] pktData = new byte[packetSize];
		int amount = storage.read(pktData, false); // read data into packet array

		if (amount < packetSize) switchState(worldObj, State.STOPPED);

		if (amount > 0) {
			return new AudioPacketDFPWM(source, getVolume(), packetSize * 8 * 4, amount == packetSize ? pktData : Arrays.copyOf(pktData, amount));
		} else {
			return null;
		}
	}

	public AudioPacket update(IAudioSource source, World worldObj) {
		if(!worldObj.isRemote) {
			switch(state) {
				case PLAYING: {
					if(storage.getPosition() >= storage.getSize() || storage.getPosition() < 0){
						storage.setPosition(storage.getPosition());
					}
                    long time = System.nanoTime();
					if ((time - (250 * 1000000)) > lastCodecTime) {
                        lastCodecTime += (250 * 1000000);
						return createMusicPacket(source, worldObj);
					}
				} break;
				case REWINDING: {
					int seeked = storage.seek(-2048);
					if(seeked > -2048) switchState(worldObj, State.STOPPED);
				} break;
				case FORWARDING: {
					int seeked = storage.seek(2048);
					if(seeked < 2048) switchState(worldObj, State.STOPPED);
				} break;
				case STOPPED: {
				} break;
			}
		}
		return null;
	}
}
