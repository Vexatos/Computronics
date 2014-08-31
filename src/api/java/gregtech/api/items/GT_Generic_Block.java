package gregtech.api.items;

import gregtech.api.GregTech_API;
import gregtech.api.util.GT_LanguageManager;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public class GT_Generic_Block extends Block {
	protected final String mUnlocalizedName;
	
	protected GT_Generic_Block(Class<? extends ItemBlock> aItemClass, String aName, Material aMaterial) {
		super(aMaterial);
		setBlockName(mUnlocalizedName = aName);
		GameRegistry.registerBlock(this, aItemClass, getUnlocalizedName());
		GT_LanguageManager.addStringLocalization(getUnlocalizedName()+"." + GregTech_API.ITEM_WILDCARD_DAMAGE + ".name", "Any Sub Block of this one");
	}
}