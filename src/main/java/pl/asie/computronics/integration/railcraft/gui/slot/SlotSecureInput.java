package pl.asie.computronics.integration.railcraft.gui.slot;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * @author Vexatos
 */
public class SlotSecureInput extends SlotStackFilter {

	public boolean locked = true;

	public SlotSecureInput(IStackFilter filter, IInventory contents, int id, int x, int y) {
		super(filter, contents, id, x, y);
	}

	public int getSlotStackLimit() {
		return 1;
	}

	public boolean canTakeStack(EntityPlayer player) {
		return !this.locked;
	}

	public boolean canShift() {
		return !this.locked;
	}
}
