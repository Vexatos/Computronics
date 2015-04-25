package pl.asie.computronics.network;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class NetworkHandlerServer extends MessageHandlerBase {
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
		switch(command) {
			case Packets.PACKET_TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntityServer();
				State state = State.values()[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case Packets.PACKET_TICKET_SYNC: {
				if(Loader.isModLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player);
				}
			}
			break;
		}
	}
}
