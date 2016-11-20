package pl.asie.lib.util.color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemColorizer {

	/**
     * Return whether the specified armor ItemStack has a color.
	 */
	public static boolean hasColor(ItemStack stack) {
		return (stack.hasTagCompound() && (stack.getTagCompound().hasKey("display") && stack.getTagCompound().getCompoundTag("display").hasKey("color")));
	}

	/**
	 * Return the color for the specified armor ItemStack.
	 */
	public static int getColor(ItemStack stack) {
		NBTTagCompound stackCompound = stack.getTagCompound();

        if(stackCompound != null) {
            NBTTagCompound displayCompound = stackCompound.getCompoundTag("display");
            return displayCompound.hasKey("color") ? displayCompound.getInteger("color") : -1;
        } else {
            return -1;
        }
	}

	public static void removeColor(ItemStack par1ItemStack) {
		NBTTagCompound stackCompound = par1ItemStack.getTagCompound();

		if(stackCompound != null) {
			NBTTagCompound displayCompound = stackCompound.getCompoundTag("display");
            if(displayCompound.hasKey("color")) {
                displayCompound.removeTag("color");
            }
		}
	}

	public static void setColor(ItemStack par1ItemStack, int par2) {
		NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

		if(nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
			par1ItemStack.setTagCompound(nbttagcompound);
		}

		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

		if(!nbttagcompound.hasKey("display")) {
			nbttagcompound.setTag("display", nbttagcompound1);
		}

		nbttagcompound1.setInteger("color", par2);
	}
}
