package pl.asie.computronics.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class NetworkHandlerServer extends MessageHandlerBase {
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
		switch(command){
			case Packets.PACKET_TAPE_GUI_STATE:{
				byte mode = packet.readByte();
				if(mode == 0) {
					TileEntity entity = packet.readTileEntityServer();
					State state = State.values()[packet.readUnsignedByte()];
					if(entity instanceof TileTapeDrive) {
						TileTapeDrive tile = (TileTapeDrive) entity;
						tile.switchState(state);
					}
				} else {
					String name = packet.readString();
					EntityPlayer p = player.worldObj.getPlayerEntityByName(name);
					ItemStack stack = p.getCurrentEquippedItem();
					if(stack != null && stack.getItem() instanceof ItemPortableTapeDrive) {
						NBTTagCompound tag = stack.getTagCompound();
						tag.setInteger("computronics:state", packet.readUnsignedByte());
						stack.setTagCompound(tag);
					}
				}
			}
			break;
		}
	}
}
