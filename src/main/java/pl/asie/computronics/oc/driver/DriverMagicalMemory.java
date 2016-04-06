package pl.asie.computronics.oc.driver;

import li.cil.oc.api.driver.item.Memory;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public class DriverMagicalMemory extends DriverOCSpecialPart implements Memory {

	public DriverMagicalMemory() {
		super(0);
	}

	@Override
	public double amount(ItemStack stack) {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		return null;
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Memory;
	}

	@Override
	public int tier(ItemStack stack) {
		return 0;
	}
}
