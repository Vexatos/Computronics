package pl.asie.computronics.integration.railcraft.gui.slot;

import mods.railcraft.api.core.items.IStackFilter;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public class PaperSlotFilter implements IStackFilter {

	public static final PaperSlotFilter FILTER = new PaperSlotFilter();

	@Override
	public boolean matches(ItemStack stack) {
		return stack != null && stack.getItem().equals(Items.paper);
	}
}
