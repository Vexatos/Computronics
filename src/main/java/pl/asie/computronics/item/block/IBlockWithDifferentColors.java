package pl.asie.computronics.item.block;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IBlockWithDifferentColors {

	public boolean hasSubTypes();

	public int getColorFromItemStack(ItemStack stack, int pass);
}
