package pl.asie.computronics.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.util.color.ItemColorizer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;

public class ItemTape extends Item implements IItemStorage {
	private static int[] sizes = { 4096*60*4, 4096*60*8, 4096*60*16, 4096*60*32 };
	private Icon tape_i, tape_g, tape_d, tape_c;
	
	public ItemTape(int id) {
		super(id);
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		tape_i = r.registerIcon("computronics:tape");
		tape_g = r.registerIcon("computronics:tape_gold");
		tape_d = r.registerIcon("computronics:tape_diamond");
		tape_c = r.registerIcon("computronics:tape_cover");
	}
	
	@Override
	public Icon getIconFromDamageForRenderPass(int meta, int pass) {
		if(pass == 0) switch(meta) {
			case 0: return tape_i;
			case 1: return tape_g;
			case 2: return tape_g;
			case 3: return tape_d;
				
			default: return tape_i;
		}
		else return tape_c;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		int size = getSize(stack);
		int len = (int)Math.floor(size / (4096.0 * 60.0));
		if(stack.getTagCompound() != null) {
			String label = stack.getTagCompound().hasKey("label") ? stack.getTagCompound().getString("label") : "";
			if(label.length() > 0) text.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + label);
		}
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
	
	// Colorizing
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        return pass == 0 ? 16777215 : (ItemColorizer.hasColor(stack) ? ItemColorizer.getColor(stack) : 16777215);
    }
}
