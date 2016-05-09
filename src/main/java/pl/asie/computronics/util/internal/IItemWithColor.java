package pl.asie.computronics.util.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Vexatos
 */
public interface IItemWithColor {

	@SideOnly(Side.CLIENT)
	int getColorFromItemstack(ItemStack stack, int tintIndex);

}
