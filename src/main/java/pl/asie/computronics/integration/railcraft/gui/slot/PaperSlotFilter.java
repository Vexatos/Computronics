package pl.asie.computronics.integration.railcraft.gui.slot;

import mods.railcraft.api.core.StackFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class PaperSlotFilter extends StackFilter {

	public static final PaperSlotFilter FILTER = new PaperSlotFilter();

	@Override
	public boolean apply(@Nullable ItemStack stack) {
		if(stack == null) {
			return false;
		}
		int paperID = OreDictionary.getOreID("paper");
		for(int id : OreDictionary.getOreIDs(stack)) {
			if(id == paperID) {
				return true;
			}
		}
		return false;
	}
}
