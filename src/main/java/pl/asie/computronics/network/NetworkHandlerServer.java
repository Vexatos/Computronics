package pl.asie.computronics.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.PortableDriveManager;
import pl.asie.computronics.tape.PortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class NetworkHandlerServer extends MessageHandlerBase {

	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
		PacketType type = PacketType.of(command);
		if(type == null) {
			return;
		}
		switch(type) {
			case TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntityServer();
				State state = State.VALUES[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, true);
				}
			}
			break;
			case TICKET_PRINT: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.printTicket(packet, player, true);
				}
			}
			break;
			case PORTABLE_TAPE_STATE: {
				PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getTapeDrive(packet.readString(), false);
				State state = State.VALUES[packet.readUnsignedByte()];
				if(tapeDrive != null) {
					tapeDrive.switchState(state);
				}
			}
			break;
		}
	}
}
