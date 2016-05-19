package pl.asie.lib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 * @author Vexatos
 */
public class FluidUtils {

	@SuppressWarnings("deprecation")
	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		Item item = stack.getItem();
		FluidStack fstack = null;
		if(item instanceof IFluidContainerItem) {
			fstack = ((IFluidContainerItem) item).getFluid(stack);
		}
		if(fstack == null) {
			fstack = net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem(stack);
		}
		return fstack != null && fstack.getFluid() == fluid;
	}
}
