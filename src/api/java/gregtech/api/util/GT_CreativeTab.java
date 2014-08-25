package gregtech.api.util;

import gregtech.api.enums.ItemList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GT_CreativeTab extends CreativeTabs {

	@SuppressWarnings("deprecation")
	public GT_CreativeTab() {
		super("GregTech");
		LanguageRegistry.instance().addStringLocalization("itemGroup.GregTech", "GregTech Intergalactical");
	}
	
	@Override
    public ItemStack getIconItemStack() {
        return ItemList.Tool_Cheat.getUndamaged(1, new ItemStack(Blocks.iron_block, 1));
    }
	
	@Override
	public Item getTabIconItem() {
		return ItemList.Tool_Cheat.getItem();
	}
}