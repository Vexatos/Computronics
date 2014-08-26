package pl.asie.computronics;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Environment;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import pl.asie.computronics.tile.TileChatBox;

public class ChatBoxHandler {
	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		//System.out.println("event");
		for(Object o: event.player.worldObj.loadedTileEntityList) {
			if(o instanceof TileChatBox) {
				TileChatBox te = (TileChatBox)o;
				if(te.isCreative() || event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < te.getDistance()) {
					te.receiveChatMessage(event);
				}
			} else if(o instanceof Robot) {
				Robot r = (Robot)o;
				TileEntity te = (TileEntity)o;
				if(event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < Computronics.CHATBOX_DISTANCE) {
					for(int i = 0; i < r.getSizeInventory(); i++) {
						Environment e = r.getComponentInSlot(i);
						if(e instanceof RobotUpgradeChatBox) {
							((RobotUpgradeChatBox)e).receiveChatMessage(event);
						}
					}
				}
			}
		}
	}
}
