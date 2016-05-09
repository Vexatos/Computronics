package pl.asie.computronics.api.tape;

import net.minecraft.item.ItemStack;

public interface IItemTapeStorage {

	ITapeStorage getStorage(ItemStack stack);

	int getSize(ItemStack stack);
}
