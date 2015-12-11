package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public abstract class ModuleProviderBlock implements ModuleProvider {

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face) {
		return stack.getItem() != null && worksWith(stack, casing, face, Block.getBlockFromItem(stack.getItem()));
	}

	public abstract boolean worksWith(ItemStack stack, Casing casing, Face face, Block block);
}
