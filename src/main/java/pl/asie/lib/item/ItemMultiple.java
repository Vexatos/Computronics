package pl.asie.lib.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemMultiple extends Item {

	protected final String mod;
	protected final String[] parts;

	public ItemMultiple(String mod, String[] parts) {
		super();
		this.mod = mod;
		this.parts = parts;
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	public String getUnlocalizedName() {
		return "item.asielib.unknown";
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(stack == null) {
			return "item.asielib.unknown";
		} else {
			return "item." + this.mod + "." + this.parts[stack.getItemDamage() % parts.length];
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for(int i = 0; i < parts.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
}
