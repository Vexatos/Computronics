package pl.asie.computronics.integration.railcraft.gui.slot;

import mods.railcraft.common.gui.slots.SlotSecure;
import net.minecraft.inventory.IInventory;

/**
 * @author Vexatos
 */
public class SlotSecureInput extends SlotSecure {

	public SlotSecureInput(IInventory contents, int id, int x, int y) {
		super(contents, id, x, y);
		this.setStackLimit(64);
	}
}
