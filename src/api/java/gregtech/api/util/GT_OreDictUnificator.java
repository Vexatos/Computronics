package gregtech.api.util;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.objects.MaterialStack;
import gregtech.api.objects.OrePrefixMaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * 
 * This is the Core of my OreDict Unification Code
 * 
 * If you just want to use this to unificate your Items, then use the Function in the GregTech_API File
 * 
 * P.S. It is intended to be named "Unificator" and not "Unifier", because that sounds more awesome.
 */
public class GT_OreDictUnificator {
	public static volatile int VERSION = 503;
	
	private static final HashMap<String, ItemStack> sName2OreMap = new HashMap<String, ItemStack>();
	private static final HashMap<GT_ItemStack, OrePrefixMaterialData> sItemStack2NameMap = new HashMap<GT_ItemStack, OrePrefixMaterialData>();
	private static final ArrayList<GT_ItemStack> sBlackList = new ArrayList<GT_ItemStack>();
	
	private static int isRegisteringOre = 0, isAddingOre = 0;
	
	static {
		GregTech_API.sItemStackMappings.add(sItemStack2NameMap);
	}
	
	/**
	 * The Blacklist just prevents the Item from being Unificated into something else.
	 * Useful if you have things like the Industrial Diamond, which is better than regular Diamond, but also placeable in absolutely all Diamond Recipes.
	 */
	public static void addToBlacklist(ItemStack aStack) {
		if (GT_Utility.isStackValid(aStack)) sBlackList.add(new GT_ItemStack(aStack));
	}
	
	public static boolean isBlacklisted(ItemStack aStack) {
		return sBlackList.contains(new GT_ItemStack(aStack)) || GT_Utility.getBlockFromStack(aStack) != Blocks.air;
	}
	
	public static void add(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, false, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, true, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack, boolean aOverwrite, boolean aAlreadyRegistered) {
		if (aMaterial == null || aPrefix == null || GT_Utility.isStackInvalid(aStack) || Items.feather.getDamage(aStack) == GregTech_API.ITEM_WILDCARD_DAMAGE) return;
		isAddingOre++;
		aStack = GT_Utility.copyAmount(1, aStack);
		if (!aAlreadyRegistered) registerOre(aPrefix.get(aMaterial), aStack);
		addAssociation(aPrefix, aMaterial, aStack);
		if (aOverwrite || GT_Utility.isStackInvalid(sName2OreMap.get(aPrefix.get(aMaterial).toString()))) {
			sName2OreMap.put(aPrefix.get(aMaterial).toString(), aStack);
		}
		isAddingOre--;
	}
	
	public static ItemStack getFirstOre(Object aName, long aAmount) {
		if (GT_Utility.isStringInvalid(aName)) return null;
		if (GT_Utility.isStackValid(sName2OreMap.get(aName.toString()))) return GT_Utility.copyAmount(aAmount, sName2OreMap.get(aName.toString()));
		return GT_Utility.copyAmount(aAmount, getOres(aName).toArray());
	}
	
	public static ItemStack get(Object aName, long aAmount) {
		return get(aName, null, aAmount, true, true);
	}
	
	public static ItemStack get(Object aName, ItemStack aReplacement, long aAmount) {
		return get(aName, aReplacement, aAmount, true, true);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Object aMaterial, long aAmount) {
		return get(aPrefix, aMaterial, null, aAmount);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Object aMaterial, ItemStack aReplacement, long aAmount) {
		return get(aPrefix.get(aMaterial), aReplacement, aAmount, false, true);
	}
	
	public static ItemStack get(Object aName, ItemStack aReplacement, long aAmount, boolean aMentionPossibleTypos, boolean aNoInvalidAmounts) {
		if (aNoInvalidAmounts && aAmount < 1) return null;
		if (!sName2OreMap.containsKey(aName.toString()) && aMentionPossibleTypos) GT_Log.err.println("Unknown Key for Unification, Typo? " + aName);
		return GT_Utility.copyAmount(aAmount, sName2OreMap.get(aName.toString()), getFirstOre(aName, aAmount), aReplacement);
	}
	
	public static ItemStack[] setStackArray(boolean aUseBlackList, ItemStack... aStacks) {
		for (int i = 0; i < aStacks.length; i++) aStacks[i] = get(aUseBlackList, GT_Utility.copy(aStacks[i]));
		return aStacks;
	}
	
	public static ItemStack[] getStackArray(boolean aUseBlackList, Object... aStacks) {
		ItemStack[] rStacks = new ItemStack[aStacks.length];
		for (int i = 0; i < aStacks.length; i++) rStacks[i] = get(aUseBlackList, GT_Utility.copy(aStacks[i]));
		return rStacks;
	}
	
	public static ItemStack setStack(ItemStack aStack) {
		return setStack(true, aStack);
	}
	
	public static ItemStack setStack(boolean aUseBlackList, ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) return aStack;
		ItemStack tStack = get(aUseBlackList, aStack);
		if (GT_Utility.areStacksEqual(aStack, tStack)) return aStack;
		aStack.func_150996_a(tStack.getItem());
		Items.feather.setDamage(aStack, Items.feather.getDamage(tStack));
		return aStack;
	}
	
	public static ItemStack get(ItemStack aStack) {
		return get(true, aStack);
	}
	
	public static ItemStack get(boolean aUseBlackList, ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) return null;
		if (aUseBlackList && isBlacklisted(aStack)) return GT_Utility.copy(aStack);
		OrePrefixMaterialData tName = sItemStack2NameMap.get(new GT_ItemStack(aStack));
		ItemStack rStack = null;
		if (tName != null) rStack = sName2OreMap.get(tName.toString());
		if (GT_Utility.isStackInvalid(rStack)) return GT_Utility.copy(aStack);
		assert rStack != null;
		rStack.setTagCompound(aStack.getTagCompound());
		return GT_Utility.copyAmount(aStack.stackSize, rStack);
	}
	
	public static void addAssociation(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		if (aPrefix == null || aMaterial == null || GT_Utility.isStackInvalid(aStack)) return;
		if (Items.feather.getDamage(aStack) == GregTech_API.ITEM_WILDCARD_DAMAGE) {
			aStack = GT_Utility.copyAmount(1, aStack);
			for (byte i = 0; i < 16; i++) {
				Items.feather.setDamage(aStack, i);
				sItemStack2NameMap.put(new GT_ItemStack(aStack), new OrePrefixMaterialData(aPrefix, aMaterial));
			}
		}
		sItemStack2NameMap.put(new GT_ItemStack(aStack), new OrePrefixMaterialData(aPrefix, aMaterial));
	}
	
	public static OrePrefixMaterialData getAssociation(ItemStack aStack) {
		return sItemStack2NameMap.get(new GT_ItemStack(aStack));
	}
	
	public static boolean isItemStackInstanceOf(ItemStack aStack, Object aName) {
		if (GT_Utility.isStringInvalid(aName) || GT_Utility.isStackInvalid(aStack)) return false;
		for (ItemStack tOreStack : getOres(aName.toString())) if (GT_Utility.areStacksEqual(tOreStack, aStack, !tOreStack.hasTagCompound())) return true;
		return false;
	}
	
	public static boolean isItemStackDye(ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) return false;
		for (Dyes tDye : Dyes.VALUES) if (isItemStackInstanceOf(aStack, tDye.toString())) return true;
		return false;
	}
	
    public static boolean registerOre(OrePrefixes aPrefix, Object aMaterial, ItemStack aStack) {
    	return registerOre(aPrefix.get(aMaterial), aStack);
    }
    
    private static List mIDToStack;
    private static Map mStackToID;
    private static boolean mCheckFields = true;
    
    public static boolean registerOre(Object aName, ItemStack aStack) {
    	if (aName == null || GT_Utility.isStackInvalid(aStack)) return false;
    	String tName = aName.toString();
    	if (GT_Utility.isStringInvalid(tName)) return false;
    	ArrayList<ItemStack> tList = getOres(tName);
    	for (int i = 0; i < tList.size(); i++) if (GT_Utility.areStacksEqual(tList.get(i), aStack, true)) return false;
    	// Doesn't compare already existing ItemStacks with the passed ones properly (no MetaData check), so I need to hack fix it myself until it is fixed on Forge Side.
    	if (mCheckFields) {
    		mCheckFields = false;
    		try {
	            Field tField = OreDictionary.class.getDeclaredField("idToStack");
	            tField.setAccessible(true);
	            mIDToStack = (List)tField.get(null);
	    		
	            tField = OreDictionary.class.getDeclaredField("stackToId");
	            tField.setAccessible(true);
	            mStackToID = (Map)tField.get(null);
    		} catch(Throwable e) {
        		e.printStackTrace(GT_Log.err);
        	}
    	}
    	isRegisteringOre++;
    	if (mIDToStack != null && mStackToID != null) {
            int tOreID = OreDictionary.getOreID(tName);
            int tHash = Item.getIdFromItem(aStack.getItem());
            if (aStack.getItemDamage() != GregTech_API.ITEM_WILDCARD_DAMAGE) tHash |= ((aStack.getItemDamage() + 1) << 16);
            List ids = (List)mStackToID.get(tHash);
            if (ids == null) {
            	OreDictionary.registerOre(tName, GT_Utility.copyAmount(1, aStack));
            } else {
                ids.add(tOreID);
                ((List)mIDToStack.get(tOreID)).add(GT_Utility.copyAmount(1, aStack));
                MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(tName, GT_Utility.copyAmount(1, aStack)));
            }
    	} else {
    		OreDictionary.registerOre(tName, GT_Utility.copyAmount(1, aStack));
    	}
    	isRegisteringOre--;
    	return true;
    }
    
    public static boolean isRegisteringOres() {
    	return isRegisteringOre > 0;
    }
    
    public static boolean isAddingOres() {
    	return isAddingOre > 0;
    }
    
    public static ItemStack getDust(MaterialStack aMaterial) {
    	return aMaterial==null?null:getDust(aMaterial.mMaterial, aMaterial.mAmount);
    }
    
    public static ItemStack getDust(Materials aMaterial, OrePrefixes aPrefix) {
    	return aMaterial==null?null:getDust(aMaterial, aPrefix.mMaterialAmount);
    }
    
    public static ItemStack getDust(Materials aMaterial, long aMaterialAmount) {
    	ItemStack rStack = null;
		if (                   aMaterialAmount      %   GregTech_API.MATERIAL_UNIT     == 0) rStack = GT_OreDictUnificator.get(OrePrefixes.dust		, aMaterial,  aMaterialAmount      /  GregTech_API.MATERIAL_UNIT     );
		if (rStack == null && (aMaterialAmount * 4) %   GregTech_API.MATERIAL_UNIT     == 0) rStack = GT_OreDictUnificator.get(OrePrefixes.dustSmall, aMaterial, (aMaterialAmount * 4) /  GregTech_API.MATERIAL_UNIT     );
		if (rStack == null && (aMaterialAmount * 9) >=  GregTech_API.MATERIAL_UNIT         ) rStack = GT_OreDictUnificator.get(OrePrefixes.dustTiny	, aMaterial, (aMaterialAmount * 9) /  GregTech_API.MATERIAL_UNIT     );
    	return rStack;
    }
    
    public static ItemStack getIngot(MaterialStack aMaterial) {
    	return aMaterial==null?null:getIngot(aMaterial.mMaterial, aMaterial.mAmount);
    }
    
    public static ItemStack getIngot(Materials aMaterial, OrePrefixes aPrefix) {
    	return aMaterial==null?null:getIngot(aMaterial, aPrefix.mMaterialAmount);
    }
    
    public static ItemStack getIngot(Materials aMaterial, long aMaterialAmount) {
    	ItemStack rStack = null;
    	if (                   aMaterialAmount      %  (GregTech_API.MATERIAL_UNIT *9) == 0 && aMaterialAmount / (GregTech_API.MATERIAL_UNIT * 9) > 1) rStack = GT_OreDictUnificator.get(OrePrefixes.block	, aMaterial,  aMaterialAmount      / (GregTech_API.MATERIAL_UNIT * 9));
    	if (rStack == null &&  aMaterialAmount      %   GregTech_API.MATERIAL_UNIT     == 0                                                          ) rStack = GT_OreDictUnificator.get(OrePrefixes.ingot	, aMaterial,  aMaterialAmount      /  GregTech_API.MATERIAL_UNIT     );
		if (rStack == null && (aMaterialAmount * 9) >=  GregTech_API.MATERIAL_UNIT                                                                   ) rStack = GT_OreDictUnificator.get(OrePrefixes.nugget	, aMaterial, (aMaterialAmount * 9) /  GregTech_API.MATERIAL_UNIT     );
    	return rStack;
    }
    
    /**
     * @return a Copy of the OreDictionary.getOres() List
     */
    public static ArrayList<ItemStack> getOres(OrePrefixes aPrefix, Object aMaterial) {
    	return getOres(aPrefix.get(aMaterial));
    }
    
    /**
     * @return a Copy of the OreDictionary.getOres() List
     */
    public static ArrayList<ItemStack> getOres(Object aOreName) {
    	String aName = aOreName==null?"":aOreName.toString();
    	ArrayList<ItemStack> rList = new ArrayList<ItemStack>();
    	if (GT_Utility.isStringValid(aName)) rList.addAll(OreDictionary.getOres(aName));
    	return rList;
    }
}
