package pl.asie.computronics.item.block;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IBlockWithSpecialText {

	public boolean hasSubTypes();

	public String getTranslationKey(ItemStack stack);
}
