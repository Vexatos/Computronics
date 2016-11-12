package pl.asie.computronics.integration.railcraft.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author Vexatos
 */
@SuppressWarnings("Since15")
public class PaperSlotFilter implements Predicate<ItemStack> {

	public static final PaperSlotFilter FILTER = new PaperSlotFilter();

	@Override
	public boolean test(@Nullable ItemStack stack) {
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
