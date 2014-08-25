package gregtech.api.items;

import gregtech.api.GregTech_API;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Extended by most Items, also used as a fallback Item, to prevent the accidental deletion when Errors occur.
 */
public class GT_Generic_Item extends Item {
	protected IIcon mIcon;
	private final String mName, mTooltip;
	
	public GT_Generic_Item(String aUnlocalized, String aEnglish, String aEnglishTooltip) {
		this(aUnlocalized, aEnglish, aEnglishTooltip, true);
	}
	
	public GT_Generic_Item(String aUnlocalized, String aEnglish, String aEnglishTooltip, boolean aWriteToolTipIntoLangFile) {
		super();
    	mName = "gt." + aUnlocalized;
		GT_LanguageManager.addStringLocalization(mName + ".name", aEnglish);
		if (GT_Utility.isStringValid(aEnglishTooltip)) GT_LanguageManager.addStringLocalization(mTooltip = mName + ".tooltip_main", aEnglishTooltip, aWriteToolTipIntoLangFile); else mTooltip = null;
		setCreativeTab(GregTech_API.TAB_GREGTECH);
		GameRegistry.registerItem(this, mName, GregTech_API.MOD_ID);
	}
	
	@Override public final Item setUnlocalizedName(String aName) {return this;}
	@Override public final String getUnlocalizedName() {return mName;}
	@Override public String getUnlocalizedName(ItemStack aStack) {return getHasSubtypes()?mName+"."+getDamage(aStack):mName;}
    
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aIconRegister) {
		mIcon = aIconRegister.registerIcon(GregTech_API.TEXTURE_PATH_ITEM + (GT_Config.troll?"troll":mName));
    }
	
	@Override
    public boolean doesSneakBypassUse(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
        return true;
    }
	
	@Override
    public IIcon getIconFromDamage(int par1) {
        return mIcon;
    }
	
	public int getTier(ItemStack aStack) {
		return 0;
	}
	
	@Override
    public void addInformation(ItemStack aStack, EntityPlayer aPlayer, List aList, boolean aF3_H) {
		if (getMaxDamage() > 0 && !getHasSubtypes()) aList.add((aStack.getMaxDamage() - getDamage(aStack)) + " / " + aStack.getMaxDamage());
	    if (mTooltip != null) aList.add(GT_LanguageManager.getTranslation(mTooltip));
	    if (GT_ModHandler.isElectricItem(aStack)) aList.add("Tier: " + getTier(aStack));
	    addAdditionalToolTips(aList, aStack);
	}
	
	protected void addAdditionalToolTips(List aList, ItemStack aStack) {
		//
	}
}