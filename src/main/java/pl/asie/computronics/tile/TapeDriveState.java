package pl.asie.computronics.tile;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.Packets;
import pl.asie.computronics.tape.Storage;
import pl.asie.lib.network.Packet;
import net.minecraft.world.World;

public class TapeDriveState {
	public enum State {
		STOPPED,
		PLAYING,
		REWINDING,
		FORWARDING
	}
	
	private State state = State.STOPPED;
	private int codecId, codecTick, packetId;
	protected int packetSize = 1024;
	protected int soundVolume = 127;
	private Storage storage;
	
	public Storage getStorage() { return storage; }
	protected void setStorage(Storage storage) { this.storage = storage; }
	protected void setState(State state) { this.state = state; }
	
	public boolean setSpeed(float speed) {
		if(speed < 0.25F || speed > 2.0F) return false;
		this.packetSize = Math.round(1024*speed);
		return true;
	}
	
	public void setVolume(float volume) {
		if(volume < 0.0F) volume = 0.0F;
		if(volume > 1.0F) volume = 1.0F;
		this.soundVolume = (int)Math.floor(volume*127);
	}
	
	public void switchState(World worldObj, int x, int y, int z, State newState) {
		if(worldObj.isRemote) { // Client-side happening
			if(newState == state) return;
		}
		if(!worldObj.isRemote) { // Server-side happening
			if(this.storage == null) newState = State.STOPPED;
			if(state == State.PLAYING) { // State is playing - stop playback
				Computronics.instance.audio.removePlayer(codecId);
				try {
					Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_STOP)
						.writeInt(codecId);
					Computronics.packet.sendToAll(pkt);
				} catch(Exception e) { e.printStackTrace(); }
			}
			if(newState == State.PLAYING) { // Time to play again!
				codecId = Computronics.instance.audio.newPlayer();
				Computronics.instance.audio.getPlayer(codecId);
				codecTick = 0;
				packetId = 0;
			}
		}
		state = newState;
		//sendState();
	}
	
	public State getState() {
		return state;
	}

	private Packet createMusicPacket(World worldObj, int x, int y, int z) {
		byte[] packet = new byte[packetSize];
		int amount = storage.read(packet, 0, false); // read data into packet array
		try {
			Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_DATA)
				.writeInt(worldObj.provider.dimensionId)
				.writeInt(x).writeInt(y).writeInt(z)
				.writeInt(packetId++)
				.writeInt(codecId)
				.writeShort((short)packetSize)
				.writeByte((byte)soundVolume)
				.writeByteArrayData(packet);
			if(amount < packetSize) switchState(worldObj, x, y, z, State.STOPPED);
			return pkt;
		} catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	public Packet update(World worldObj, int x, int y, int z) {
		if(!worldObj.isRemote) {
			switch(state) {
				case PLAYING: {
					if(codecTick % 5 == 0) {
						codecTick++;
						return createMusicPacket(worldObj, x, y, z);
					} else codecTick++;
				} break;
				case REWINDING: {
					int seeked = storage.seek(-2048);
					if(seeked > -2048) switchState(worldObj, x, y, z, State.STOPPED);
				} break;
				case FORWARDING: {
					int seeked = storage.seek(2048);
					if(seeked < 2048) switchState(worldObj, x, y, z, State.STOPPED);
				} break;	
				case STOPPED: {
				} break;
			}
		}
		return null;
	}
}
