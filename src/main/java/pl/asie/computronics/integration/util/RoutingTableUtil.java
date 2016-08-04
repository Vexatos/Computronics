package pl.asie.computronics.integration.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Vexatos
 */
public class RoutingTableUtil {
	public static String getRoutingTableTitle(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			String title = nbt.getString("title");
			if(title != null) {
				return title;
			}
		}
		return "";
	}

	public static boolean setRoutingTableTitle(ItemStack stack, String title) {
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(title != null && !title.isEmpty()) {
				nbt.setString("title", title);
				stack.setTagCompound(nbt);
				return true;
			}
		}
		return false;
	}
}
