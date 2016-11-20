package pl.asie.computronics.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pl.asie.computronics.reference.Config;

public class ChatBoxUtils {

	public static void sendChatMessage(World world, double xCoord, double yCoord, double zCoord, int distance, String prefix, String string, boolean sendToAll) {
		if(world.isRemote) {
			return;
		}
		distance = Math.min(distance, 32767);
		String text = TextFormatting.GRAY + "" + TextFormatting.ITALIC + "[" + prefix + "] ";
		text += TextFormatting.RESET + "" + TextFormatting.GRAY + string;
		TextComponentString component = new TextComponentString(text);
		component.setStyle(component.getStyle().setColor(TextFormatting.GRAY));
		try {
			for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
				if(player == null) {
					continue;
				}
				if(sendToAll || (player.world.provider.getDimension() == world.provider.getDimension()
					&& player.getDistanceSq(xCoord, yCoord, zCoord) < distance * distance)) {
					player.sendStatusMessage(component.createCopy(), false);
				}
			}
		} catch(Exception e) {
			// NO-OP
		}
	}

	public static void sendChatMessage(World world, double xCoord, double yCoord, double zCoord, int distance, String prefix, String string) {
		sendChatMessage(world, xCoord, yCoord, zCoord, distance, prefix, string, Config.CHATBOX_MAGIC);
	}

	public static void sendChatMessage(TileEntity te, int d, String prefix, String string, boolean sendToAll) {
		final BlockPos pos = te.getPos();
		sendChatMessage(te.getWorld(), pos.getX(), pos.getY(), pos.getZ(), d, prefix, string, sendToAll);
	}
}
