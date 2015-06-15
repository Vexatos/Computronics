package pl.asie.computronics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets.Types;
import pl.asie.computronics.oc.DriverCardSound;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.NBTUtils;
import pl.asie.computronics.util.internal.ITapeDrive;
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
				int type = packet.readInt();
				State state;
				if(type == Types.TileEntity) {
					TileEntity entity = packet.readTileEntityServer();
					state = State.values()[packet.readUnsignedByte()];
					if(entity instanceof ITapeDrive) {
						((ITapeDrive) entity).switchState(state);
					}
				} else if(type == Types.Entity) {
					int dimensionId = packet.readInt();
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(dimensionId);
					Entity entity = world == null ? null : world.getEntityByID(packet.readInt());
					state = State.values()[packet.readUnsignedByte()];
					if(entity instanceof ITapeDrive) {
						((ITapeDrive) entity).switchState(state);
					}
				} else {
					state = State.values()[packet.readUnsignedByte()];
					ItemStack stack = NBTUtils.readItemStack(packet);
					if(stack != null && stack.getItem() instanceof ITapeDrive) {
						NBTTagCompound data = stack.getTagCompound();
						if(data == null) {
							data = new NBTTagCompound();
						}
						data.setInteger("state", state.ordinal());
					}
				}
			}
			break;
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
				String sourceName = "dfpwm_" + codecId;
				StreamingAudioPlayer codec = Computronics.instance.audio.getPlayer(codecId);

				if(dimId != WorldUtils.getCurrentClientDimension()) {
					return;
				}

				codec.decompress(audio, data, 0, 0, packetSize);
				for(int i = 0; i < (packetSize * 8); i++) {
					// Convert signed to unsigned data
					audio[i] = (byte) (((int) audio[i] & 0xFF) ^ 0x80);
				}

				if((codec.lastPacketId + 1) != packetId) {
					codec.reset();
				}
				codec.setSampleRate(packetSize * 32);
				codec.setDistance((float) Config.TAPEDRIVE_DISTANCE);
				codec.setVolume(volume / 127.0F);
				codec.playPacket(audio, x, y, z);
				codec.lastPacketId = packetId;
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
