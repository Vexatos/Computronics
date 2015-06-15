package pl.asie.computronics.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.gui.handlers.TapeGuiHandler;
import pl.asie.lib.util.SlotTyped;

public class ContainerTapeReader extends Container {

	private final IInventory inventory;
	private final int containerSize;
	private final TapeGuiHandler handler;

	public ContainerTapeReader(IInventory inventory,
		InventoryPlayer inventoryPlayer, TapeGuiHandler handler) {
		this.inventory = inventory;
		this.handler = handler;

		this.addSlotToContainer(new SlotTyped(inventory, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);

		this.containerSize = this.inventory.getSizeInventory();
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		if(this.inventory == null) {
			return null;
		} else {
			Slot slotObject = (Slot) this.inventorySlots.get(slot);
			if(slotObject != null && slotObject.getHasStack()) {
				this.tryTransferStackInSlot(slotObject, slotObject.inventory == this.inventory);
				if(!Computronics.proxy.isClient()) {
					this.detectAndSendChanges();
				}
			}

			return null;
		}
	}

	public int getSize() {
		return this.containerSize;
	}

	public TapeGuiHandler getHandler() {
		return this.handler;
	}

	public IInventory getInventoryObject() {
		return this.inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return handler.canInteractWith(player);
	}

	protected void tryTransferStackInSlot(Slot from, boolean intoPlayerInventory) {
		ItemStack fromStack = from.getStack();
		boolean somethingChanged = false;
		int step = intoPlayerInventory ? -1 : 1;
		int begin = intoPlayerInventory ? this.inventorySlots.size() - 1 : 0;
		int end = intoPlayerInventory ? 0 : this.inventorySlots.size() - 1;
		int i;
		Slot intoSlot;
		if(fromStack.getMaxStackSize() > 1) {
			for(i = begin; i * step <= end; i += step) {
				if(i >= 0 && i < this.inventorySlots.size() && from.getHasStack() && from.getStack().stackSize > 0) {
					intoSlot = (Slot) this.inventorySlots.get(i);
					if(intoSlot.inventory != from.inventory && intoSlot.getHasStack()) {
						ItemStack maxStackSize = intoSlot.getStack();
						boolean itemsMoved = fromStack.isItemEqual(maxStackSize) && ItemStack.areItemStackTagsEqual(fromStack, maxStackSize);
						int maxStackSize1 = Math.min(fromStack.getMaxStackSize(), intoSlot.getSlotStackLimit());
						boolean slotHasCapacity = maxStackSize.stackSize < maxStackSize1;
						if(itemsMoved && slotHasCapacity) {
							int itemsMoved1 = Math.min(maxStackSize1 - maxStackSize.stackSize, fromStack.stackSize);
							if(itemsMoved1 > 0) {
								maxStackSize.stackSize += from.decrStackSize(itemsMoved1).stackSize;
								intoSlot.onSlotChanged();
								somethingChanged = true;
							}
						}
					}
				}
			}
		}

		for(i = begin; i * step <= end; i += step) {
			if(i >= 0 && i < this.inventorySlots.size() && from.getHasStack() && from.getStack().stackSize > 0) {
				intoSlot = (Slot) this.inventorySlots.get(i);
				if(intoSlot.inventory != from.inventory && !intoSlot.getHasStack() && intoSlot.isItemValid(fromStack)) {
					int maxStackSize2 = Math.min(fromStack.getMaxStackSize(), intoSlot.getSlotStackLimit());
					int itemsMoved2 = Math.min(maxStackSize2, fromStack.stackSize);
					intoSlot.putStack(from.decrStackSize(itemsMoved2));
					somethingChanged = true;
				}
			}
		}

		if(somethingChanged) {
			from.onSlotChanged();
		}

	}

	public void bindPlayerInventory(InventoryPlayer inventoryPlayer, int startX, int startY) {
		int i;
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, startX + j * 18, startY + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, startX + i * 18, startY + 58));
		}

	}

}
