package pl.asie.computronics;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import pl.asie.lib.util.WorldUtils;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class NetworkHandlerClient extends MessageHandlerBase {
	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(32768, 8, 1, false, false);

	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
			throws IOException {
		//System.out.println("CLIENT PACKET " + command);
		switch(command) {
			case Packets.PACKET_TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntity();
				State state = State.values()[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive)entity;
					tile.switchState(state);
				}
			} break;
			case Packets.PACKET_AUDIO_DATA: {
				int dimId = packet.readInt();
				int x = packet.readInt();
				int y = packet.readInt();
				int z = packet.readInt();
				int packetId = packet.readInt();
				int codecId = packet.readInt();
				short packetSize = packet.readShort();
				short volume = packet.readByte();
				byte[] data = packet.readByteArrayData(packetSize);
				byte[] audio = new byte[packetSize * 8];
				String sourceName = "dfpwm_"+codecId;
				StreamingAudioPlayer codec = Computronics.instance.audio.getPlayer(codecId);
	
				if(dimId != WorldUtils.getCurrentClientDimension()) return;
				
				codec.decompress(audio, data, 0, 0, packetSize);
				for(int i = 0; i < (packetSize * 8); i++) {
					// Convert signed to unsigned data
					audio[i] = (byte)(((int)audio[i] & 0xFF) ^ 0x80);
				}
				
				if((codec.lastPacketId + 1) != packetId) {
					codec.reset();
				}
				codec.setSampleRate(packetSize * 32);
				codec.setDistance((float)Computronics.TAPEDRIVE_DISTANCE);
				codec.setVolume(volume/127.0F);
				codec.playPacket(audio, x, y, z);
				codec.lastPacketId = packetId;
			} break;
			case Packets.PACKET_AUDIO_STOP: {
				int codecId = packet.readInt();
				Computronics.instance.audio.removePlayer(codecId);
			} break;
			case Packets.PACKET_PARTICLE_SPAWN: {
		        double x = packet.readFloat();
		        double y = packet.readFloat();
		        double z = packet.readFloat();
		        double vx = packet.readFloat();
		        double vy = packet.readFloat();
		        double vz = packet.readFloat();
		        String name = packet.readString();
		        Minecraft.getMinecraft().thePlayer.getEntityWorld().spawnParticle(name, x, y, z, vx, vy, vz);
			} break;
		}
	}
}
