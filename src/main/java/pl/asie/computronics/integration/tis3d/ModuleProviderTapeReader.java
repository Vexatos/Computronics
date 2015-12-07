package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.Casing;
import li.cil.tis3d.api.Face;
import li.cil.tis3d.api.module.Module;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class ModuleProviderTapeReader extends ModuleProviderBlock {

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face, Block block) {
		return block == Computronics.tapeReader;
	}

	@Override
	public Module createModule(ItemStack stack, Casing casing, Face face) {
		return new ModuleTapeReader(casing, face);
	}
}
