package pl.asie.computronics;

import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.TileChatBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

public class ComputronicsEventHandler {
	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event) {
		for(Object o: event.player.worldObj.loadedTileEntityList) {
			if(o instanceof TileChatBox) {
				TileChatBox te = (TileChatBox)o;
				if(event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < Computronics.CHATBOX_DISTANCE) {
					te.receiveChatMessage(event);
				}
			}
		}
	}
}
