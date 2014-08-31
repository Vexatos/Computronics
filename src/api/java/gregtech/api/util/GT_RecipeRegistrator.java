package gregtech.api.util;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.enums.TC_Aspects.TC_AspectStack;
import gregtech.api.interfaces.internal.IThaumcraftCompat;
import gregtech.api.objects.MaterialStack;
import gregtech.api.objects.OrePrefixMaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Class for Automatic Recipe registering.
 */
public class GT_RecipeRegistrator {
	public static volatile int VERSION = 504;
	/**
	 * List of Materials, which are used in the Creation of Sticks. All Rod Materials are automatically added to this List.
	 */
	public static final List<Materials> sRodMaterialList = new ArrayList<Materials>();

	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 * @param aAllowAlloySmelter if it is allowed to be recycled inside the Alloy Smelter.
	 */
	public static void registerBasicReverseMaceratingAndSmelting(ItemStack aStack, Materials aMaterial, long aMaterialAmount, boolean aAllowAlloySmelter, MaterialStack aByproducts) {
		registerBasicReverseMacerating(aStack, aMaterial, aMaterialAmount, aByproducts);
		registerBasicReverseSmelting(aStack, aMaterial, aMaterialAmount, aAllowAlloySmelter);
	}
	
	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 * @param aAllowAlloySmelter if it is allowed to be recycled inside the Alloy Smelter.
	 */
	public static void registerBasicReverseMaceratingAndSmelting(ItemStack aStack, Materials aMaterial, long aMaterialAmount, MaterialStack aByproducts) {
		registerBasicReverseMacerating(aStack, aMaterial, aMaterialAmount, aByproducts);
		registerBasicReverseSmelting(aStack, aMaterial, aMaterialAmount, true);
	}
	
	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 * @param aAllowAlloySmelter if it is allowed to be recycled inside the Alloy Smelter.
	 */
	public static void registerBasicReverseMaceratingAndSmelting(ItemStack aStack, Materials aMaterial, long aMaterialAmount, boolean aAllowAlloySmelter) {
		registerBasicReverseMacerating(aStack, aMaterial, aMaterialAmount, null);
		registerBasicReverseSmelting(aStack, aMaterial, aMaterialAmount, aAllowAlloySmelter);
	}
	
	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 */
	public static void registerBasicReverseMaceratingAndSmelting(ItemStack aStack, Materials aMaterial, long aMaterialAmount) {
		registerBasicReverseMaceratingAndSmelting(aStack, aMaterial, aMaterialAmount, true);
	}
	
	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 * @param aAllowAlloySmelter if it is allowed to be recycled inside the Alloy Smelter.
	 */
	public static void registerBasicReverseSmelting(ItemStack aStack, Materials aMaterial, long aMaterialAmount, boolean aAllowAlloySmelter) {
		if (aStack == null || aMaterial == null || aMaterialAmount <= 0) return;
		aMaterialAmount /= aStack.stackSize;
		if (aAllowAlloySmelter)
			GT_ModHandler.addSmeltingAndAlloySmeltingRecipe(GT_Utility.copyAmount(1, aStack), GT_OreDictUnificator.getIngot(aMaterial.mSmeltInto, aMaterialAmount));
		else
			GT_ModHandler.addSmeltingRecipe(GT_Utility.copyAmount(1, aStack), GT_OreDictUnificator.getIngot(aMaterial.mSmeltInto, aMaterialAmount));
	}
	
	/**
	 * @param aStack the stack to be recycled.
	 * @param aMaterial the Material.
	 * @param aMaterialAmount the amount of it in Material Units.
	 */
	public static void registerBasicReverseMacerating(ItemStack aStack, Materials aMaterial, long aMaterialAmount, MaterialStack aByProducts) {
		if (aStack == null || aMaterial == null || aMaterialAmount <= 0) return;
		aMaterialAmount /= aStack.stackSize;
		ItemStack tDust = GT_OreDictUnificator.getDust(aMaterial.mMacerateInto, aMaterialAmount);
		if (tDust != null && GT_ModHandler.addPulverisationRecipe(GT_Utility.copyAmount(1, aStack), tDust, GT_OreDictUnificator.getDust(aByProducts), 100, true)) {
			if (GregTech_API.sThaumcraftCompat != null) GregTech_API.sThaumcraftCompat.addCrucibleRecipe(IThaumcraftCompat.ADVANCEDENTROPICPROCESSING, aStack, tDust, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, Math.max(1, (aMaterialAmount * 2) / GregTech_API.MATERIAL_UNIT))));
		}
	}
	
	/**
	 * You give this Function a Material and it will scan almost everything for adding recycling Recipes
	 * 
	 * @param aMat a Material, for example an Ingot or a Gem.
	 * @param aOutput the Dust you usually get from macerating aMat
	 * @param aBackSmelting allows to reverse smelt into aMat (false for Gems)
	 * @param aBackMacerating allows to reverse macerate into aOutput
	 */
	public static void registerUsagesForMaterials(ItemStack aMat, ItemStack aOutput, String aPlate, boolean aBackSmelting, boolean aBackMacerating, boolean aRecipeReplacing) {
		if (aMat == null || aOutput == null) return;
		aMat = GT_Utility.copy(aMat);
		aOutput = GT_Utility.copy(aOutput);
		ItemStack tStack, tUnificated = GT_OreDictUnificator.get(true, aMat);
		OrePrefixMaterialData aAssotiation = GT_OreDictUnificator.getAssociation(aMat);
		if (aOutput.stackSize < 1) aOutput.stackSize = 1;
		if (aAssotiation == null || !aAssotiation.mPrefix.name().startsWith(OrePrefixes.ingot.toString())) aPlate = null;
		if (aPlate != null && GT_OreDictUnificator.getFirstOre(aPlate, 1) == null) aPlate = null;
		
		if (!GT_Utility.areStacksEqual(GT_OreDictUnificator.get(aMat), new ItemStack(Items.iron_ingot, 1))) {
			if ((tStack = GT_ModHandler.getRecipeOutput(new ItemStack[] {aMat, null, aMat, null, aMat, null, null, null, null})) != null)
				if (GT_Utility.areStacksEqual(tStack, new ItemStack(Items.bucket, 1)))
					GT_ModHandler.removeRecipe(aMat, null, aMat, null, aMat, null, null, null, null);
			if ((tStack = GT_ModHandler.getRecipeOutput(new ItemStack[] {null, null, null, aMat, null, aMat, null, aMat, null})) != null)
				if (GT_Utility.areStacksEqual(tStack, new ItemStack(Items.bucket, 1)))
					GT_ModHandler.removeRecipe(null, null, null, aMat, null, aMat, null, aMat, null);
			if ((tStack = GT_ModHandler.getRecipeOutput(new ItemStack[] {aMat, null, aMat, aMat, aMat, aMat, null, null, null})) != null)
				if (GT_Utility.areStacksEqual(tStack, new ItemStack(Items.minecart, 1)))
					GT_ModHandler.removeRecipe(aMat, null, aMat, aMat, aMat, aMat, null, null, null);
			if ((tStack = GT_ModHandler.getRecipeOutput(new ItemStack[] {null, null, null, aMat, null, aMat, aMat, aMat, aMat})) != null)
				if (GT_Utility.areStacksEqual(tStack, new ItemStack(Items.minecart, 1)))
					GT_ModHandler.removeRecipe(null, null, null, aMat, null, aMat, aMat, aMat, aMat);
		}
		
		if (aBackMacerating || aBackSmelting) {
			sMt1.func_150996_a(aMat.getItem());
			sMt1.stackSize = 1;
			Items.feather.setDamage(sMt1, Items.feather.getDamage(aMat));
			
			for (ItemStack[] tRecipe : sShapes1) {
				int tAmount1 = 0;
				for (ItemStack tMat : tRecipe) {
					if (tMat == sMt1) tAmount1++;
				}
				for (ItemStack tCrafted : GT_ModHandler.getRecipeOutputs(tRecipe)) {
					if (aBackMacerating) GT_ModHandler.addPulverisationRecipe(tCrafted, GT_Utility.copyAmount(tAmount1, aOutput), null, 0, false);
					if (aBackSmelting) GT_ModHandler.addSmeltingAndAlloySmeltingRecipe(tCrafted, GT_Utility.copyAmount(tAmount1, tUnificated));
				}
			}
			
		    for (Materials tMaterial : sRodMaterialList) {
		    	ItemStack tMt2 = GT_OreDictUnificator.get(OrePrefixes.stick, tMaterial, 1), tMt3 = GT_OreDictUnificator.get(OrePrefixes.dustSmall, tMaterial, 2);
		    	if (tMt2 != null) {
					sMt2.func_150996_a(tMt2.getItem());
					sMt2.stackSize = 1;
					Items.feather.setDamage(sMt2, Items.feather.getDamage(tMt2));
					
					for (int i = 0; i < sShapes1.length; i++) {
						ItemStack[] tRecipe = sShapes1[i];
						
						int tAmount1 = 0, tAmount2 = 0;
						for (ItemStack tMat : tRecipe) {
							if (tMat == sMt1) tAmount1++;
							if (tMat == sMt2) tAmount2++;
						}
						for (ItemStack tCrafted : GT_ModHandler.getVanillyToolRecipeOutputs(tRecipe)) {
							if (aBackMacerating) GT_ModHandler.addPulverisationRecipe(tCrafted, GT_Utility.copyAmount(tAmount1, aOutput), tAmount2>0?GT_Utility.mul(tAmount2, tMt3):null, 100, false);
							if (aBackSmelting) GT_ModHandler.addSmeltingAndAlloySmeltingRecipe(tCrafted, GT_Utility.copyAmount(tAmount1, tUnificated));
							if (aRecipeReplacing && aPlate != null && sShapesA[i] != null && sShapesA[i].length > 1) {
								assert aAssotiation != null;
								if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.recipereplacements, aAssotiation.mMaterial+"."+sShapesA[i][0], true)) {
									if (null != (tStack = GT_ModHandler.removeRecipe(tRecipe))) {
										switch (sShapesA[i].length) {
										case  2: GT_ModHandler.addCraftingRecipe(tStack, GT_ModHandler.RecipeBits.BUFFERED, new Object[] {sShapesA[i][1]									, s_P.charAt(0), aPlate, s_R.charAt(0), OrePrefixes.stick.toString() + tMaterial, s_I.charAt(0), aAssotiation}); break;
										case  3: GT_ModHandler.addCraftingRecipe(tStack, GT_ModHandler.RecipeBits.BUFFERED, new Object[] {sShapesA[i][1], sShapesA[i][2]					, s_P.charAt(0), aPlate, s_R.charAt(0), OrePrefixes.stick.toString() + tMaterial, s_I.charAt(0), aAssotiation}); break;
										default: GT_ModHandler.addCraftingRecipe(tStack, GT_ModHandler.RecipeBits.BUFFERED, new Object[] {sShapesA[i][1], sShapesA[i][2], sShapesA[i][3]	, s_P.charAt(0), aPlate, s_R.charAt(0), OrePrefixes.stick.toString() + tMaterial, s_I.charAt(0), aAssotiation}); break;
										}
									}
								}
							}
						}
					}
		    	}
		    }
		}
	}
	
	private static final ItemStack sMt1 = new ItemStack(Blocks.dirt, 1, 0), sMt2 = new ItemStack(Blocks.dirt, 1, 1);
	private static final String s_H = "h", s_F = "f", s_I = "I", s_P = "P", s_R = "R";
	
	private static final ItemStack[][]
		sShapes1 = new ItemStack[][] {
		{sMt1, null, sMt1, sMt1, sMt1, sMt1, null, sMt1, null},
		{null, sMt1, null, sMt1, sMt1, sMt1, sMt1, null, sMt1},
		{sMt1, sMt1, sMt1, sMt1, null, sMt1, null, null, null},
	    {sMt1, null, sMt1, sMt1, sMt1, sMt1, sMt1, sMt1, sMt1},
	    {sMt1, sMt1, sMt1, sMt1, null, sMt1, sMt1, null, sMt1},
	    {null, null, null, sMt1, null, sMt1, sMt1, null, sMt1},
		{null, sMt1, null, null, sMt1, null, null, sMt2, null},
	    {sMt1, sMt1, sMt1, null, sMt2, null, null, sMt2, null},
	    {null, sMt1, null, null, sMt2, null, null, sMt2, null},
	    {sMt1, sMt1, null, sMt1, sMt2, null, null, sMt2, null},
	    {null, sMt1, sMt1, null, sMt2, sMt1, null, sMt2, null},
	    {sMt1, sMt1, null, null, sMt2, null, null, sMt2, null},
	    {null, sMt1, sMt1, null, sMt2, null, null, sMt2, null},
	    {null, sMt1, null, sMt1, null, null, null, sMt1, sMt2},
	    {null, sMt1, null, null, null, sMt1, sMt2, sMt1, null},
	    {null, sMt1, null, sMt1, null, sMt1, null, null, sMt2},
	    {null, sMt1, null, sMt1, null, sMt1, sMt2, null, null},
	    {null, sMt2, null, null, sMt1, null, null, sMt1, null},
	    {null, sMt2, null, null, sMt2, null, sMt1, sMt1, sMt1},
	    {null, sMt2, null, null, sMt2, null, null, sMt1, null},
	    {null, sMt2, null, sMt1, sMt2, null, sMt1, sMt1, null},
	    {null, sMt2, null, null, sMt2, sMt1, null, sMt1, sMt1},
	    {null, sMt2, null, null, sMt2, null, sMt1, sMt1, null},
	    {sMt1, null, null, null, sMt2, null, null, null, sMt2},
	    {null, null, sMt1, null, sMt2, null, sMt2, null, null},
	    {sMt1, null, null, null, sMt2, null, null, null, null},
	    {null, null, sMt1, null, sMt2, null, null, null, null},
	    {sMt1, sMt2, null, null, null, null, null, null, null},
	    {sMt2, sMt1, null, null, null, null, null, null, null},
	    {sMt1, null, null, sMt2, null, null, null, null, null},
	    {sMt2, null, null, sMt1, null, null, null, null, null},
	    {sMt1, sMt1, sMt1, sMt1, sMt1, sMt1, null, sMt2, null},
		{sMt1, sMt1, null, sMt1, sMt1, sMt2, sMt1, sMt1, null},
		{null, sMt1, sMt1, sMt2, sMt1, sMt1, null, sMt1, sMt1},
		{null, sMt2, null, sMt1, sMt1, sMt1, sMt1, sMt1, sMt1},
		{sMt1, sMt1, sMt1, sMt1, sMt2, sMt1, null, sMt2, null},
		{sMt1, sMt1, null, sMt1, sMt2, sMt2, sMt1, sMt1, null},
		{null, sMt1, sMt1, sMt2, sMt2, sMt1, null, sMt1, sMt1},
		{null, sMt2, null, sMt1, sMt2, sMt1, sMt1, sMt1, sMt1},
		{sMt1, null, null, null, sMt1, null, null, null, null},
		{null, sMt1, null, sMt1, null, null, null, null, null},
		{sMt1, sMt1, null, sMt2, null, sMt1, sMt2, null, null},
		{null, sMt1, sMt1, sMt1, null, sMt2, null, null, sMt2}
	};
	
	private static final String[][] sShapesA = new String[][] {
		null,
		null,
		{"Helmet"						, s_P+s_P+s_P, s_P+s_H+s_P},
		{"ChestPlate"					, s_P+s_H+s_P, s_P+s_P+s_P, s_P+s_P+s_P},
		{"Pants"						, s_P+s_P+s_P, s_P+s_H+s_P, s_P+" "+s_P},
		{"Boots"						, s_P+" "+s_P, s_P+s_H+s_P},
		{"Sword"						, " "+s_P+" ", s_F+s_P+s_H, " "+s_R+" "},
		{"Pickaxe"						, s_P+s_I+s_I, s_F+s_R+s_H, " "+s_R+" "},
		{"Shovel"						, s_F+s_P+s_H, " "+s_R+" ", " "+s_R+" "},
		{"Axe"							, s_P+s_I+s_H, s_P+s_R+" ", s_F+s_R+" "},
		{"Axe"							, s_P+s_I+s_H, s_P+s_R+" ", s_F+s_R+" "},
		{"Hoe"							, s_P+s_I+s_H, s_F+s_R+" ", " "+s_R+" "},
		{"Hoe"							, s_P+s_I+s_H, s_F+s_R+" ", " "+s_R+" "},
		{"Sickle"						, " "+s_P+" ", s_P+s_F+" ", s_H+s_P+s_R},
		{"Sickle"						, " "+s_P+" ", s_P+s_F+" ", s_H+s_P+s_R},
		{"Sickle"						, " "+s_P+" ", s_P+s_F+" ", s_H+s_P+s_R},
		{"Sickle"						, " "+s_P+" ", s_P+s_F+" ", s_H+s_P+s_R},
		{"Sword"						, " "+s_R+" ", s_F+s_P+s_H, " "+s_P+" "},
		{"Pickaxe"						, " "+s_R+" ", s_F+s_R+s_H, s_P+s_I+s_I},
		{"Shovel"						, " "+s_R+" ", " "+s_R+" ", s_F+s_P+s_H},
		{"Axe"							, s_F+s_R+" ", s_P+s_R+" ", s_P+s_I+s_H},
		{"Axe"							, s_F+s_R+" ", s_P+s_R+" ", s_P+s_I+s_H},
		{"Hoe"							, " "+s_R+" ", s_F+s_R+" ", s_P+s_I+s_H},
		{"Hoe"							, " "+s_R+" ", s_F+s_R+" ", s_P+s_I+s_H},
		{"Spear"						, s_P+s_H+" ", s_F+s_R+" ", " "+" "+s_R},
		{"Spear"						, s_P+s_H+" ", s_F+s_R+" ", " "+" "+s_R},
		{"Knive"						, s_H+s_P, s_R+s_F},
		{"Knive"						, s_F+s_H, s_P+s_R},
		{"Knive"						, s_F+s_H, s_P+s_R},
		{"Knive"						, s_P+s_F, s_R+s_H},
		{"Knive"						, s_P+s_F, s_R+s_H},
		null,
		null,
		null,
		null,
		{"WarAxe"						, s_P+s_P+s_P, s_P+s_R+s_P, s_F+s_R+s_H},
		null,
		null,
		null,
		{"Shears"						, s_H+s_P, s_P+s_F},
		{"Shears"						, s_H+s_P, s_P+s_F},
		{"Scythe"						, s_I+s_P+s_H, s_R+s_F+s_P, s_R+" "+" "},
		{"Scythe"						, s_H+s_P+s_I, s_P+s_F+s_R, " "+" "+s_R}
	};
}