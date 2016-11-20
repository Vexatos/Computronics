package pl.asie.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * @author Vexatos
 */
public class FluidUtils {

	@SuppressWarnings("deprecation")
	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if(handler != null) {
				for(IFluidTankProperties props : handler.getTankProperties()) {
					if(props.getContents() != null && props.getContents().getFluid() == fluid) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
