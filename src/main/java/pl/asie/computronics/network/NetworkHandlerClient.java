package pl.asie.computronics.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;

import javax.sound.sampled.AudioFormat;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.DriverCardSound;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import pl.asie.lib.util.WorldUtils;

public class NetworkHandlerClient extends MessageHandlerBase {
	private class CodecData {
		public final int x, y, z;
		public final byte[] data;

		public CodecData(int x, int y, int z, byte[] data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.data = data;
		}
	}

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
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case Packets.PACKET_AUDIO_DATA: {
				int dimId = packet.readInt();
				int packetId = packet.readInt();
				int sampleRate = packet.readInt();
				short packetSize = packet.readShort();
				byte[] data = packet.readByteArrayData(packetSize);
				short receivers = packet.readShort();

				if (dimId != WorldUtils.getCurrentClientDimension()) {
					for (int i = 0; i < receivers; i++) {
						packet.readInt();
						packet.readInt();
						packet.readInt();
						packet.readInt();
						packet.readShort();
						packet.readByte();
					}

					return;
				}

				Map<StreamingAudioPlayer, CodecData> codecs = new HashMap<StreamingAudioPlayer, CodecData>();

				for (int j = 0; j < receivers; j++) {
					int codecId = packet.readInt();
					int x = packet.readInt();
					int y = packet.readInt();
					int z = packet.readInt();
					int distance = packet.readUnsignedShort();
					byte volume = packet.readByte();

					byte[] audio = new byte[packetSize * 8];
					StreamingAudioPlayer codec = Computronics.instance.audio.getPlayer(codecId);
					codec.decompress(audio, data, 0, 0, packetSize);
					for (int i = 0; i < (packetSize * 8); i++) {
						// Convert signed to unsigned data
						audio[i] = (byte) (((int) audio[i] & 0xFF) ^ 0x80);
					}

					codec.setSampleRate(sampleRate);
					codec.setDistance((float) distance);
					codec.setVolume(volume / 127.0F);
					codec.lastPacketId = packetId;
					codecs.put(codec, new CodecData(x, y, z, audio));
				}

				for (StreamingAudioPlayer codec : codecs.keySet()) {
					CodecData cd = codecs.get(codec);
					codec.playPacket(cd.data, cd.x, cd.y, cd.z);
				}
			}
			break;
			case Packets.PACKET_AUDIO_STOP: {
				int codecId = packet.readInt();
				Computronics.instance.audio.removePlayer(codecId);
			}
			break;
			case Packets.PACKET_PARTICLE_SPAWN: {
				double x = packet.readFloat();
				double y = packet.readFloat();
				double z = packet.readFloat();
				double vx = packet.readFloat();
				double vy = packet.readFloat();
				double vz = packet.readFloat();
				String name = packet.readString();
				Minecraft.getMinecraft().thePlayer.getEntityWorld().spawnParticle(name, x, y, z, vx, vy, vz);
			}
			break;
			case Packets.PACKET_COMPUTER_BEEP: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					DriverCardSound.onSound(packet, player);
				}
			}
			break;
			case Packets.PACKET_COMPUTER_BOOM: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					Computronics.proxy.goBoom(packet);
				}
			}
			break;
			case Packets.PACKET_TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, false);
				}
			}
			break;
		}
	}
}
