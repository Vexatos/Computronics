package pl.asie.computronics.oc.block;

import li.cil.oc.api.driver.Block;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class DriverBlockEnvironments implements Block, EnvironmentAware {
	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)
			|| !(((ItemBlock) stack.getItem()).field_150939_a instanceof IComputronicsEnvironmentBlock)) {
			return null;
		}

		return ((IComputronicsEnvironmentBlock) ((ItemBlock) stack.getItem()).field_150939_a)
			.getTileEntityClass(stack.getItemDamage());
	}
}
