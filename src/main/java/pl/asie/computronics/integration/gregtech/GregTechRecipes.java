package pl.asie.computronics.integration.gregtech;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import li.cil.oc.api.detail.ItemInfo;
import mods.railcraft.common.items.ItemElectricMeter;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.ModRecipes;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.util.color.RecipeColorizer;

/**
 * @author Vexatos
 */
public class GregTechRecipes extends ModRecipes {

	@Override
	public void registerRecipes() {
		Computronics.log.info("Registering GregTech-style recipes for Computronics. Turn it off in the configs if you don't want it.");

		if(Computronics.camera != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.camera, 1, 0),
				"gct", "ges", "gct", 's', ItemList.Hull_LV.get(1), 'i', "plateIron", 'e', "lensRuby", 'g', "lensGlass", 'c', "circuitPrimitive", 't', "cableGt01Tin"));
		}
		if(Computronics.chatBox != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.chatBox, 1, 0),
				"ili", "rer", "tst", 's', ItemList.Hull_LV.get(1), 'i', "plateGlass", 'e', ItemList.Emitter_LV.get(1), 'r', "circuitBasic", 't', "cableGt01Tin", 'l', ItemList.Sensor_LV.get(1)));
		}
		if(Computronics.ironNote != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.ironNote, 1, 0),
				"iii", "ini", "iii", 'i', "plateIron", 'n', Blocks.noteblock));
		}
		if(Computronics.tapeReader != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.tapeReader, 1, 0),
				"tit", "mrm", "cac", 'i', ItemList.Hull_LV.get(1), 'r', "circuitBasic", 'a', Computronics.ironNote, 'm', ItemList.Electric_Motor_LV.get(1), 't', "cableGt01Tin", 'c', "plateIronMagnetic"));
		}
		if(Computronics.cipher != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.cipher, 1, 0),
				"isi", "trt", "ele", 'i', "cableGt01Copper", 'r', ItemList.Robot_Arm_MV.get(1), 'e', "circuitElite", 's', ItemList.Hull_MV.get(1), 'l', "plateSilicon", 't', "screwAluminium"));
		}
		if(Computronics.radar != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.radar, 1, 0),
				"ftf", "dbd", "lcl", 't', ItemList.Sensor_HV.get(1), 'b', ItemList.Emitter_HV.get(1), 'c', ItemList.Hull_HV.get(1), 'f', "circuitMaster", 'd', "circuitElite", 'l', "cableGt02Gold"));
		}
		if(Computronics.nc_eepromreader != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.nc_eepromreader, 1, 0),
				"ntn", "cec", "nhn", 'e', GameRegistry.findItem(Mods.NedoComputers, "EEPROM"), 'c', "circuitBasic", 't', "cableGt01Tin", 'h', ItemList.Hull_LV.get(1), 'n', "circuitPrimitive"));
		}
		if(Computronics.colorfulLamp != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.colorfulLamp, 1, 0),
				"igi", "glg", "ini", 'i', "plateIron", 'g', "plateGlass", 'l', Blocks.redstone_lamp, 'n', "circuitPrimitive"));
		}
		if(!(Loader.isModLoaded(Mods.OpenComputers) && !Config.NON_OC_RECIPES && registerOCRecipes())) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "ece", "gig", 'g', "screwStainlessSteel",
				'c', Computronics.cipher != null ? Computronics.cipher : ItemList.Robot_Arm_HV.get(1), 'e', "wireGt01Gold", 'i', "circuitMaster",
				'd', Computronics.cipher != null ? ItemList.Robot_Arm_HV.get(1) : "plateDiamond"));
		}
		if(Loader.isModLoaded(Mods.Railcraft) && Computronics.railcraft != null) {
			registerRailcraftRecipes();
		}
		if(Computronics.itemTape != null) {
			// Tape recipes
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				"sis", "ipi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'p', "plateOlivine", 's', "screwIron"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				"sis", "ipi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'p', "plateEmerald", 's', "screwIron"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				"sis", "ngn", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'n', "plateElectrum", 'g', "plateOlivine", 's', "screwIron"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				"sis", "ngn", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'n', "plateElectrum", 'g', "plateEmerald", 's', "screwIron"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 2),
				"sis", "idi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateElectrum", 's', "screwSteel", 'd', "circuitData"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 3),
				"sps", "pep", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwStainlessSteel", 'p', "plateDiamond", 'e', "circuitElite"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 4),
				"dcd", "ncn", "dTd", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'd', ItemList.Duct_Tape.get(1), 'c', "circuitElite", 'n', "plateNetherStar"));

			//GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 8),
			//	" n ", "nnn", " T ", 'T', new ItemStack(itemParts, 1, 0), 'n', Items.nether_star));

			// Mod compat - copper/steel
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'p', "plateCopper", 's', "screwCopper", 'o', "dustOlivine"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'p', "plateCopper", 's', "screwCopper", 'o', "dustEmerald"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwIron", 'p', "plateSteel", 'o', "plateOlivine"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwIron", 'p', "plateSteel", 'o', "plateEmerald"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 7),
				"cic", "isi", "cTc", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIridium", 's', "plateTungstenSteel", 'c', "screwTungstenSteel"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				"iii", "iei", "eoe", 'e', "foilElectrum", 'i', "foilIron", 'o', "dustOlivine"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				"iii", "iei", "eoe", 'e', "foilElectrum", 'i', "foilIron", 'o', "dustEmerald"));
			GameRegistry.addRecipe(new RecipeColorizer(Computronics.itemTape));
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected boolean registerOCRecipes() {
		ItemInfo cpu = li.cil.oc.api.Items.get("cpu2");
		ItemInfo chip = li.cil.oc.api.Items.get("chip2");
		ItemInfo capacitor = li.cil.oc.api.Items.get("capacitor");
		if(cpu != null && chip != null && capacitor != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "ece", "gig",
				'g', "screwStainlessSteel",
				'c', Computronics.cipher != null ? Computronics.cipher : cpu.createItemStack(1),
				'e', chip.createItemStack(1),
				'i', capacitor.block(),
				'd', Computronics.cipher != null ? cpu.createItemStack(1) : ItemList.Robot_Arm_HV.get(1)));
			return true;
		}
		Computronics.log.warn("An error happened during registering OpenComputers-style recipes, falling back to default ones");
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.Railcraft)
	protected void registerRailcraftRecipes() {
		if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
				"srm", "lhe", "gcg", 's', ItemList.Sensor_LV.get(1), 'm', ItemList.Emitter_LV.get(1), 'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1), 'l', "cableGt01Tin", 'e', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1), 'c', ItemElectricMeter.getItem(), 'h', ItemList.Hull_LV.get(1), 'g', new ItemStack(RailcraftItem.rail.item(), 1, ItemRail.EnumRail.ELECTRIC.ordinal())));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
				" r ", "rpn", " nc", 'p', ItemList.Emitter_LV.get(1), 'n', "ringRedAlloy", 'r', "cableGt01Tin", 'c', "circuitBasic"));
		}
		if(Computronics.railcraft.digitalBox != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.digitalBox, 1, 0),
				"iri", "ibi", "isi", 'i', "plateIron", 'r', ItemList.Sensor_LV.get(1), 'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.receiver", 1), 's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1)));
		}
		if(Computronics.railcraft.detector != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.detector, 1, 0),
				"bbb", "bdp", "bbb", 'b', "plateSteel", 'p', "cableGt02Gold", 'd', GameRegistry.findItemStack(Mods.Railcraft, "detector.advanced", 1)));
		}
	}

	public static void regsiterGregTechTapeRecipes() {

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 9),
			"psp", "tct", "prp", 'o', "dustOlivine", 'r', new ItemStack(Computronics.itemParts, 1, 0), 's', ItemList.Duct_Tape.get(1), 't', new ItemStack(Computronics.itemPartsGreg, 1, 0), 'p', "plateTungstenSteel", 'c', "circuitUltimate"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemPartsGreg, 1, 0),
			"srs", "fff", "hch", 's', "foilStainlessSteel", 'f', "foilChromiumDioxide", 'c', "craftingToolWireCutter", 'r', "ringNiobiumTitanium", 'h', "cellArgon"));

		//ChromiumDioxide(255, Textures.SET_DULL, 11.0F, 256, 3, 1 | 2, 230, 200, 200, 0, "ChromiumDioxide", 0, 0, 0, 0, 375, 0, false, false, 1, 1, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(Materials.Chrome, 1), new MaterialStack(Materials.Oxygen, 2)), Arrays.asList(new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 1)));

		GT_Mod.gregtechproxy.addFluid("Argon", "Argon", Materials.Argon,
			2, 295, GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Argon, 1L), GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Empty, 1L), 1000);

		//Nope.
		Materials.ChromiumDioxide.mBlastFurnaceRequired = false;
		Materials.ChromiumDioxide.mBlastFurnaceTemp = 650;
		Materials.ChromiumDioxide.mMeltingPoint = 650;

		GregTech_API.sRecipeAdder.addDistilleryRecipe(ItemList.Circuit_Integrated.getWithDamage(0L, 1L), Materials.Air.getGas(1000L), Materials.Nitrogen.getGas(780L), 1600, 32, false);
		GregTech_API.sRecipeAdder.addDistilleryRecipe(ItemList.Circuit_Integrated.getWithDamage(0L, 2L), Materials.Air.getGas(1000L), Materials.Oxygen.getGas(210L), 1600, 128, false);
		GregTech_API.sRecipeAdder.addDistilleryRecipe(ItemList.Circuit_Integrated.getWithDamage(0L, 3L), Materials.Air.getGas(1000L), Materials.Argon.getGas(5L), 6000, 512, false);

		GregTech_API.sRecipeAdder.addElectrolyzerRecipe(ItemList.Cell_Air.get(1), null, null, Materials.Air.getGas(2000L), ItemList.Cell_Empty.get(1),
			null, null, null, null, null, null, 800, 30);

		GregTech_API.sRecipeAdder.addChemicalRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1),
			null, Materials.Oxygen.getGas(5000), null, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.ChromiumDioxide, 1), 800);
	}
}
