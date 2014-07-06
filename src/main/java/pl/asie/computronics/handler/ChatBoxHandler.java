package pl.asie.computronics.handler;

import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Environment;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

public class ChatBoxHandler {
	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		for(Object o: event.player.worldObj.loadedTileEntityList) {
			if(o instanceof TileChatBox) {
				TileChatBox te = (TileChatBox)o;
				if(te.isCreative() || event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < te.getDistance()) {
					te.receiveChatMessage(event);
				}
			} else if(o instanceof Robot && o instanceof TileEntity) {
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
