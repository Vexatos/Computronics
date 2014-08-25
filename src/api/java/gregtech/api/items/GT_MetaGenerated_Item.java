package gregtech.api.items;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.TC_Aspects.TC_AspectStack;
import gregtech.api.interfaces.IFoodStat;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_OreDictUnificator;

import java.util.*;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Gregorius Techneticies
 * 
 * One Item for everything!
 * 
 * This brilliant Item Class is used for automatically generating all possible variations of Material Items, like Dusts, Ingots, Gems, Plates and similar.
 * It saves me a ton of work, when adding Items, because I always have to make a new Item SubType for each OreDict Prefix, when adding a new Material.
 * 
 * As you can see, up to 32766 Items can be generated using this Class. And the last 766 Items can be custom defined, just to save space and MetaData.
 * 
 * These Items can also have special RightClick abilities, electric Charge or even be set to become a Food alike Item.
 */
public abstract class GT_MetaGenerated_Item extends GT_MetaBase_Item {
	/**
	 * All instances of this Item Class are listed here.
	 * This gets used to register the Renderer to all Items of this Type, if useStandardMetaItemRenderer() returns true.
	 * 
	 * You can also use the unlocalized Name gotten from getUnlocalizedName() as Key if you want to get a specific Item.
	 */
	public static final HashMap<String, GT_MetaGenerated_Item> sInstances = new HashMap<String, GT_MetaGenerated_Item>();
	
	/* ---------- CONSTRUCTOR AND MEMBER VARIABLES ---------- */

	protected BitSet mEnabledItems = new BitSet(766);
	protected BitSet mVisibleItems = new BitSet(766);
	protected final OrePrefixes[] mGeneratedPrefixList;
	public IIcon[][] mIconList = new IIcon[mEnabledItems.size()][1];
	
	public final HashMap<Short, IFoodStat> mFoodStats = new HashMap<Short, IFoodStat>();
	public final HashMap<Short, Long[]> mElectricStats = new HashMap<Short, Long[]>();
	public final HashMap<Short, Short> mBurnValues = new HashMap<Short, Short>();
	
	/**
	 * Creates the Item using these Parameters.
	 * @param aUnlocalized The Unlocalized Name of this Item.
	 * @param aGeneratedPrefixList The OreDict Prefixes you want to have generated.
	 */
	public GT_MetaGenerated_Item(String aUnlocalized, OrePrefixes... aGeneratedPrefixList) {
		super(aUnlocalized);
		mGeneratedPrefixList = Arrays.copyOf(aGeneratedPrefixList, 32);
		setCreativeTab(GregTech_API.TAB_GREGTECH_MATERIALS);
        setHasSubtypes(true);
        setMaxDamage(0);
        
        sInstances.put(getUnlocalizedName(), this);
        
        for (int i = 0; i < 32000; i++) {
			OrePrefixes tPrefix = mGeneratedPrefixList[i / 1000];
			if (tPrefix == null) continue;
			Materials tMaterial = GregTech_API.sGeneratedMaterials[i % 1000];
			if (tMaterial == null) continue;
			if (doesMaterialAllowGeneration(tPrefix, tMaterial)) {
				ItemStack tStack = new ItemStack(this, 1, i);
				GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".name", getDefaultLocalization(tPrefix, tMaterial, i));
				GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".tooltip", tMaterial.getToolTip(tPrefix.mMaterialAmount / GregTech_API.MATERIAL_UNIT));
				String tOreName = getOreDictString(tPrefix, tMaterial);
				tPrefix = OrePrefixes.getOrePrefix(tOreName);
				if (tPrefix != null && tPrefix.mIsUnificatable) {
					GT_OreDictUnificator.set(tPrefix, OrePrefixes.getMaterial(tOreName, tPrefix), tStack);
				} else {
					GT_OreDictUnificator.registerOre(tOreName, tStack);
				}
			}
		}
	}
	
	/* ---------- OVERRIDEABLE FUNCTIONS ---------- */
	
	/**
	 * @param aPrefix the OreDict Prefix
	 * @param aMaterial the Material
	 * @param aMetaData a Index from [0 - 31999]
	 * @return the Localized Name when default LangFiles are used.
	 */
	public String getDefaultLocalization(OrePrefixes aPrefix, Materials aMaterial, int aMetaData) {
		return aPrefix.mLocalizedMaterialPre + aMaterial.mDefaultLocalName + aPrefix.mLocalizedMaterialPost;
	}
	
	/**
	 * @param aMetaData a Index from [0 - 31999]
	 * @param aMaterial the Material
	 * @return an Icon Container for the Item Display.
	 */
	public abstract IIconContainer getIconContainer(int aMetaData, Materials aMaterial);
	
	/**
	 * @param aPrefix this can be null, you have to return false in that case
	 * @param aMaterial this can be null, you have to return false in that case
	 * @return if this Item should be generated and visible.
	 */
	public boolean doesMaterialAllowGeneration(OrePrefixes aPrefix, Materials aMaterial) {
		// You have to check for at least these Conditions in every Case! So add a super Call like the following for this before executing your Code:
		// if (!super.doesMaterialAllowGeneration(aPrefix, aMaterial)) return false;
		return aPrefix != null && aMaterial != null && !aPrefix.dontGenerateItem(aMaterial);
	}
	
	/**
	 * @param aPrefix always != null
	 * @param aMaterial always != null
	 * @param aDoShowAllItems this is the Configuration Setting of the User, if he wants to see all the Stuff like Tiny Dusts or Crushed Ores as well.
	 * @return if this Item should be visible in NEI or Creative
	 */
	public boolean doesShowInCreative(OrePrefixes aPrefix, Materials aMaterial, boolean aDoShowAllItems) {
		return true;
	}
	
	/**
	 * @return the name of the Item to be registered at the OreDict.
	 */
	public String getOreDictString(OrePrefixes aPrefix, Materials aMaterial) {
		return aPrefix.get(aMaterial).toString();
	}
	
	/**
	 * @return the Color Modulation the Material is going to be rendered with.
	 */
	public short[] getRGBa(ItemStack aStack) {
		int aMetaData = getDamage(aStack);
		if (aMetaData < 0) return Materials._NULL.mRGBa;
		if (aMetaData < 32000) {
			Materials tMaterial = GregTech_API.sGeneratedMaterials[aMetaData % 1000];
			if (tMaterial == null) return Materials._NULL.mRGBa;
			for (byte i = 0; i < tMaterial.mRGBa.length; i++) {
				if (tMaterial.mRGBa[i] > 255) tMaterial.mRGBa[i] = 255;
				if (tMaterial.mRGBa[i] < 0) tMaterial.mRGBa[i] = 0;
			}
			return tMaterial.mRGBa;
		}
        return Materials._NULL.mRGBa;
	}
	
	/**
	 * @return if this MetaGenerated Item should use my Default Renderer System.
	 */
	public boolean useStandardMetaItemRenderer() {
		return true;
	}
	
	/* ---------- FOR ADDING CUSTOM ITEMS INTO THE REMAINING 766 RANGE ---------- */
	
	/**
	 * This adds a Custom Item to the ending Range.
	 * @param aID The Id of the assigned Item [0 - 765] (The MetaData gets auto-shifted by +32000)
	 * @param aEnglish The Default Localized Name of the created Item
	 * @param aToolTip The Default ToolTip of the created Item, you can also insert null for having no ToolTip
	 * @param aFoodBehavior The Food Value of this Item. Can be null aswell. Just a convenience thing.
	 * @param aOreDictNames The OreDict Names you want to give the Item.
	 * @return An ItemStack containing the newly created Item.
	 */
	public final ItemStack addItem(int aID, String aEnglish, String aToolTip, Object... aOreDictNamesAndAspects) {
		if (aToolTip == null) aToolTip = "";
		if (aID >= 0 && aID < mEnabledItems.size()) {
			ItemStack rStack = new ItemStack(this, 1, 32000+aID);
			mEnabledItems.set(aID);
			mVisibleItems.set(aID);
			GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".name", aEnglish);
			GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".tooltip", aToolTip);
			List<TC_AspectStack> tAspects = new ArrayList<TC_AspectStack>();
			for (Object tOreDictNameOrAspect : aOreDictNamesAndAspects) if (tOreDictNameOrAspect != null) {
				if (tOreDictNameOrAspect instanceof TC_AspectStack) {
					((TC_AspectStack)tOreDictNameOrAspect).addToAspectList(tAspects);
					continue;
				}
				if (tOreDictNameOrAspect instanceof IFoodStat) {
					setFoodBehavior(32000+aID, (IFoodStat)tOreDictNameOrAspect);
					continue;
				}
				if (tOreDictNameOrAspect == SubTag.INVISIBLE) {
					mVisibleItems.set(aID, false);
					continue;
				}
				GT_OreDictUnificator.registerOre(tOreDictNameOrAspect, rStack);
			}
			if (GregTech_API.sThaumcraftCompat != null) GregTech_API.sThaumcraftCompat.registerThaumcraftAspectsToItem(rStack, tAspects, false);
			return rStack;
		}
		return null;
	}
	
	/**
	 * Sets a Food Behavior for the Item.
	 * 
	 * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aFoodBehavior the Food Behavior you want to add.
	 * @return the Item itself for convenience in constructing.
	 */
	public final GT_MetaGenerated_Item setFoodBehavior(int aMetaValue, IFoodStat aFoodBehavior) {
		if (aMetaValue < 0 || aMetaValue >= 32000 + mEnabledItems.length()) return this;
		if (aFoodBehavior == null) mFoodStats.remove((short)aMetaValue); else mFoodStats.put((short)aMetaValue, aFoodBehavior);
		return this;
	}
	
	/**
	 * Sets the Furnace Burn Value for the Item.
	 * 
	 * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aValue 200 = 1 Burn Process = 500 EU, max = 32767 (that is 81917.5 EU)
	 * @return the Item itself for convenience in constructing.
	 */
	public final GT_MetaGenerated_Item setBurnValue(int aMetaValue, int aValue) {
		if (aMetaValue < 0 || aMetaValue >= 32000 + mEnabledItems.length() || aValue < 0) return this;
		if (aValue == 0) mBurnValues.remove((short)aMetaValue); else mBurnValues.put((short)aMetaValue, aValue>Short.MAX_VALUE?Short.MAX_VALUE:(short)aValue);
		return this;
	}
	
	/**
	 * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aMaxCharge Maximum Charge. (if this is == 0 it will remove the Electric Behavior)
	 * @param aTransferLimit Transfer Limit.
	 * @param aTier The electric Tier.
	 * @param aSpecialData If this Item has a Fixed Charge, like a SingleUse Battery (if > 0).
	 * Use -1 if you want to make this Battery chargeable (the use and canUse Functions will still discharge if you just use this)
	 * Use -2 if you want to make this Battery dischargeable.
	 * Use -3 if you want to make this Battery charge/discharge-able.
	 * @return the Item itself for convenience in constructing.
	 */
	public final GT_MetaGenerated_Item setElectricStats(int aMetaValue, long aMaxCharge, long aTransferLimit, long aTier, long aSpecialData, boolean aUseAnimations) {
		if (aMetaValue < 0 || aMetaValue >= 32000 + mEnabledItems.length()) return this;
		if (aMaxCharge == 0) mElectricStats.remove((short)aMetaValue); else {
			mElectricStats.put((short)aMetaValue, new Long[] {aMaxCharge, Math.max(0, aTransferLimit), Math.max(-1, aTier), aSpecialData});
			if (aMetaValue >= 32000 && aUseAnimations) mIconList[aMetaValue-32000] = Arrays.copyOf(mIconList[aMetaValue-32000], Math.max(9, mIconList[aMetaValue-32000].length));
		}
		return this;
	}
	
	/* ---------- INTERNAL OVERRIDES ---------- */
	
    @Override
	public ItemStack onItemRightClick(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
    	IFoodStat tStat = mFoodStats.get((short)getDamage(aStack));
    	if (tStat != null && aPlayer.canEat(tStat.alwaysEdible(this, aStack, aPlayer))) aPlayer.setItemInUse(aStack, 32);
		return super.onItemRightClick(aStack, aWorld, aPlayer);
    }
    
    @Override
    public final int getMaxItemUseDuration(ItemStack aStack) {
        return mFoodStats.get((short)getDamage(aStack)) == null ? 0 : 32;
    }
    
    @Override
	public final EnumAction getItemUseAction(ItemStack aStack) {
    	IFoodStat tStat = mFoodStats.get((short)getDamage(aStack));
        return tStat == null ? EnumAction.none : tStat.getFoodAction(this, aStack);
    }
    
    @Override
	public final ItemStack onEaten(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
    	IFoodStat tStat = mFoodStats.get((short)getDamage(aStack));
    	if (tStat != null) {
            aPlayer.getFoodStats().addStats(tStat.getFoodLevel(this, aStack, aPlayer), tStat.getSaturation(this, aStack, aPlayer));
            tStat.onEaten(this, aStack, aPlayer);
    	}
        return aStack;
    }
    
	public final IIconContainer getIconContainer(int aMetaData) {
		if (aMetaData < 0) return null;
		if (aMetaData < 32000) {
			Materials tMaterial = GregTech_API.sGeneratedMaterials[aMetaData % 1000];
			if (tMaterial == null) return null;
			return getIconContainer(aMetaData, tMaterial);
		}
		return null;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(Item var1, CreativeTabs aCreativeTab, List aList) {
        for (int i = 0; i < 32000; i++) if (doesMaterialAllowGeneration(mGeneratedPrefixList[i / 1000], GregTech_API.sGeneratedMaterials[i % 1000]) && doesShowInCreative(mGeneratedPrefixList[i / 1000], GregTech_API.sGeneratedMaterials[i % 1000], GregTech_API.sDoShowAllItemsInCreative)) aList.add(new ItemStack(this, 1, i));
        for (int i = 0, j = mEnabledItems.length(); i < j; i++) if (mVisibleItems.get(i) || (GregTech_API.DEBUG_MODE && mEnabledItems.get(i))) {
    		Long[] tStats = mElectricStats.get((short)(32000+i));
    		if (tStats != null && tStats[3] < 0) {
    			ItemStack tStack = new ItemStack(this, 1, 32000+i);
    			setCharge(tStack, Math.abs(tStats[0]));
            	aList.add(tStack);
    		}
    		if (tStats == null || tStats[3] != -2) {
            	aList.add(new ItemStack(this, 1, 32000+i));
    		}
        }
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public final void registerIcons(IIconRegister aIconRegister) {
		for (short i = 0, j = (short)mEnabledItems.length(); i < j; i++) if (mEnabledItems.get(i)) {
			for (byte k = 1; k < mIconList[i].length; k++) {
				mIconList[i][k] = aIconRegister.registerIcon(GregTech_API.TEXTURE_PATH_ITEM + (GT_Config.troll?"troll":getUnlocalizedName() + "/" + i + "/" + k));
			}
    		mIconList[i][0] = aIconRegister.registerIcon(GregTech_API.TEXTURE_PATH_ITEM + (GT_Config.troll?"troll":getUnlocalizedName() + "/" + i));
    	}
    }
	
	@Override
    public final IIcon getIconFromDamage(int aMetaData) {
		if (aMetaData < 0) return null;
		if (aMetaData < 32000) {
			Materials tMaterial = GregTech_API.sGeneratedMaterials[aMetaData % 1000];
			if (tMaterial == null) return null;
			IIconContainer tIcon = getIconContainer(aMetaData, tMaterial);
			if (tIcon != null) return tIcon.getIcon();
			return null;
		}
		return aMetaData-32000<mIconList.length?mIconList[aMetaData-32000][0]:null;
    }
	
	@Override
	public final Long[] getElectricStats(ItemStack aStack) {
		return mElectricStats.get((short)aStack.getItemDamage());
	}
	
	@Override public int getItemEnchantability() {return 0;}
	@Override public boolean isBookEnchantable(ItemStack aStack, ItemStack aBook) {return false;}
	@Override public boolean getIsRepairable(ItemStack aStack, ItemStack aMaterial) {return false;}
}