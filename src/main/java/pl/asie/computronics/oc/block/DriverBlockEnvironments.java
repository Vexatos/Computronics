package pl.asie.computronics.oc.block;

import li.cil.oc.api.driver.Block;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class DriverBlockEnvironments implements Block, EnvironmentAware {

	@Override
	public boolean worksWith(World world, BlockPos pos) {
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
		return null;
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)
			|| !(((ItemBlock) stack.getItem()).block instanceof IComputronicsEnvironmentBlock)) {
			return null;
		}

		return ((IComputronicsEnvironmentBlock) ((ItemBlock) stack.getItem()).block)
			.getTileEntityClass(stack.getItemDamage());
	}
}
