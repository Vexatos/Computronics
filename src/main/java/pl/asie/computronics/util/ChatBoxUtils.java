package pl.asie.computronics.util;

import li.cil.oc.api.driver.EnvironmentHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ChatBoxUtils {

	public static void sendChatMessage(World worldObj, double xCoord, double yCoord, double zCoord, int distance, String prefix, String string) {
		String text = EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "[" + prefix + "] ";
		text += EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + string;
		for(Object o : worldObj.playerEntities) {
			if(!(o instanceof EntityPlayer)) {
				continue;
			}
			EntityPlayer player = (EntityPlayer) o;
			if(player.getDistance(xCoord, yCoord, zCoord) < distance) {
				ChatComponentText component = new ChatComponentText(text);
				component.setChatStyle(component.getChatStyle().setColor(EnumChatFormatting.GRAY));
				player.addChatMessage(component);
			}
		}
	}

	public static void sendChatMessage(TileEntity te, int d, String prefix, String string) {
		if(te == null) {
			return;
		}
		sendChatMessage(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, d, prefix, string);
	}

	public static void sendChatMessage(EnvironmentHost c, int d, String prefix, String string) {
		if(c == null) {
			return;
		}
		sendChatMessage(c.world(), c.xPosition(), c.yPosition(), c.zPosition(), d, prefix, string);
	}
}
