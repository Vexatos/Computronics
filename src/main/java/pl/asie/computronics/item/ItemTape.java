package pl.asie.computronics.item;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.media.IMediaProvider;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.tape.Storage;
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
import net.minecraft.world.World;

@Optional.InterfaceList({
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMediaProvider", modid = "ComputerCraft"),
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMedia", modid = "ComputerCraft")
})
public class ItemTape extends Item implements IItemStorage, IMedia, IMediaProvider {
	public static final int L_SECOND = 4096;
	public static final int L_MINUTE = 4096*60;
	
	private int[] sizes;
	private IIcon tape_i, tape_g, tape_d, tape_n, tape_c, tape_co, tape_st, tape_greg;
	
	public ItemTape(String lengths) {
		super();
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		
		// parse lengths
		String[] l = lengths.split(",");
		sizes = new int[l.length];
		for(int i = 0; i < l.length; i++) {
			sizes[i] = Integer.parseInt(l[i]) * L_MINUTE;
			if(sizes[i] <= 0) sizes[i] = 4;
		}
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
			case 5: return tape_co;
			case 6: return tape_st;
			case 7: return tape_greg;
			case 8: return tape_n;

			default: return tape_i;
		}
		else return tape_c;
	}
	
	public String getLabel(ItemStack stack) {
		return stack.getTagCompound().hasKey("label") ? stack.getTagCompound().getString("label") : "";
	}

	public boolean setLabel(ItemStack stack, String label) {
		if(stack == null) return false;
		stack.getTagCompound().setString("label", label);
		return true;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		int size = getSize(stack);
		int len = (int)Math.floor(size / L_MINUTE);
		if(stack.getTagCompound() != null) {
			String label = getLabel(stack);
			if(label.length() > 0) text.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + label);
		}
		text.add(EnumChatFormatting.GRAY + I18n.format("tooltip.computronics.tape.length", ""+len));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for(int i = 0; i < sizes.length; i++) {
			if(i == 7 && !Loader.isModLoaded("gregtech_addon")) {
				//Do nothing. If we return here, we lose all new tapes.
			}
			else list.add(new ItemStack(item, 1, i));
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

	@Override
	public IMedia getMedia(ItemStack stack) {
		if(stack != null && stack.stackSize > 0 && stack.getItem() != null && stack.getItem() instanceof ItemTape) return ((IMedia)stack.getItem());
		return null;
	}

	@Override
	public String getAudioTitle(ItemStack stack) { return null; }
	@Override
	public String getAudioRecordName(ItemStack stack) { return null; }
	@Override
	public IMount createDataMount(ItemStack stack, World world) { return null; }
}
