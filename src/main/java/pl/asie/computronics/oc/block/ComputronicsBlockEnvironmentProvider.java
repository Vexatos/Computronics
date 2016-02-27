package pl.asie.computronics.oc.block;

import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public class ComputronicsBlockEnvironmentProvider implements EnvironmentProvider {

	@Override
	public Class<?> getEnvironment(ItemStack stack) {
		if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)
			|| !(((ItemBlock) stack.getItem()).block instanceof IComputronicsEnvironmentBlock)) {
			return null;
		}

		return ((IComputronicsEnvironmentBlock) ((ItemBlock) stack.getItem()).block)
			.getTileEntityClass(stack.getItemDamage());
	}
}
