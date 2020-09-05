package pl.asie.computronics.integration.railcraft.gui.slot;

import mods.railcraft.common.gui.slots.SlotSecure;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * @author Vexatos
 */
public class SlotSecureInput extends SlotSecure {

	public SlotSecureInput(Predicate<ItemStack> filter, IInventory contents, int id, int x, int y) {
		super(filter, contents, id, x, y);
		this.setStackLimit(64);
	}
}
