package pl.asie.computronics.tape;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.tile.TileTapeDrive;

/**
 * @author Vexatos
 */
public class TapeStorageEventHandler {

	@SubscribeEvent
	public void handleTapeStorageSaving(WorldEvent.Unload event) {
		if(event.getWorld().isRemote) {
			return;
		}

		for(TileEntity tile : event.getWorld().loadedTileEntityList) {
			if(tile instanceof TileTapeDrive && !tile.isInvalid()) {
				((TileTapeDrive) tile).saveStorage();
			}
		}
	}
}
