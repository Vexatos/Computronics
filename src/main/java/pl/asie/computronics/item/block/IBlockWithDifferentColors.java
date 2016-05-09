package pl.asie.computronics.item.block;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IBlockWithDifferentColors {

	boolean hasSubTypes();

	int getColorFromItemstack(ItemStack stack, int pass);
}
