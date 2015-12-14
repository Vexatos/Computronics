package pl.asie.computronics.integration.tis3d.module;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;

/**
 * @author Vexatos
 */
public class ComputronicsModuleProvider implements ModuleProvider {

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face) {
		return stack.getItem() != null && stack.getItem() == IntegrationTIS3D.itemModules;
	}

	@Override
	public Module createModule(ItemStack stack, Casing casing, Face face) {
		switch(stack.getItemDamage()) {
			case 0:
				return new ModuleColorful(casing, face);
			case 1:
				return new ModuleTapeReader(casing, face);
			default:
				return null;
		}
	}
}
