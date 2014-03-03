package pl.asie.computronics.item;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemTape extends Item implements IItemStorage {
	private int[] sizes = { 524288, 1048576, 1048576 * 2, 1048576 * 4, 1048576 * 6 };
	
	public ItemTape(int id) {
		super(id);
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
	}
	
	public int getSize(int meta) {
		return sizes[meta % sizes.length];
	}

	public Storage getStorage(ItemStack stack) {
		int size = getSize(stack.getItemDamage());
		
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("storage")) {
			// Exists, read NBT data if everything is alright
			NBTTagCompound nbt = stack.getTagCompound();
			String storageName = nbt.getString("storage");
			int position = nbt.hasKey("position") ? nbt.getInteger("position") : 0;
			if(Computronics.storage.exists(storageName))
				return Computronics.storage.get(storageName, size, position);
		}
		
		// Doesn't exist, create new storage and write NBT data
		Storage storage = Computronics.storage.newStorage(size);
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("storage", storage.getName());
		stack.getTagCompound().setInteger("position", 0);
		return storage;
	}
}
