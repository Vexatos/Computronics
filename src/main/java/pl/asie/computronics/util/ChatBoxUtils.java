package pl.asie.computronics.util;

import li.cil.oc.api.driver.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.lib.util.ChatUtils;

public class ChatBoxUtils {
	public static void sendChatMessage(World worldObj, int xCoord, int yCoord, int zCoord, int d, String string) {
		if(worldObj == null) return;
		
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.setColor(EnumChatFormatting.GRAY);
		chat.setItalic(true);
		chat.addText(EnumChatFormatting.ITALIC + Computronics.CHATBOX_PREFIX + " ");
		chat.addText(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + ChatUtils.color(string));
		
		for(Object o: worldObj.playerEntities) {
			if(!(o instanceof EntityPlayer)) continue;
			EntityPlayer player = (EntityPlayer)o;
			if(player.getDistance(xCoord, yCoord, zCoord) < d) {
				player.sendChatToPlayer(chat);
			}
		}
	}
	
	public static void sendChatMessage(TileEntity te, int d, String string) {
		if(te == null) return;
		sendChatMessage(te.worldObj, te.xCoord, te.yCoord, te.zCoord, d, string);
	}
	
	public static void sendChatMessage(Container c, int d, String string) {
		if(c == null) return;
		sendChatMessage(c.world(), (int)Math.round(c.xPosition()), (int)Math.round(c.yPosition()), (int)Math.round(c.zPosition()), d, string);
	}
}
