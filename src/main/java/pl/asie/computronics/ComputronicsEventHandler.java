package pl.asie.computronics;

import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Environment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import pl.asie.computronics.tile.TileChatBoxBase;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ComputronicsEventHandler implements IChatListener {
	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event) {
		for(Object o: event.player.worldObj.loadedTileEntityList) {
			if(o instanceof TileChatBoxBase) {
				TileChatBoxBase te = (TileChatBoxBase)o;
				if(te.isCreative() || event.player.getDistance(te.xCoord, te.yCoord, te.zCoord) < Computronics.CHATBOX_DISTANCE) {
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
	
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void onSound(SoundLoadEvent event) {
		event.manager.addSound("computronics:tape_eject.ogg");
		event.manager.addSound("computronics:tape_rewind.ogg");
		event.manager.addSound("computronics:tape_insert.ogg");
		event.manager.addSound("computronics:tape_button.ogg");
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		if(!(message.message.startsWith("/"))) return message;
		
		for(Object o: handler.getPlayer().worldObj.loadedTileEntityList) {
			if(o instanceof TileChatBoxBase) {
				TileChatBoxBase te = (TileChatBoxBase)o;
				if(te.isCreative() || (handler.getPlayer().getDistance(te.xCoord, te.yCoord, te.zCoord) < Computronics.CHATBOX_DISTANCE && message.message.startsWith("/me") && Computronics.CHATBOX_ME_DETECT)) {
					te.receiveChatMessage(new ServerChatEvent((EntityPlayerMP)handler.getPlayer(), message.message, ChatMessageComponent.createFromText(message.message)));
				}
			}
		}
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}
}
