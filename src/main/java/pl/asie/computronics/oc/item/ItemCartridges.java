package pl.asie.computronics.oc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import pl.asie.computronics.Computronics;

import java.util.List;

/**
 * @author Vexatos
 */
public class ItemCartridges extends Item {
	private IIcon patchedIconFull;
	private IIcon patchedIconEmpty;
	private IIcon cellIconFull;
	private IIcon cellIconEmpty;
	private IIcon fireproofIconFull;
	private IIcon fireproofIconEmpty;

	public ItemCartridges() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setNoRepair();
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return meta == 0 ? cellIconFull : cellIconEmpty;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass != 0) {
			return null;
		}
		if(!stack.hasTagCompound()) {
			return cellIconFull;
		}
		switch(stack.getItemDamage()) {
			case 0: { // patched
				return stack.getTagCompound().getBoolean("full") ? patchedIconFull : patchedIconEmpty;
			}
			case 1: { // cell
				return stack.getTagCompound().getBoolean("full") ? cellIconFull : cellIconEmpty;
			}
			case 2: { // fireproof
				return stack.getTagCompound().getBoolean("full") ? fireproofIconFull : fireproofIconEmpty;
			}
			default: {
				return cellIconFull;
			}
		}
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack p_77630_1_) {
		return super.doesContainerItemLeaveCraftingGrid(p_77630_1_);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		if(!(stack.getItem() instanceof ItemCartridges)) {
			return super.getContainerItem(stack);
		}
		if(stack.hasTagCompound() && (!stack.getTagCompound().getBoolean("full")
			|| stack.getTagCompound().getInteger("damaged") >= 2)) {
			return super.getContainerItem(stack);
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("full", false);
		ItemStack newStack = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		newStack.setTagCompound(tag);
		return newStack;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return getIcon(stack, 0);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		patchedIconFull = r.registerIcon("computronics:opencomputers/cartridge_patched_full");
		patchedIconEmpty = r.registerIcon("computronics:opencomputers/cartridge_patched_empty");
		cellIconFull = r.registerIcon("computronics:opencomputers/cartridge_cell_full");
		cellIconEmpty = r.registerIcon("computronics:opencomputers/cartridge_cell_empty");
		fireproofIconFull = r.registerIcon("computronics:opencomputers/cartridge_fireproof_full");
		fireproofIconEmpty = r.registerIcon("computronics:opencomputers/cartridge_fireproof_empty");
	}

	public String getUnlocalizedName() {
		return "item.asielib.unknown";
	}

	public String getUnlocalizedName(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		String s = "item.computronics.cartridge_";
		switch(stack.getItemDamage()) {
			case 0: { // patched
				s += "patched_" + (tag.getBoolean("full") ? "full" : "empty");
				break;
			}
			case 1: { // cell
				s += "cell_";
				s += tag.getBoolean("full") ? "full_" : "empty_";
				s += tag.getInteger("damaged");
				break;
			}
			case 2: { // fireproof
				s += "fireproof_";
				s += tag.getBoolean("full") ? "full_" : "empty_";
				s += tag.getInteger("damaged");
				break;
			}
			default: {
				s = "item.asielib.unknown";
				break;
			}
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("full", true);

		{
			ItemStack stack = new ItemStack(item, 1, 0);
			stack.setTagCompound((NBTTagCompound) tag.copy());
			list.add(stack);
			stack = new ItemStack(item, 1, 0);
			NBTTagCompound newTag = (NBTTagCompound) tag.copy();
			newTag.setBoolean("full", false);
			stack.setTagCompound(newTag);
			list.add(stack);
		}
		for(int i = 1; i <= 2; ++i) {
			for(int j = 0; j < 3; ++j) {
				tag.setInteger("damaged", j);
				ItemStack stack = new ItemStack(item, 1, i);
				stack.setTagCompound((NBTTagCompound) tag.copy());
				list.add(stack);
			}
		}
		tag.setBoolean("full", false);
		for(int i = 1; i <= 2; ++i) {
			for(int j = 0; j < 3; ++j) {
				tag.setInteger("damaged", j);
				ItemStack stack = new ItemStack(item, 1, i);
				stack.setTagCompound((NBTTagCompound) tag.copy());
				list.add(stack);
			}
		}
	}
}
