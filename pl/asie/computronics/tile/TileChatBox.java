package pl.asie.computronics.tile;

import pl.asie.lib.block.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class TileChatBox extends TileEntityBase {
	public void sendChatMessage(String string, int distance) {
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.setColor(EnumChatFormatting.GRAY);
		chat.setItalic(true);
		chat.addText(EnumChatFormatting.ITALIC + "[ChatBox] ");
		chat.addText(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + string);
		for(Object o: this.worldObj.playerEntities) {
			if(!(o instanceof EntityPlayer)) continue;
			EntityPlayer player = (EntityPlayer)o;
			if(player.getDistance(this.xCoord, this.yCoord, this.zCoord) < distance) {
				player.sendChatToPlayer(chat);
			}
		}
	}
}
