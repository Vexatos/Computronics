package pl.asie.computronics.item;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.util.color.ItemColorizer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

public class ItemTape extends Item implements IItemStorage {
	public static final int L_SECOND = 4096;
	public static final int L_MINUTE = 4096*60;
	
	private static int[] sizes = { L_MINUTE*4, L_MINUTE*8, L_MINUTE*16, L_MINUTE*32, L_MINUTE*64, L_MINUTE*2, L_MINUTE*6, L_MINUTE*16, L_MINUTE*128 };
	private IIcon tape_i, tape_g, tape_d, tape_n, tape_c, tape_co, tape_st, tape_greg;
	
	public ItemTape() {
		super();
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		tape_i = r.registerIcon("computronics:tape");
		tape_g = r.registerIcon("computronics:tape_gold");
		tape_d = r.registerIcon("computronics:tape_diamond");
		tape_n = r.registerIcon("computronics:tape_nether_star");
		tape_c = r.registerIcon("computronics:tape_cover");
		tape_co = r.registerIcon("computronics:tape_copper");
		tape_st = r.registerIcon("computronics:tape_steel");
		tape_greg = r.registerIcon("computronics:tape_greg");
	}
	
	@Override
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		if(pass == 0) switch(meta) {
			case 0: return tape_i;
			case 1: return tape_g;
			case 2: return tape_g;
			case 3: return tape_d;
			case 4: return tape_n;
			case 5: return tape_n;
			case 6: return tape_co;
			case 7: return tape_st;
			case 8: return tape_greg;
				
			default: return tape_i;
		}
		else return tape_c;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		int size = getSize(stack);
		int len = (int)Math.floor(size / L_MINUTE);
		if(stack.getTagCompound() != null) {
			String label = stack.getTagCompound().hasKey("label") ? stack.getTagCompound().getString("label") : "";
			if(label.length() > 0) text.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + label);
		}
		text.add(EnumChatFormatting.GRAY + I18n.format("tooltip.computronics.tape.length", ""+len));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for(int i = 0; i < sizes.length; i++) {
			if(i == 8 && !Loader.isModLoaded("gregtech_addon")) return;
			list.add(new ItemStack(item, 1, i));
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
			if(Computronics.storage.exists(storageName))
				return Computronics.storage.get(storageName, size, 0);
		}
		
		// Doesn't exist, create new storage and write NBT data
		Storage storage = Computronics.storage.newStorage(size);
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("storage", storage.getName());
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
