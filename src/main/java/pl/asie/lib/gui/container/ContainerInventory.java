package pl.asie.lib.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pl.asie.lib.AsieLibMod;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public abstract class ContainerInventory extends Container {

	private final IInventory inventory;
	private final int containerSize;

	public ContainerInventory(@Nullable IInventory inventory) {
		this.inventory = inventory;
		if(inventory != null) {
			this.containerSize = inventory.getSizeInventory();
		} else {
			this.containerSize = 0;
		}
	}

	public int getSize() {
		return containerSize;
	}

	public IInventory getInventoryObject() {
		return inventory;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		if(inventory == null) {
			return ItemStack.EMPTY;
		}

		//ItemStack stack = null;
		Slot slotObject = inventorySlots.get(slot);
		if(slotObject != null && slotObject.getHasStack()) {
			tryTransferStackInSlot(slotObject, slotObject.inventory == this.inventory);
			if(!AsieLibMod.proxy.isClient()) {
				detectAndSendChanges();
			}
		}
			/*ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if(slot < getSize()) {
				if(!this.mergeItemStack(stackInSlot, getSize(), inventorySlots.size(), true)) {
					return null;
				}
			} else if(!this.mergeItemStack(stackInSlot, 0, getSize(), false)) {
				return null;
			}
			if(stackInSlot.getCount() == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}*/
		return ItemStack.EMPTY;
	}

	//Mostly stolen from Sangar
	protected void tryTransferStackInSlot(Slot from, boolean intoPlayerInventory) {
		ItemStack fromStack = from.getStack();
		boolean somethingChanged = false;

		int step = intoPlayerInventory ? -1 : 1;
		int begin = intoPlayerInventory ? (inventorySlots.size() - 1) : 0,
			end = intoPlayerInventory ? 0 : inventorySlots.size() - 1;

		if(fromStack.getMaxStackSize() > 1) {
			for(int i = begin; i * step <= end; i += step) {
				if(i >= 0 && i < inventorySlots.size() && from.getHasStack() && from.getStack().getCount() > 0) {
					Slot intoSlot = inventorySlots.get(i);
					if(intoSlot.inventory != from.inventory && intoSlot.getHasStack()) {
						ItemStack intoStack = intoSlot.getStack();
						boolean itemsAreEqual = fromStack.isItemEqual(intoStack) && ItemStack.areItemStackTagsEqual(fromStack, intoStack);
						int maxStackSize = Math.min(fromStack.getMaxStackSize(), intoSlot.getSlotStackLimit());
						boolean slotHasCapacity = intoStack.getCount() < maxStackSize;
						if(itemsAreEqual && slotHasCapacity) {
							int itemsMoved = Math.min(maxStackSize - intoStack.getCount(), fromStack.getCount());
							if(itemsMoved > 0) {
								intoStack.grow(from.decrStackSize(itemsMoved).getCount());
								intoSlot.onSlotChanged();
								somethingChanged = true;
							}
						}
					}
				}
			}
		}

		for(int i = begin; i * step <= end; i += step) {
			if(i >= 0 && i < inventorySlots.size() && from.getHasStack() && from.getStack().getCount() > 0) {
				Slot intoSlot = inventorySlots.get(i);
				if(intoSlot.inventory != from.inventory && !intoSlot.getHasStack() && intoSlot.isItemValid(fromStack)) {
					int maxStackSize = Math.min(fromStack.getMaxStackSize(), intoSlot.getSlotStackLimit());
					int itemsMoved = Math.min(maxStackSize, fromStack.getCount());
					intoSlot.putStack(from.decrStackSize(itemsMoved));
					somethingChanged = true;
				}
			}
		}

		if(somethingChanged) {
			from.onSlotChanged();
		}

	}

	public void bindPlayerInventory(InventoryPlayer inventoryPlayer, int startX, int startY) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
					startX + j * 18, startY + i * 18));
			}
		}
		for(int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, startX + i * 18, startY + 58));
		}
	}
}
