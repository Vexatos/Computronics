package net.mcft.copy.betterstorage.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICrateStorage {
	
	/** Returns a unique inventory identifier for that side.
	 *  When this identifier matches another inventory's or
	 *  side's identifier, they should be considered the same. */
	public Object getInventoryIdentifier(ForgeDirection side);
	
	/** Returns all items in the inventory, accessible or not.
	 *  The returned list may contain null values and
	 *  ItemStacks with stack sizes above their usual limit.
	 *  <p>
	 *  Do not directly modify the items in the list returned
	 *  and note that if the inventory is modified, the list
	 *  may or may not change as well. */
	public List<ItemStack> getContents(ForgeDirection side);
	
	/** Returns the number of items of a specific type (damage
	 *  and NBT data sensitive) in the inventory. */
	public int getItemCount(ForgeDirection side, ItemStack identifier);
	
	/** Returns the space left for a specific type of item
	 *  (damage and NBT data sensitive) in this inventory.
	 *  When there's space for the item, a machine should also
	 *  be able to insert them using insertItems(). */
	public int spaceForItem(ForgeDirection side, ItemStack identifier);
	
	/** Tries to insert items to the inventory, returns the
	 *  items that couldn't be added. The stack may have a
	 *  size above its usual limit. */
	public ItemStack insertItems(ForgeDirection side, ItemStack stack);
	
	/** Tries to extract items from the inventory, returns the
	 *  the items that are actually extracted, null if none. */
	public ItemStack extractItems(ForgeDirection side, ItemStack stack);
	
	/** Registers a crate watcher on the inventory. It will
	 *  call the crate watcher's onCrateItemsModified method
	 *  when items are changed. */
	public void registerCrateWatcher(ICrateWatcher watcher);
	
	/** Unregisters a crate watcher on the inventory. */
	public void unregisterCrateWatcher(ICrateWatcher watcher);
	
}
