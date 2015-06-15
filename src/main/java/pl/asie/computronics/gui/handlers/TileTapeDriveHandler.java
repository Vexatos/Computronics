package pl.asie.computronics.gui.handlers;

import net.minecraft.entity.player.EntityPlayer;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.network.Packets.Types;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class TileTapeDriveHandler implements TapeGuiHandler {

	private final TileTapeDrive tile;

	public TileTapeDriveHandler(TileTapeDrive tile) {
		this.tile = tile;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUseableByPlayer(player);
	}

	@Override
	public boolean exists() {
		return tile != null;
	}

	@Override
	public State getEnumState() {
		return tile.getEnumState();
	}

	@Override
	public void switchState(State state) {
		tile.switchState(state);
	}

	@Override
	public void writeLocation(Packet packet) throws IOException {
		packet.writeInt(Types.TileEntity).writeTileLocation(tile);
	}

	@Override
	public int getSyncPacketID() {
		return Packets.PACKET_TAPE_GUI_STATE;
	}
}
