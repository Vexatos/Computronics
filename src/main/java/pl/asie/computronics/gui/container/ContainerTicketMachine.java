package pl.asie.computronics.gui.container;

import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import pl.asie.computronics.integration.railcraft.slot.PaperSlotFilter;
import pl.asie.computronics.integration.railcraft.slot.SlotSecureInput;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;

/**
 * @author Vexatos
 */
public class ContainerTicketMachine extends ContainerBase {

	private final InventoryPlayer inventoryPlayer;

	public ContainerTicketMachine(TileEntityBase entity, InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.inventoryPlayer = inventoryPlayer;
		IInventory inventory = (IInventory) entity;
		for(int i = 0; i < 5; i++) {
			this.addSlotToContainer(new SlotSecure(ItemTicketGold.FILTER, inventory, i, 35 + (i * 18), 24));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlotToContainer(new SlotSecure(ItemTicketGold.FILTER, inventory, i + 5, 35 + (i * 18), 48));
		}
		this.addSlotToContainer(new SlotSecureInput(PaperSlotFilter.FILTER, inventory, 10, 161, 34));
		this.addSlotToContainer(new SlotOutput(inventory, 11, 161, 68));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

	public void setTicketsLocked(boolean locked) {
		for(int i = 0; i < 10; i++) {
			Object slot = this.inventorySlots.get(i);
			if(slot instanceof SlotSecure) {
				((SlotSecure) slot).locked = locked;
			}
		}
	}

	public void setPaperLocked(boolean locked) {
		Object slot = this.inventorySlots.get(10);
		if(slot instanceof SlotSecureInput) {
			((SlotSecureInput) slot).locked = locked;
		}
	}

	public InventoryPlayer getInventoryPlayer() {
		return this.inventoryPlayer;
	}
}
