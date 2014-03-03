package pl.asie.computronics.api;

import net.minecraft.item.ItemStack;
import pl.asie.computronics.storage.Storage;

public interface IItemStorage {
	public Storage getStorage(ItemStack stack);
}
