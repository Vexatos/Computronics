package pl.asie.computronics.api;

import net.minecraft.item.ItemStack;
import pl.asie.computronics.tape.Storage;

public interface IItemStorage {
	public Storage getStorage(ItemStack stack);
	public int getSize(ItemStack stack);
}
