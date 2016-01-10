package pl.asie.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CrossModUtils {
	public static void renameItemStack(ItemStack target, String name) {
		if(target == null) return;
		Item item = target.getItem();
		item.setUnlocalizedName(name);
		if(item instanceof ItemBlock) {
			Block.getBlockFromItem(((ItemBlock)item)).setBlockName(name);
		}
	}
	
	public static void renameItem(Item target, String name) {
		renameItemStack(new ItemStack(target, 1, 0), name);
	}
}
