package pl.asie.computronics.gui.container;

import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.integration.railcraft.slot.PaperSlotFilter;
import pl.asie.computronics.integration.railcraft.slot.SlotSecureInput;
import pl.asie.computronics.tile.TileTicketMachine;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;

/**
 * @author Vexatos
 */
public class ContainerTicketMachine extends ContainerBase {

	private final InventoryPlayer inventoryPlayer;
	private boolean isPaperLocked = true;
	private boolean isSelectLocked = true;

	public ContainerTicketMachine(TileEntityBase entity, InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.inventoryPlayer = inventoryPlayer;
		IInventory inventory = (IInventory) entity;
		for(int i = 0; i < 5; i++) {
			this.addSlotToContainer(new SlotSecure(ItemTicketGold.FILTER, inventory, i, 33 + (i * 18), 15));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlotToContainer(new SlotSecure(ItemTicketGold.FILTER, inventory, i + 5, 33 + (i * 18), 33));
		}
		this.addSlotToContainer(new SlotSecureInput(PaperSlotFilter.FILTER, inventory, 10, 144, 15));
		this.addSlotToContainer(new SlotOutput(inventory, 11, 144, 54));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
		if(getEntity() instanceof TileTicketMachine) {
			TileTicketMachine machine = (TileTicketMachine) getEntity();
			if(!machine.isOwner(player.getGameProfile())) {
				setTicketsLocked(true);
				setPaperLocked(true);
			} else {
				setTicketsLocked(false);
				setPaperLocked(false);
			}
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	public void setTicketsLocked(boolean locked) {
		if(isSelectLocked == locked) {
			return;
		}
		for(int i = 0; i < 10; i++) {
			Object slot = this.inventorySlots.get(i);
			if(slot instanceof SlotSecure) {
				((SlotSecure) slot).locked = locked;
			}
		}
		isSelectLocked = locked;
	}

	public void setPaperLocked(boolean locked) {
		if(isPaperLocked == locked) {
			return;
		}
		Object slot = this.inventorySlots.get(10);
		if(slot instanceof SlotSecureInput) {
			((SlotSecureInput) slot).locked = locked;
		}
		isPaperLocked = locked;
	}

	public InventoryPlayer getInventoryPlayer() {
		return this.inventoryPlayer;
	}
}
