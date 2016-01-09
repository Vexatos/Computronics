package pl.asie.computronics.integration.tis3d.manual;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IModuleWithPrefix extends IModuleWithDocumentation {

	public String getPrefix(ItemStack stack);
}
