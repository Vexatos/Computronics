package pl.asie.computronics.integration.gregtech.gregtech6;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.data.CS;
import gregapi.data.IL;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.recipes.Recipe;
import gregapi.util.OM;
import mods.railcraft.common.items.ItemElectricMeter;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.ModRecipes;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;
import pl.asie.lib.util.color.RecipeColorizer;

import static gregapi.data.CS.T;

/**
 * @author Vexatos
 */
public class GregTech6Recipes extends ModRecipes {

	@Override
	public void registerRecipes() {
		Computronics.log.info("Registering GregTech-6-style recipes for Computronics. Turn it off in the configs if you don't want them.");

		if(Computronics.camera != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.camera, 1, 0),
				"tcg", "seg", "tcg", 's', OP.casingMachine.dat(MT.Aluminium).toString(), 'i', "plateIron", 'e', "lensRuby", 'g', "lensGlass", 'c', "circuitBasic", 't', "cableGt01Tin");
		}
		if(Computronics.chatBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.chatBox, 1, 0),
				"ili", "rer", "tst", 's', OP.casingMachine.dat(MT.Aluminium).toString(), 'i', "plateGemGlass", 'e', IL.Emitter_LV.get(1), 'r', "circuitBasic", 't', "cableGt01Tin", 'l', IL.Sensor_LV.get(1));
		}
		if(Computronics.ironNote != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.ironNote, 1, 0),
				"iii", "ini", "iii", 'i', "plateIron", 'n', Blocks.noteblock);
		}
		if(Computronics.audioCable != null) {
			Recipe.RecipeMap.sBathRecipes.addRecipe2(T, 0, 64, OP.wireGt02.mat(MT.Silver, 1), OP.plateDouble.mat(MT.Paper, 1), MT.Bismuth.liquid(CS.U, true), null, new ItemStack(Computronics.audioCable, 1, 0));
			Recipe.RecipeMap.sBathRecipes.addRecipe2(T, 0, 64, OP.wireGt02.mat(MT.Silver, 1), OP.plateDouble.mat(MT.Paper, 1), MT.Lead.liquid(CS.U, true), null, new ItemStack(Computronics.audioCable, 1, 0));
			Recipe.RecipeMap.sAssemblerRecipes.addRecipe2(T, 30, 64, OP.wireGt02.mat(MT.Silver, 1), OP.plateDouble.mat(MT.Paper, 1), MT.Bismuth.liquid(CS.U, true), null, new ItemStack(Computronics.audioCable, 1, 0));
			Recipe.RecipeMap.sAssemblerRecipes.addRecipe2(T, 30, 64, OP.wireGt02.mat(MT.Silver, 1), OP.plateDouble.mat(MT.Paper, 1), MT.Lead.liquid(CS.U, true), null, new ItemStack(Computronics.audioCable, 1, 0));
		}
		if(Computronics.speaker != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.speaker, 1, 0),
				"wpc", "ifb", "shc", 'h', OP.casingMachine.dat(MT.Aluminium).toString(),
				'f', "foilAluminium", 'p', "plateSteel",
				'b', "plateDoublePaper", 'i', "plateSteelMagnetic",
				's', Computronics.audioCable != null ? new ItemStack(Computronics.audioCable, 1, 0) : "cableGt02Silver",
				'c', "screwSteel", 'w', "craftingToolScrewdriver");
		}
		if(Computronics.speechBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.speechBox, 1, 0),
				"crc", "mim", "tat", 'i', OP.casingMachine.dat(MT.StainlessSteel).toString(), 'r', "circuitElite",
				'a', Computronics.speaker != null ? Computronics.speaker : Computronics.ironNote != null ? Computronics.ironNote : Blocks.noteblock,
				'm', "plateGemSilicon", 't', "wireGt02Gold", 'c', "screwAluminium"
			);
		}
		if(Computronics.tapeReader != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.tapeReader, 1, 0),
				"trt", "mim", "cac", 'i', OP.casingMachine.dat(MT.Aluminium).toString(), 'r', "circuitBasic",
				'a', Computronics.ironNote != null ? Computronics.ironNote : Blocks.noteblock,
				'm', IL.Electric_Motor_LV.get(1), 't', "cableGt01Tin", 'c', "plateIronMagnetic");
		}
		if(Computronics.cipher != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher, 1, 0),
				"isi", "trt", "ele", 'i', "cableGt01Copper", 'r', IL.Robot_Arm_MV.get(1), 'e', "circuitElite", 's', OP.casingMachine.dat(MT.SteelGalvanized).toString(), 'l', "plateGemSilicon", 't', "screwAluminium");
		}
		if(Computronics.radar != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.radar, 1, 0),
				"ftf", "dbd", "lcl", 't', IL.Sensor_HV.get(1), 'b', IL.Emitter_HV.get(1), 'c', OP.casingMachine.dat(MT.StainlessSteel).toString(), 'f', "circuitMaster", 'd', "circuitElite", 'l', "cableGt02Gold");
		}
		if(Computronics.colorfulLamp != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.colorfulLamp, 1, 0),
				"igi", "glg", "ini", 'i', "plateIron", 'g', "plateGemGlass", 'l', Blocks.redstone_lamp, 'n', "circuitBasic");
		}
		if(!(Mods.isLoaded(Mods.OpenComputers) && !Config.NON_OC_RECIPES && registerOCRecipes())) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "ece", "gig", 'g', "screwStainlessSteel",
				'c', Computronics.cipher != null ? Computronics.cipher : IL.Robot_Arm_HV.get(1), 'e', "wireGt01Gold", 'i', "circuitMaster",
				'd', Computronics.cipher != null ? IL.Robot_Arm_HV.get(1) : "plateGemDiamond");
		}
		if(Mods.isLoaded(Mods.Railcraft) && Computronics.railcraft != null) {
			registerRailcraftRecipes();
		}
		if(Computronics.itemTape != null) {
			// Tape recipes
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				"sis", "ipi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'p', "plateGemOlivine", 's', "screwIron");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				"sis", "ipi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'p', "plateGemEmerald", 's', "screwIron");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				"sis", "ngn", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'n', "plateElectrum", 'g', "plateGemOlivine", 's', "screwIron");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				"sis", "ngn", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIron", 'n', "plateElectrum", 'g', "plateGemEmerald", 's', "screwIron");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 2),
				"sis", "idi", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateElectrum", 's', "screwSteelGalvanized", 'd', "circuitAdvanced");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 3),
				"sps", "pep", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwStainlessSteel", 'p', "plateGemDiamond", 'e', "circuitElite");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 4),
				"dnd", "ncn", "dTd", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'd',
				IL.Duct_Tape.exists() ? IL.Duct_Tape.get(1) : "bottleGlue", 'c', IL.Processor_Crystal_Diamond.get(1), 'n', "plateGemNetherStar");

			//RecipeUtils.addShapedRecipe(new ItemStack(itemTape, 1, 8),
			//	" n ", "nnn", " T ", 'T', new ItemStack(itemParts, 1, 0), 'n', Items.nether_star));

			// Mod compat - copper/steel
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'p', "plateCopper", 's', "screwCopper", 'o', "plateGemOlivine");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'p', "plateCopper", 's', "screwCopper", 'o', "plateGemEmerald");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwIron", 'p', "plateSteel", 'o', "plateGemOlivine");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				"sps", "pop", "sTs", 'T', new ItemStack(Computronics.itemParts, 1, 0), 's', "screwIron", 'p', "plateSteel", 'o', "plateGemEmerald");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 7),
				"cic", "isi", "cTc", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIridium", 's', "plateTungstenSteel", 'c', "screwTungstenSteel");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				"iwi", "ese", "ioi", 'e', "foilElectrum", 'i', "plateTinySteelGalvanized", 'o', "dustOlivine", 's', "springSmallSteelGalvanized", 'w', "wireFineSteelGalvanized");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				"iwi", "ese", "ioi", 'e', "foilElectrum", 'i', "plateTinySteelGalvanized", 'o', "dustEmerald", 's', "springSmallSteelGalvanized", 'w', "wireFineSteelGalvanized");
			GameRegistry.addRecipe(new RecipeColorizer(Computronics.itemTape));
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected boolean registerOCRecipes() {
		RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
			"gdg", "ece", "gig",
			'g', "screwStainlessSteel",
			'c', Computronics.cipher != null ? Computronics.cipher : "oc:cpu2",
			'e', "oc:circuitChip2",
			'i', "oc:capacitor",
			'd', Computronics.cipher != null ? "oc:cpu2" : IL.Robot_Arm_HV.get(1));
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.Railcraft)
	protected void registerRailcraftRecipes() {
		if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
				"srm", "lhe", "gcg",
				's', IL.Sensor_LV.get(1),
				'm', IL.Emitter_LV.get(1),
				'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1),
				'l', "cableGt01Tin",
				'e', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1),
				'c', ItemElectricMeter.getItem(),
				'h', OP.casingMachine.dat(MT.Aluminium).toString(),
				'g', new ItemStack(RailcraftItem.rail.item(), 1, ItemRail.EnumRail.ELECTRIC.ordinal()));

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
				" r ", "rpn", " nc", 'p', IL.Emitter_LV.get(1), 'n', "plateTinyRedstoneAlloy", 'r', "wireGt01Tin", 'c', "circuitBasic");
		}
		if(Computronics.railcraft.digitalReceiverBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalReceiverBox, 1, 0),
				"iri", "ibi", "isi", 'i', "plateIron", 'r', IL.Sensor_LV.get(1), 'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.receiver", 1), 's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1));
		}
		if(Computronics.railcraft.digitalControllerBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalControllerBox, 1, 0),
				"iri", "ibi", "isi", 'i', "plateIron", 'r', IL.Sensor_LV.get(1), 'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.controller", 1), 's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1));
		}
		if(Computronics.railcraft.detector != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.detector, 1, 0),
				"bbb", "bdp", "bbb", 'b', "plateSteel", 'p', "cableGt02Gold", 'd', GameRegistry.findItemStack(Mods.Railcraft, "detector.advanced", 1));
		}
		if(Computronics.railcraft.ticketMachine != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.ticketMachine, 1, 0),
				"trt", "shg", "tpt",
				'h', OP.casingMachine.dat(MT.Aluminium).toString(),
				'r', IL.Electric_Motor_LV.get(1),
				't', "plateSteel",
				's', "circuitGood",
				'p', IL.Electric_Piston_LV.get(1),
				'g', IL.Cover_Screen.exists() ? IL.Cover_Screen.get(1) : "plateGemGlass");
		}
	}

	public static void registerStandardGregTechRecipes() {

		RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 9),
			"psp", "tct", "prp", 'r', new ItemStack(Computronics.itemParts, 1, 0), 's', IL.Duct_Tape.exists() ? IL.Duct_Tape.get(1) :
				"craftingToolHardHammer", 't', new ItemStack(Computronics.itemPartsGreg, 1, 0), 'p', "plateQuadrupleTungstenSteel", 'c', IL.Processor_Crystal_Sapphire.get(1));

		RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemPartsGreg, 1, 0),
			"srs", "fff", "hch", 's', "foilStainlessSteel", 'f', "foilChromiumDioxide", 'c', "craftingToolPincers", 'r', "ringOsmiridium", 'h', "cellArgon");

		Recipe.RecipeMap.sCannerRecipes.addRecipe2(true, 16, 16, null, IL.Cell_Empty.get(1), MT.Argon.fluid(CS.U, true), null, OM.get(OP.cell, MT.Argon, 1));
	}
}
