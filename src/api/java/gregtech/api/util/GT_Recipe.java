package gregtech.api.util;

import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.objects.GT_ArrayList;
import gregtech.api.objects.GT_ItemStack;

import java.util.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * 
 * This File contains the functions used for Recipes. Please do not include this File AT ALL in your Moddownload as it ruins compatibility
 * This is just the Core of my Recipe System, if you just want to GET the Recipes I add, then you can access this File.
 * Do NOT add Recipes using the Constructors inside this Class, The GregTech_API File calls the correct Functions for these Constructors.
 * 
 * I know this File causes some Errors, because of missing Main Functions, but if you just need to compile Stuff, then remove said erroreous Functions.
 */
public class GT_Recipe {
	public static volatile int VERSION = 503;
	
	public static class GT_Recipe_Map {
		/** Contains all Recipe Maps */
		public static final List<GT_Recipe_Map> sMappings				= new ArrayList<GT_Recipe_Map>();
		
		public static final GT_Recipe_Map sScannerFakeRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,    3), "gt.recipe.scanner"						, "Scanner"							, GregTech_API.GUI_PATH+"basicmachines/Scanner"				, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sRockBreakerFakeRecipes		= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,    3), "gt.recipe.rockbreaker"					, "Rock Breaker"					, GregTech_API.GUI_PATH+"basicmachines/RockBreaker"			, 1, 1, ""					,    1, ""		, true						);
		
		public static final GT_Recipe_Map sMaceratorRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 6000), "gt.recipe.macerator"						, "Pulverization"					, GregTech_API.GUI_PATH+"basicmachines/Macerator3"			, 1, 3, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sChemicalBathRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 1000), "gt.recipe.chemicalbath"					, "Chemical Bath"					, GregTech_API.GUI_PATH+"basicmachines/ChemicalBath"		, 1, 3, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sFluidCannerRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.fluidcanner"					, "Fluid Canning Machine"			, GregTech_API.GUI_PATH+"basicmachines/FluidCannerNEI"		, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sBrewingRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.brewer"						, "Brewing Machine"					, GregTech_API.GUI_PATH+"basicmachines/PotionBrewer"		, 1, 0, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sDistilleryRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.distillery"					, "Distillery"						, GregTech_API.GUI_PATH+"basicmachines/Distillery"			, 1, 0, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sFermentingRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.fermenter"						, "Fermenter"						, GregTech_API.GUI_PATH+"basicmachines/Fermenter"			, 0, 0, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sFluidSolidficationRecipes	= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.fluidsolidifier"				, "Fluid Solidifier"				, GregTech_API.GUI_PATH+"basicmachines/FluidSolidifier"		, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sFluidExtractionRecipes		= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.fluidextractor"				, "Fluid Extractor"					, GregTech_API.GUI_PATH+"basicmachines/FluidExtractor"		, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sBoxinatorRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 2500), "gt.recipe.packager"						, "Packager"						, GregTech_API.GUI_PATH+"basicmachines/Packager"			, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sUnboxinatorRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 2500), "gt.recipe.unpackager"					, "Unpackager"						, GregTech_API.GUI_PATH+"basicmachines/Unpackager"			, 1, 2, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sFusionRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   50), "gt.recipe.fusionreactor"					, "Fusion Reactor"					, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 1, "Start: "			,    1, " EU"	, false						);
		public static final GT_Recipe_Map sCentrifugeRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 1000), "gt.recipe.centrifuge"					, "Centrifuge"						, GregTech_API.GUI_PATH+"basicmachines/Centrifuge"			, 2, 6, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sElectrolyzerRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  200), "gt.recipe.electrolyzer"					, "Electrolyzer"					, GregTech_API.GUI_PATH+"basicmachines/Electrolyzer"		, 2, 6, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sGrinderRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  200), "gt.recipe.grinder"						, "Grinder"							, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 4, ""					,    1, ""		, false						);
		public static final GT_Recipe_Map sBlastRecipes					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  500), "gt.recipe.blastfurnace"					, "Blast Furnace"					, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 2, "Heat Capacity: "	,    1, " K"	, true						);
		public static final GT_Recipe_Map sImplosionRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   50), "gt.recipe.implosioncompressor"			, "Implosion Compressor"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 2, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sVacuumRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.vacuumfreezer"					, "Vacuum Freezer"					, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sChemicalRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.chemicalreactor"				, "Chemical Reactor"				, GregTech_API.GUI_PATH+"basicmachines/ChemicalReactor"		, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sDistillationRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   50), "gt.recipe.distillationtower"				, "Distillation Tower"				, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 4, ""					,    1, ""		, false						);
		public static final GT_Recipe_Map sWiremillRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   50), "gt.recipe.wiremill"						, "Wiremill"						, GregTech_API.GUI_PATH+"basicmachines/Wiremill"			, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sBenderRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  400), "gt.recipe.metalbender"					, "Metal Bender"					, GregTech_API.GUI_PATH+"basicmachines/Bender"				, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sAlloySmelterRecipes			= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 6000), "gt.recipe.alloysmelter"					, "Alloy Smelter"					, GregTech_API.GUI_PATH+"basicmachines/AlloySmelter"		, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sAssemblerRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  300), "gt.recipe.assembler"						, "Assembler"						, GregTech_API.GUI_PATH+"basicmachines/Assembler"			, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sCannerRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  300), "gt.recipe.canner"						, "Canning Machine"					, GregTech_API.GUI_PATH+"basicmachines/Canner"				, 2, 2, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sCNCRecipes					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  100), "gt.recipe.cncmachine"					, "CNC Machine"						, GregTech_API.GUI_PATH+"basicmachines/Default"				, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sLatheRecipes					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  400), "gt.recipe.lathe"							, "Lathe"							, GregTech_API.GUI_PATH+"basicmachines/Lathe"				, 1, 2, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sCutterRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  200), "gt.recipe.cuttingsaw"					, "Cutting Saw"						, GregTech_API.GUI_PATH+"basicmachines/Cutter"				, 1, 2, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sExtruderRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false, 1000), "gt.recipe.extruder"						, "Extruder"						, GregTech_API.GUI_PATH+"basicmachines/Extruder"			, 2, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sHammerRecipes				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,  200), "gt.recipe.hammer"						, "Hammer"							, GregTech_API.GUI_PATH+"basicmachines/SteelHammer"			, 1, 1, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sAmplifiers					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.uuamplifier"					, "UU Amplifier"					, GregTech_API.GUI_PATH+"basicmachines/Amplifabricator"		, 1, 0, ""					,    1, ""		, true						);
		public static final GT_Recipe_Map sDieselFuels					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.dieselgeneratorfuel"			, "Diesel Generator Fuel"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		public static final GT_Recipe_Map sTurbineFuels					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.gasturbinefuel"				, "Gas Turbine Fuel"				, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		public static final GT_Recipe_Map sHotFuels						= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.thermalgeneratorfuel"			, "Thermal Generator Fuel"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, false						);
		public static final GT_Recipe_Map sDenseLiquidFuels				= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.semifluidboilerfuels"			, "Semifluid Boiler Fuels"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		public static final GT_Recipe_Map sPlasmaFuels					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.plasmageneratorfuels"			, "Plasma generator Fuels"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, false						);
		public static final GT_Recipe_Map sMagicFuels					= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.magicfuels"					, "Magic Fuels"						, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, false						);
		public static final GT_Recipe_Map sSmallNaquadahReactorFuels	= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.smallnaquadahreactor"			, "Small Naquadah Reactor"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		public static final GT_Recipe_Map sLargeNaquadahReactorFuels	= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.largenaquadahreactor"			, "Large Naquadah Reactor"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		public static final GT_Recipe_Map sFluidNaquadahReactorFuels	= new GT_Recipe_Map(new GT_ArrayList<GT_Recipe>(false,   10), "gt.recipe.fluidnaquadahreactor"			, "Fluid Naquadah Reactor"			, GregTech_API.GUI_PATH+"basicmachines/Default"				, 1, 1, "Fuel Value: "		, 1000, " EU"	, true						);
		
		/** HashMap of Recipes based on their Items */
		public final Map<GT_ItemStack, List<GT_Recipe>> mRecipeItemMap = new HashMap<GT_ItemStack, List<GT_Recipe>>();
		/** HashMap of Recipes based on their Fluids */
		public final Map<Fluid, List<GT_Recipe>> mRecipeFluidMap = new HashMap<Fluid, List<GT_Recipe>>();
		/** The List of all Recipes */
		public final List<GT_Recipe> mRecipeList;
		/** String used as reference for this Recipe Handler. Used in NEI for the Overlays as well as an unlocalised Name. */
		public final String mUnlocalizedName;
		/** GUI used for NEI Display. Usually the GUI of the Machine itself */
		public final String mNEIGUIPath;
		public final String mNEISpecialValuePre, mNEISpecialValuePost;
		public final int mUsualInputCount, mUsualOutputCount, mNEISpecialValueMultiplier;
		public final boolean mNEIAllowed;
		
		/**
		 * Initialises a new type of Recipe Handler.
		 * @param aRecipeList a List you specify as Recipe List. Usually just an ArrayList with a pre-initialised Size.
		 * @param aUnlocalizedName the unlocalised Name of this Recipe Handler, used mainly for NEI.
		 * @param aLocalName the displayed Name inside the NEI Recipe GUI.
		 * @param aNEIGUIPath the displayed GUI Texture, usually just a Machine GUI. Auto-Attaches ".png" if forgotten.
		 * @param aUsualInputCount the usual amount of Input Slots this Recipe Class has.
		 * @param aUsualOutputCount the usual amount of Output Slots this Recipe Class has.
		 * @param aNEISpecialValuePre the String in front of the Special Value in NEI.
		 * @param aNEISpecialValueMultiplier the Value the Special Value is getting Multiplied with before displaying
		 * @param aNEISpecialValuePost the String after the Special Value. Usually for a Unit or something.
		 * @param aNEIAllowed if NEI is allowed to display this Recipe Handler in general.
		 */
		public GT_Recipe_Map(List<GT_Recipe> aRecipeList, String aUnlocalizedName, String aLocalName, String aNEIGUIPath, int aUsualInputCount, int aUsualOutputCount, String aNEISpecialValuePre, int aNEISpecialValueMultiplier, String aNEISpecialValuePost, boolean aNEIAllowed) {
			sMappings.add(this);
			mNEIAllowed = aNEIAllowed;
			mRecipeList = aRecipeList;
			mNEIGUIPath = aNEIGUIPath.endsWith(".png")?aNEIGUIPath:aNEIGUIPath + ".png";
			mNEISpecialValuePre = aNEISpecialValuePre;
			mNEISpecialValueMultiplier = aNEISpecialValueMultiplier;
			mNEISpecialValuePost = aNEISpecialValuePost;
			mUsualInputCount = aUsualInputCount;
			mUsualOutputCount = aUsualOutputCount;
			GregTech_API.sItemStackMappings.add(mRecipeItemMap);
			GT_LanguageManager.addStringLocalization(mUnlocalizedName = aUnlocalizedName, aLocalName);
		}
		
		public GT_Recipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, ItemStack aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
			return addRecipe(new GT_Recipe(aOptimize, aInputs, aOutputs, aSpecial, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
		}
		
		public GT_Recipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, ItemStack aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
			return addRecipe(new GT_Recipe(aOptimize, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
		}
		
		public GT_Recipe addRecipe(GT_Recipe aRecipe) {
			if (aRecipe.mFluidInputs.length <= 0 && aRecipe.mInputs.length <= 0) return null;
			if (findRecipe(false, Long.MAX_VALUE, aRecipe.mFluidInputs, aRecipe.mInputs) != null) return null;
			return add(aRecipe);
		}
		
		/** Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes */
		public GT_Recipe addFakeRecipe(boolean aCheckForCollisions, ItemStack[] aInputs, ItemStack[] aOutputs, ItemStack aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
			return addFakeRecipe(aCheckForCollisions, new GT_Recipe(false, aInputs, aOutputs, aSpecial, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
		}
		
		/** Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes */
		public GT_Recipe addFakeRecipe(boolean aCheckForCollisions, ItemStack[] aInputs, ItemStack[] aOutputs, ItemStack aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
			return addFakeRecipe(aCheckForCollisions, new GT_Recipe(false, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
		}
		
		/** Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes */
		public GT_Recipe addFakeRecipe(boolean aCheckForCollisions, GT_Recipe aRecipe) {
			aRecipe.mFakeRecipe = true;
			if (aCheckForCollisions) {
				if (findRecipe(false, Long.MAX_VALUE, aRecipe.mFluidInputs, aRecipe.mInputs) != null) return null;
			}
			return add(aRecipe);
		}
		
		public GT_Recipe add(GT_Recipe aRecipe) {
			mRecipeList.add(aRecipe);
			for (FluidStack aFluid : aRecipe.mFluidInputs) if (aFluid != null) {
				List<GT_Recipe> tList = mRecipeFluidMap.get(aFluid.getFluid());
				if (tList == null) mRecipeFluidMap.put(aFluid.getFluid(), tList = new ArrayList<GT_Recipe>(1));
				tList.add(aRecipe);
			}
			return addToItemMap(aRecipe);
		}
		
		public void reInit() {
        	Map<GT_ItemStack, List<GT_Recipe>> tMap = mRecipeItemMap;
        	if (tMap != null) tMap.clear();
        	for (GT_Recipe tRecipe : mRecipeList) {
            	GT_OreDictUnificator.setStackArray(true, tRecipe.mInputs);
            	GT_OreDictUnificator.setStackArray(true, tRecipe.mOutputs);
            	if (tMap != null) addToItemMap(tRecipe);
        	}
		}
		
		/** @return if this Item is a valid Input for any for the Recipes */
		public boolean containsInput(ItemStack aStack) {
			return aStack != null && (mRecipeItemMap.containsKey(new GT_ItemStack(aStack)) || mRecipeItemMap.containsKey(new GT_ItemStack(GT_Utility.copyMetaData(GregTech_API.ITEM_WILDCARD_DAMAGE, aStack))));
		}
		
		/** @return if this Fluid is a valid Input for any for the Recipes */
		public boolean containsInput(FluidStack aFluid) {
			return aFluid != null && containsInput(aFluid.getFluid());
		}
		
		/** @return if this Fluid is a valid Input for any for the Recipes */
		public boolean containsInput(Fluid aFluid) {
			return aFluid != null && mRecipeFluidMap.containsKey(aFluid);
		}
		
		/**
		 * finds a Recipe matching the aFluid and ItemStack Inputs.
		 * @param aNotUnificated if this is true the Recipe searcher will unificate the ItemStack Inputs
		 * @param aVoltage Voltage of the Machine or Long.MAX_VALUE if it has no Voltage
		 * @param aFluids the Fluid Inputs
		 * @param aInputs the Item Inputs
		 * @return the Recipe it has found or null for no matching Recipe
		 */
		public GT_Recipe findRecipe(boolean aNotUnificated, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
			return findRecipe(null, aNotUnificated, aVoltage, aFluids, aInputs);
		}
		
		/**
		 * finds a Recipe matching the aFluid and ItemStack Inputs.
		 * @param aRecipe in case this is != null it will try to use this Recipe first when looking things up.
		 * @param aNotUnificated if this is true the Recipe searcher will unificate the ItemStack Inputs
		 * @param aVoltage Voltage of the Machine or Long.MAX_VALUE if it has no Voltage
		 * @param aFluids the Fluid Inputs
		 * @param aInputs the Item Inputs
		 * @return the Recipe it has found or null for no matching Recipe
		 */
		public GT_Recipe findRecipe(GT_Recipe aRecipe, boolean aNotUnificated, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
			if (mRecipeList.isEmpty()) return null;
			
			if (aNotUnificated) aInputs = GT_OreDictUnificator.getStackArray(true, (Object[])aInputs);
			
			if (aRecipe != null) if (!aRecipe.mFakeRecipe && aRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) return aRecipe.mEnabled&&aVoltage>=aRecipe.mEUt?aRecipe:null;
			
			if (aInputs != null) for (ItemStack tStack : aInputs) if (tStack != null) {
				List<GT_Recipe> tRecipes = mRecipeItemMap.get(new GT_ItemStack(tStack));
				if (tRecipes != null) for (GT_Recipe tRecipe : tRecipes) if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) return tRecipe.mEnabled&&aVoltage>=tRecipe.mEUt?tRecipe:null;
				tRecipes = mRecipeItemMap.get(new GT_ItemStack(GT_Utility.copyMetaData(GregTech_API.ITEM_WILDCARD_DAMAGE, tStack)));
				if (tRecipes != null) for (GT_Recipe tRecipe : tRecipes) if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) return tRecipe.mEnabled&&aVoltage>=tRecipe.mEUt?tRecipe:null;
			}
			
			if (aFluids != null) for (FluidStack aFluid : aFluids) if (aFluid != null) {
				List<GT_Recipe> tRecipes = mRecipeFluidMap.get(aFluid.getFluid());
				if (tRecipes != null) for (GT_Recipe tRecipe : tRecipes) if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) return tRecipe.mEnabled&&aVoltage>=tRecipe.mEUt?tRecipe:null;
			}
			
			return null;
		}
		
		protected GT_Recipe addToItemMap(GT_Recipe aRecipe) {
			for (ItemStack aStack : aRecipe.mInputs) if (aStack != null) {
				GT_ItemStack tStack = new GT_ItemStack(aStack);
				List<GT_Recipe> tList = mRecipeItemMap.get(tStack);
				if (tList == null) mRecipeItemMap.put(tStack, tList = new ArrayList<GT_Recipe>(1));
				tList.add(aRecipe);
			}
			return aRecipe;
		}
	}
	
	public static void reInit() {
        GT_Log.out.println("GT_Mod: Re-Unificating Recipes.");
        for (GT_Recipe_Map tMapEntry : GT_Recipe_Map.sMappings) tMapEntry.reInit();
	}
	
	/** If you want to change the Output, feel free to modify or even replace the whole ItemStack Array, for Inputs, please add a new Recipe, because of the HashMaps. */
	public ItemStack[] mInputs, mOutputs;
	/** If you want to change the Output, feel free to modify or even replace the whole ItemStack Array, for Inputs, please add a new Recipe, because of the HashMaps. */
	public FluidStack[] mFluidInputs, mFluidOutputs;
	/** If you changed the amount of Array-Items inside the Output Array then the length of this Array must be larger or equal to the Output Array. A chance of 10000 equals 100% */
	public int[] mChances;
	/** An Item that needs to be inside the Special Slot, like for example the Copy Slot inside the Printer. This is only useful for Fake Recipes, since findRecipe() and containsInput() don't give a shit about this Field */
	public ItemStack mSpecialItem;
	
	public int mDuration, mEUt, mSpecialValue;
	
	/** Use this to just disable a specific Recipe, but the Configuration enables that already for every single Recipe. */
	public boolean mEnabled = true;
	/** If this Recipe is hidden from NEI */
	public boolean mHidden = false;
	/** If this Recipe is Fake and therefore doesn't get found by the findRecipe Function (It is still in the HashMaps, so that containsInput does return true on those fake Inputs) */
	public boolean mFakeRecipe = false;
	/** If this Recipe can be stored inside a Machine in order to make Recipe searching more Efficient by trying the previously used Recipe first. In case you have a Recipe Map overriding things and returning one time use Recipes, you have to set this to false. */
	public boolean mCanBeBuffered = true;
	
	public ItemStack getRepresentativeInput(int aIndex) {if (aIndex < 0 || aIndex >= mInputs.length) return null; return GT_Utility.copy(mInputs[aIndex]);}
	public ItemStack getOutput(int aIndex) {if (aIndex < 0 || aIndex >= mOutputs.length) return null; return GT_Utility.copy(mOutputs[aIndex]);}
	
	public int getOutputChance(int aIndex) {if (aIndex < 0 || aIndex >= mChances.length) return 10000; return mChances[aIndex];}
	
	public FluidStack getRepresentativeFluidInput(int aIndex) {if (aIndex < 0 || aIndex >= mFluidInputs.length || mFluidInputs[aIndex] == null) return null; return mFluidInputs[aIndex].copy();}
	public FluidStack getFluidOutput(int aIndex) {if (aIndex < 0 || aIndex >= mFluidOutputs.length || mFluidOutputs[aIndex] == null) return null; return mFluidOutputs[aIndex].copy();}
	
	public void checkCellBalance() {
		if (!GregTech_API.SECONDARY_DEBUG_MODE || mInputs.length < 1) return;
		
		int tInputAmount  = GT_ModHandler.getCapsuleCellContainerCountMultipliedWithStackSize(mInputs);
		int tOutputAmount = GT_ModHandler.getCapsuleCellContainerCountMultipliedWithStackSize(mOutputs);
		
		if (tInputAmount < tOutputAmount) {
			if (!Materials.Tin.contains(mInputs)) {
				GT_Log.err.println("You get more Cells, than you put in? There must be something wrong.");
				new Exception().printStackTrace(GT_Log.err);
			}
		} else if (tInputAmount > tOutputAmount) {
			if (!Materials.Tin.contains(mOutputs)) {
				GT_Log.err.println("You get less Cells, than you put in? GT Machines usually don't destroy Cells.");
				new Exception().printStackTrace(GT_Log.err);
			}
		}
	}
	
	public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, FluidStack[] aFluidInputs, ItemStack... aInputs) {
		return isRecipeInputEqual(aDecreaseStacksizeBySuccess, false, aFluidInputs, aInputs);
	}
	
	public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, boolean aDontCheckStackSizes, FluidStack[] aFluidInputs, ItemStack... aInputs) {
		if (mFluidInputs.length > 0 && aFluidInputs == null) return false;
		for (FluidStack tFluid : mFluidInputs) if (tFluid != null) {
			boolean temp = true;
			for (FluidStack aFluid : aFluidInputs) if (aFluid != null && aFluid.isFluidEqual(tFluid) && (aDontCheckStackSizes || aFluid.amount >= tFluid.amount)) {temp = false; break;}
			if (temp) return false;
		}
		
		if (mInputs.length > 0 && aInputs == null) return false;
		for (ItemStack tStack : mInputs) if (tStack != null) {
			boolean temp = true;
			for (ItemStack aStack : aInputs) if ((GT_Utility.areUnificationsEqual(aStack, tStack, true) || GT_Utility.areUnificationsEqual(GT_OreDictUnificator.get(false, aStack), tStack, true)) && (aDontCheckStackSizes || aStack.stackSize >= tStack.stackSize)) {temp = false; break;}
			if (temp) return false;
		}
		
		if (aDecreaseStacksizeBySuccess) {
			if (aFluidInputs != null) {
				for (FluidStack tFluid : mFluidInputs) if (tFluid != null) {
					for (FluidStack aFluid : aFluidInputs) if (aFluid != null && aFluid.isFluidEqual(tFluid) && (aDontCheckStackSizes || aFluid.amount >= tFluid.amount)) {aFluid.amount -= tFluid.amount; break;}
				}
			}
			
			if (aInputs != null) {
				for (ItemStack tStack : mInputs) if (tStack != null) {
					for (ItemStack aStack : aInputs) if ((GT_Utility.areUnificationsEqual(aStack, tStack, true) || GT_Utility.areUnificationsEqual(GT_OreDictUnificator.get(false, aStack), tStack, true)) && (aDontCheckStackSizes || aStack.stackSize >= tStack.stackSize)) {aStack.stackSize -= tStack.stackSize; break;}
				}
			}
		}
		
		return true;
	}
	
	protected GT_Recipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, ItemStack aSpecialItem, int[] aChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		if (aInputs == null) aInputs = new ItemStack[0];
		if (aOutputs == null) aOutputs = new ItemStack[0];
		if (aFluidInputs == null) aFluidInputs = new FluidStack[0];
		if (aFluidOutputs == null) aFluidOutputs = new FluidStack[0];
		if (aChances == null) aChances = new int[aOutputs.length];
		if (aChances.length < aOutputs.length) aChances = Arrays.copyOf(aChances, aOutputs.length);
		
		aInputs			= GT_Utility.getArrayListWithoutTrailingNulls(aInputs		).toArray(new ItemStack[0]);
		aOutputs		= GT_Utility.getArrayListWithoutTrailingNulls(aOutputs		).toArray(new ItemStack[0]);
		aFluidInputs	= GT_Utility.getArrayListWithoutNulls(aFluidInputs	).toArray(new FluidStack[0]);
		aFluidOutputs	= GT_Utility.getArrayListWithoutNulls(aFluidOutputs	).toArray(new FluidStack[0]);
		
		GT_OreDictUnificator.setStackArray(true, aInputs);
		GT_OreDictUnificator.setStackArray(true, aOutputs);
		
		for (int i = 0; i < aChances		.length; i++) if (aChances[i] <= 0) aChances[i] = 10000;
		for (int i = 0; i < aFluidInputs	.length; i++) aFluidInputs [i] = aFluidInputs [i].copy();
		for (int i = 0; i < aFluidOutputs	.length; i++) aFluidOutputs[i] = aFluidOutputs[i].copy();
		
		for (int i = 0; i < aInputs.length; i++) if (aInputs[i] != null && Items.feather.getDamage(aInputs[i]) != GregTech_API.ITEM_WILDCARD_DAMAGE) for (int j = 0; j < aOutputs.length; j++) {
			if (GT_Utility.areStacksEqual(aInputs[i], aOutputs[j])) {
				if (aInputs[i].stackSize >= aOutputs[j].stackSize) {
					aInputs[i].stackSize -= aOutputs[j].stackSize;
					aOutputs[j] = null;
				} else {
					aOutputs[j].stackSize -= aInputs[i].stackSize;
				}
			}
		}
		
		if (aOptimize && aDuration >= 32) {
			ArrayList<ItemStack> tList = new ArrayList<ItemStack>();
			tList.addAll(Arrays.asList(aInputs));
			tList.addAll(Arrays.asList(aOutputs));
			for (int i = 0; i < tList.size(); i++) if (tList.get(i) == null) tList.remove(i--);
			
			for (byte i = (byte)Math.min(64, aDuration / 16); i > 1; i--) if (aDuration / i >= 16) {
				boolean temp = true;
				for (int j = 0, k = tList.size(); temp &&  j < k; j++) if (tList.get(j).stackSize  % i != 0) temp = false;
				for (int j = 0; temp && j < aFluidInputs .length; j++) if (aFluidInputs [j].amount % i != 0) temp = false;
				for (int j = 0; temp && j < aFluidOutputs.length; j++) if (aFluidOutputs[j].amount % i != 0) temp = false;
				if (temp) {
					for (int j = 0, k = tList.size();  j < k; j++) tList.get(j).stackSize  /= i;
					for (int j = 0; j < aFluidInputs .length; j++) aFluidInputs [j].amount /= i;
					for (int j = 0; j < aFluidOutputs.length; j++) aFluidOutputs[j].amount /= i;
					aDuration /= i;
				}
			}
		}
		
		mInputs = aInputs;
		mOutputs = aOutputs;
		mSpecialItem = aSpecialItem;
		mChances = aChances;
		mFluidInputs = aFluidInputs;
		mFluidOutputs = aFluidOutputs;
		mDuration = aDuration;
		mSpecialValue = aSpecialValue;
		mEUt = aEUt;
		
//		checkCellBalance();
	}
	
	// -----
	// Old Constructors, do not use!
	// -----
	
	public GT_Recipe(ItemStack aInput1, ItemStack aOutput1, int aFuelValue, int aType) {
		this(aInput1, aOutput1, null, null, null, aFuelValue, aType);
	}
	
	// aSpecialValue = EU per Liter! If there is no Liquid for this Object, then it gets multiplied with 1000!
	public GT_Recipe(ItemStack aInput1, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, int aSpecialValue, int aType) {
		this(true, new ItemStack[] {aInput1}, new ItemStack[] {aOutput1, aOutput2, aOutput3, aOutput4}, null, null, null, null, 0, 0, Math.max(1, aSpecialValue));
		
		if (mInputs.length > 0 && aSpecialValue > 0) {
			switch (aType) {
			// Diesel Generator
			case 0:
				GT_Recipe_Map.sDieselFuels.addRecipe(this);
				break;
			// Gas Turbine
			case 1:
				GT_Recipe_Map.sTurbineFuels.addRecipe(this);
				break;
			// Thermal Generator
			case 2:
				GT_Recipe_Map.sHotFuels.addRecipe(this);
				break;
			// Plasma Generator
			case 4:
				GT_Recipe_Map.sPlasmaFuels.addRecipe(this);
				break;
			// Magic Generator
			case 5:
				GT_Recipe_Map.sMagicFuels.addRecipe(this);
				break;
			// Fluid Generator. Usually 3. Every wrong Type ends up in the Semifluid Generator
			default:
				GT_Recipe_Map.sDenseLiquidFuels.addRecipe(this);
				break;
			}
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, int aDuration, int aEUt, int aSpecialValue) {
		this(true, new ItemStack[] {aInput1, aInput2}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), aEUt, Math.max(Math.min(aSpecialValue, 160000000), 0));
		if (mInputs.length > 1) {
			GT_Recipe_Map.sFusionRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt) {
		this(true, new ItemStack[] {aInput1}, new ItemStack[] {aOutput1, aOutput2}, null, null, null, null, aDuration, aEUt, 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sLatheRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4) {
		this(true, aInput2==null?new ItemStack[] {aInput1}:new ItemStack[] {aInput1, aInput2}, new ItemStack[] {aOutput1, aOutput2, aOutput3, aOutput4}, null, null, null, null, 100*aInput1.stackSize, 120, 0);
		if (mInputs.length > 0 && aInput2 != null && mOutputs[0] != null) {
			GT_Recipe_Map.sGrinderRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, int aCellAmount, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, int aDuration, int aEUt) {
		this(true, new ItemStack[] {aInput1, aCellAmount>0?ItemList.Cell_Empty.get(Math.min(64, Math.max(1, aCellAmount))):null}, new ItemStack[] {aOutput1, aOutput2, aOutput3, aOutput4}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sDistillationRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt, int aLevel) {
		this(true, aInput2==null?new ItemStack[] {aInput1}:new ItemStack[] {aInput1, aInput2}, new ItemStack[] {aOutput1, aOutput2}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), aLevel > 0 ? aLevel : 100);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sBlastRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, int aInput2, ItemStack aOutput1, ItemStack aOutput2) {
		this(true, new ItemStack[] {aInput1, GT_ModHandler.getIC2Item("industrialTnt", aInput2>0?aInput2<64?aInput2:64:1, new ItemStack(Blocks.tnt, aInput2>0?aInput2<64?aInput2:64:1))}, new ItemStack[] {aOutput1, aOutput2}, null, null, null, null, 20, 30, 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sImplosionRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, int aEUt, int aDuration, ItemStack aOutput1) {
		this(true, new ItemStack[] {aInput1}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sWiremillRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(int aEUt, int aDuration, ItemStack aInput1, ItemStack aOutput1) {
		this(true, new ItemStack[] {aInput1, ItemList.Circuit_Integrated.getWithDamage(0, aInput1.stackSize)}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sBenderRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(int aEUt, int aDuration, ItemStack aInput1, ItemStack aShape, ItemStack aOutput1) {
		this(true, new ItemStack[] {aInput1, aShape}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 1 && mOutputs[0] != null) {
			GT_Recipe_Map.sExtruderRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aInput2, int aEUt, int aDuration, ItemStack aOutput1) {
		this(true, aInput2==null ? new ItemStack[] {aInput1} : new ItemStack[] {aInput1, aInput2}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sAlloySmelterRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, int aEUt, ItemStack aInput2, int aDuration, ItemStack aOutput1, ItemStack aOutput2) {
		this(true, aInput2==null?new ItemStack[] {aInput1}:new ItemStack[] {aInput1, aInput2}, new ItemStack[] {aOutput1, aOutput2}, null, null, null, null, Math.max(aDuration, 1), Math.max(aEUt, 1), 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sCannerRecipes.addRecipe(this);
		}
	}
	
	public GT_Recipe(ItemStack aInput1, ItemStack aOutput1, int aDuration) {
		this(true, new ItemStack[] {aInput1}, new ItemStack[] {aOutput1}, null, null, null, null, Math.max(aDuration, 1), 120, 0);
		if (mInputs.length > 0 && mOutputs[0] != null) {
			GT_Recipe_Map.sVacuumRecipes.addRecipe(this);
		}
	}
}