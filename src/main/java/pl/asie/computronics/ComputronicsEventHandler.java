package pl.asie.computronics;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.TileChatBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

public class ComputronicsEventHandler {
	@SubscribeEvent
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
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onSound(SoundLoadEvent event) {
		/* event.manager.addSound("computronics:tape_eject.ogg");
		event.manager.addSound("computronics:tape_rewind.ogg");
		event.manager.addSound("computronics:tape_insert.ogg");
		event.manager.addSound("computronics:tape_button.ogg"); */
	}
}
