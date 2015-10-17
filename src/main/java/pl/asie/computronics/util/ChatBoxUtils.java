package pl.asie.computronics.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Config;

public class ChatBoxUtils {

	public static void sendChatMessage(World worldObj, double xCoord, double yCoord, double zCoord, int distance, String prefix, String string, boolean sendToAll) {
		if(worldObj.isRemote) {
			return;
		}
		distance = Math.min(distance, 32767);
		String text = EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "[" + prefix + "] ";
		text += EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + string;
		ChatComponentText component = new ChatComponentText(text);
		component.setChatStyle(component.getChatStyle().setColor(EnumChatFormatting.GRAY));
		try {
			for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if(!(o instanceof EntityPlayer)) {
					continue;
				}
				EntityPlayer player = (EntityPlayer) o;
				if(sendToAll || (player.worldObj.provider.dimensionId == worldObj.provider.dimensionId
					&& player.getDistanceSq(xCoord, yCoord, zCoord) < distance * distance)) {
					player.addChatMessage(component.createCopy());
				}
			}
		} catch(Exception e) {
			// NO-OP
		}
	}

	public static void sendChatMessage(World worldObj, double xCoord, double yCoord, double zCoord, int distance, String prefix, String string) {
		sendChatMessage(worldObj, xCoord, yCoord, zCoord, distance, prefix, string, Config.CHATBOX_MAGIC);
	}

	public static void sendChatMessage(TileEntity te, int d, String prefix, String string, boolean sendToAll) {
		if(te == null) {
			return;
		}
		sendChatMessage(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, d, prefix, string, sendToAll);
	}
}
