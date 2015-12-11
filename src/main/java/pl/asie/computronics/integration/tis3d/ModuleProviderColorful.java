package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class ModuleProviderColorful extends ModuleProviderBlock {

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face, Block block) {
		return block == Computronics.colorfulLamp;
	}

	@Override
	public Module createModule(ItemStack stack, Casing casing, Face face) {
		return new ModuleColorful(casing, face);
	}
}
