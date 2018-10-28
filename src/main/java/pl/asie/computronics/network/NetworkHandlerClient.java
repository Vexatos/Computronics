package pl.asie.computronics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketClientHandler;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.oc.driver.DriverCardNoise;
import pl.asie.computronics.oc.driver.DriverCardSoundBase;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.PortableDriveManager;
import pl.asie.computronics.tape.PortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class NetworkHandlerClient extends MessageHandlerBase {

	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(48000, 8, 1, false, false);

	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
		PacketType type = PacketType.of(command);
		if(type == null) {
			return;
		}
		switch(type) {
			case TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntity();
				State state = State.VALUES[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case AUDIO_DATA: {
				int handlerId = packet.readShort();
				AudioPacketClientHandler packetHandler = AudioPacketRegistry.INSTANCE.getClientHandler(handlerId);
				if(packetHandler != null) {
					packetHandler.receivePacket(packet);
				}
			}
			break;
			case AUDIO_STOP: {
				int managerId = packet.readInt();
				int codecId = packet.readInt();
				AudioPacketRegistry.INSTANCE.getManager(managerId).removePlayer(codecId);
			}
			break;
			case PARTICLE_SPAWN: {
				double x = packet.readFloat();
				double y = packet.readFloat();
				double z = packet.readFloat();
				double vx = packet.readFloat();
				double vy = packet.readFloat();
				double vz = packet.readFloat();
				int particle = packet.readInt();
				Minecraft.getMinecraft().player.getEntityWorld().spawnParticle(EnumParticleTypes.getParticleFromId(particle), x, y, z, vx, vy, vz);
			}
			break;
			case COMPUTER_BEEP: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					DriverCardSoundBase.onSound(packet, player);
				}
			}
			break;
			case COMPUTER_NOISE: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					DriverCardNoise.onSound(packet, player);
				}
			}
			break;
			case COMPUTER_BOOM: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					Computronics.proxy.goBoom(packet);
				}
			}
			break;
			case TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, false);
				}
			}
			break;
			case PORTABLE_TAPE_STATE: {
				PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getTapeDrive(packet.readString(), true);
				State state = State.VALUES[packet.readUnsignedByte()];
				if(tapeDrive != null) {
					tapeDrive.switchState(state);
					tapeDrive.setSourceIdClient(packet.readInt());
				}
			}
			break;
		}
	}
}
