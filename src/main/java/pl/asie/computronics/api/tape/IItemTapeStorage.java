package pl.asie.computronics.api.tape;

import net.minecraft.item.ItemStack;
import pl.asie.computronics.tape.TapeStorage;

public interface IItemTapeStorage {
	public ITapeStorage getStorage(ItemStack stack);
	public int getSize(ItemStack stack);
}
