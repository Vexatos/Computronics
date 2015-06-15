package pl.asie.computronics.network;

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
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.internal.ITapeDrive;
import pl.asie.computronics.util.internal.ITapeDriveItem;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class NetworkHandlerServer extends MessageHandlerBase {
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
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
				} else {
					int dimensionId = packet.readInt();
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(dimensionId);
					Entity entity = world == null ? null : world.getEntityByID(packet.readInt());
					state = State.values()[packet.readUnsignedByte()];
					if(type == Types.Entity && entity instanceof ITapeDrive) {
						((ITapeDrive) entity).switchState(state);
					} else if(type == Types.Item && entity instanceof EntityPlayer
						&& ((EntityPlayer) entity).inventory != null) {
						ItemStack stack = ((EntityPlayer) entity).getCurrentEquippedItem();
						if(stack != null && stack.getItem() instanceof ITapeDriveItem) {
							NBTTagCompound data = stack.getTagCompound();
							if(data == null) {
								data = new NBTTagCompound();
							}
							data.setInteger("computronics:state", state.ordinal());
							stack.setTagCompound(data);
						}
					}
				}
			}
			break;
			case Packets.PACKET_TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, true);
				}
			}
			break;
			case Packets.PACKET_TICKET_PRINT: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.printTicket(packet, player, true);
				}
			}
			break;
		}
	}
}
