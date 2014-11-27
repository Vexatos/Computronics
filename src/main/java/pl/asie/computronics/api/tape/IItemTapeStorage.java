package pl.asie.computronics.api.tape;

import net.minecraft.item.ItemStack;

public interface IItemTapeStorage {
	public ITapeStorage getStorage(ItemStack stack);

	public int getSize(ItemStack stack);
}
