package pl.asie.computronics;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.network.Environment;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileChatBox;

import java.util.ArrayList;

public class ChatBoxHandler {
	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		//System.out.println("event");
		ArrayList<Object> tiles = new ArrayList<Object>(event.player.worldObj.loadedTileEntityList);
		for(Object o : tiles) {
			if(o instanceof TileChatBox) {
				TileChatBox te = (TileChatBox) o;
				if(te.isCreative() || event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < te.getDistance()) {
					te.receiveChatMessage(event);
				}
			} else if(Loader.isModLoaded(Mods.OpenComputers)) {
				this.chatEventRobot(event, o);
			}
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void chatEventRobot(ServerChatEvent event, Object o) {
		if(o instanceof Robot) {
			Robot r = (Robot) o;
			TileEntity te = (TileEntity) o;
			if(event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < Computronics.CHATBOX_DISTANCE) {
				for(int i = 0; i < r.getSizeInventory(); i++) {
					Environment e = r.getComponentInSlot(i);
					if(e instanceof RobotUpgradeChatBox) {
						((RobotUpgradeChatBox) e).receiveChatMessage(event);
					}
				}
			}
		}
	}
}
