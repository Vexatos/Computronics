package pl.asie.computronics.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class ItemTape extends Item implements IItemStorage {
	private int[] sizes = { 4096*120, 4096*240, 4096*480, 4096*960, 4096*480*3 };
	
	public ItemTape(int id) {
		super(id);
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		int size = getSize(stack);
		int len = (int)Math.floor(size / (4096.0 * 60.0));
		text.add(EnumChatFormatting.GRAY + "Length: " + len + " minutes");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tabs, List list) {
		for(int i = 0; i < sizes.length; i++) {
			list.add(new ItemStack(id, 1, i));
		}
     }
	
	public int getSize(ItemStack stack) {
		return getSize(stack.getItemDamage());
	}
	public int getSize(int meta) {
		return sizes[meta % sizes.length];
	}

	public Storage getStorage(ItemStack stack) {
		int size = getSize(stack);
		
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
