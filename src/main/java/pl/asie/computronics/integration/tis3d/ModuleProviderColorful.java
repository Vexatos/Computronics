package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.Casing;
import li.cil.tis3d.api.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class ModuleProviderColorful implements ModuleProvider {

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face) {
		return stack.getItem() != null && Block.getBlockFromItem(stack.getItem()) == Computronics.colorfulLamp;
	}

	@Override
	public Module createModule(ItemStack stack, Casing casing, Face face) {
		return new ModuleColorful(casing, face);
	}
}
