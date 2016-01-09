package pl.asie.computronics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketClientHandler;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.oc.DriverCardSound;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class NetworkHandlerClient extends MessageHandlerBase {
	private static class CodecData {
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
		switch(command) {
			case Packets.PACKET_TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntity();
				State state = State.VALUES[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case Packets.PACKET_AUDIO_DATA: {
				int handlerId = packet.readShort();
				AudioPacketClientHandler packetHandler = AudioPacketRegistry.INSTANCE.getClientHandler(handlerId);
				if (packetHandler != null) {
					packetHandler.receivePacket(packet);
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
