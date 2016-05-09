package pl.asie.lib.util;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SlotTyped extends Slot {

	private Object[] allowedTypes;

	public SlotTyped(IInventory par1iInventory, int par2, int par3, int par4, Object[] allowedTypes) {
		super(par1iInventory, par2, par3, par4);
		this.allowedTypes = allowedTypes;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		for(Object o : allowedTypes) {
			if(o instanceof String) {
				// OreDict entry
				String s = (String) o;
				for(ItemStack target : OreDictionary.getOres(s)) {
					if(OreDictionary.itemMatches(target, stack, false)) {
						return true;
					}
				}
			} else if(o instanceof Block) {
				return (Block.getBlockFromItem(stack.getItem()).equals((Block) o));
			} else if(o instanceof Item) {
				return (stack.getItem().equals((Item) o));
			} else if(o instanceof ItemStack) {
				return OreDictionary.itemMatches(((ItemStack) o), stack, true);
			}
		}
		return false;
	}
}
