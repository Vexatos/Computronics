package pl.asie.computronics.integration;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import mods.railcraft.common.items.ItemElectricMeter;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.util.color.RecipeColorizer;

/**
 * @author Vexatos
 */
public class ModRecipes {

	public static class GregTechRecipes {
		public static void registerGregTechRecipes() {
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
			if(Loader.isModLoaded(Mods.OpenComputers) && !Computronics.NON_OC_RECIPES) {
				registerGregTechOCRecipes();
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
					"gdg", "ece", "gig", 'g', "screwStainlessSteel",
					'c', Computronics.cipher != null ? Computronics.cipher : ItemList.Robot_Arm_HV.get(1), 'e', "wireGt01Gold", 'i', "circuitMaster",
					'd', Computronics.cipher != null ? ItemList.Robot_Arm_HV.get(1) : "plateDiamond"));
			}
			if(Loader.isModLoaded(Mods.Railcraft) && Computronics.railcraft != null) {
				registerGregTechRailcraftRecipes();
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

		@Optional.Method(modid = Mods.OpenComputers)
		private static void registerGregTechOCRecipes() {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "ece", "gig",
				'g', "screwStainlessSteel",
				'c', Computronics.cipher != null ? Computronics.cipher : li.cil.oc.api.Items.get("cpu2").createItemStack(1),
				'e', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'i', li.cil.oc.api.Items.get("capacitor").block(),
				'd', Computronics.cipher != null ? li.cil.oc.api.Items.get("cpu2").createItemStack(1) : ItemList.Robot_Arm_HV.get(1)));
		}

		@Optional.Method(modid = Mods.Railcraft)
		private static void registerGregTechRailcraftRecipes() {
			if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
					"srm", "lhe", "gcg", 's', ItemList.Sensor_LV.get(1), 'm', ItemList.Emitter_LV.get(1), 'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1), 'l', "cableGt01Tin", 'e', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1), 'c', ItemElectricMeter.getItem(), 'h', ItemList.Hull_LV.get(1), 'g', new ItemStack(RailcraftItem.rail.item(), 1, ItemRail.EnumRail.ELECTRIC.ordinal())));

				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
					" r ", "rpn", " nc", 'p', ItemList.Emitter_LV.get(1), 'n', "ringRedAlloy", 'r', "cableGt01Tin", 'c', "circuitBoardBasic"));
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

	public static void registerRecipes() {
		if(Computronics.camera != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.camera, 1, 0),
				"sss", "geg", "iii", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'g', Blocks.glass);
		}
		if(Computronics.chatBox != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.chatBox, 1, 0),
				"sss", "ses", "iri", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'r', Items.redstone);
		}
		if(Computronics.ironNote != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.ironNote, 1, 0),
				"iii", "ini", "iii", 'i', Items.iron_ingot, 'n', Blocks.noteblock);
		}
		if(Computronics.tapeReader != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.tapeReader, 1, 0),
				"iii", "iri", "iai", 'i', Items.iron_ingot, 'r', Items.redstone, 'a', Computronics.ironNote);
		}
		if(Computronics.cipher != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.cipher, 1, 0),
				"sss", "srs", "eie", 'i', Items.iron_ingot, 'r', Items.redstone, 'e', Items.ender_pearl, 's', Blocks.stonebrick);
		}
		if(Computronics.radar != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.radar, 1, 0),
				"sts", "rbr", "scs", 'i', Items.iron_ingot, 'r', Items.redstone, 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'b', Items.bowl, 'c', Items.comparator);
		}
		if(Computronics.nc_eepromreader != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.nc_eepromreader, 1, 0),
				"sts", "iei", "srs", 'i', Items.iron_ingot, 'r', Items.redstone, 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'e', GameRegistry.findItem(Mods.NedoComputers, "EEPROM"));
		}
		if(Computronics.colorfulLamp != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.colorfulLamp, 1, 0),
				"igi", "glg", "igi", 'i', Items.iron_ingot, 'g', Blocks.glass, 'l', Items.glowstone_dust);
		}
		if(Loader.isModLoaded(Mods.OpenComputers) && !Computronics.NON_OC_RECIPES) {
			registerOCRecipes();
		} else {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "gcg", "eie", 'g', Items.gold_ingot,
				'c', Computronics.cipher != null ? Computronics.cipher : Items.diamond, 'e', Items.ender_pearl, 'i', Items.iron_ingot,
				'd', Computronics.cipher != null ? Items.diamond : Items.gold_ingot);
		}
		if(Loader.isModLoaded(Mods.Railcraft) && Computronics.railcraft != null) {
			registerRailcraftRecipes();
		}
		if(Computronics.itemTape != null) {
			// Tape recipes
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				" i ", "iii", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				" i ", "ngn", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot, 'n', Items.gold_nugget, 'g', Items.gold_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 2),
				" i ", "ggg", "nTn", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot, 'n', Items.gold_nugget, 'g', Items.gold_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 3),
				" i ", "ddd", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot, 'd', Items.diamond));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 4),
				" d ", "dnd", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', Items.nether_star, 'd', Items.diamond));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 8),
				" n ", "nnn", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', Items.nether_star));

			// Mod compat - copper/steel
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				" i ", " c ", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot, 'c', "ingotCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				" i ", "isi", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', Items.iron_ingot, 's', "ingotSteel"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemTape, 1, 7),
				" i ", "isi", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIridium", 's', "plateTungstenSteel"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				" i ", "rrr", "iii", 'r', Items.redstone, 'i', Items.iron_ingot));
			GameRegistry.addRecipe(new RecipeColorizer(Computronics.itemTape));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private static void registerOCRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
			"gdg", "mcm", "gbg",
			'g', Items.gold_ingot,
			'd', Computronics.cipher != null ? li.cil.oc.api.Items.get("cpu2").createItemStack(1) : Items.diamond,
			'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
			'c', Computronics.cipher != null ? Computronics.cipher : li.cil.oc.api.Items.get("cpu2").createItemStack(1),
			'b', li.cil.oc.api.Items.get("capacitor").block());
	}

	@Optional.Method(modid = Mods.Railcraft)
	private static void registerRailcraftRecipes() {
		if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
			GameRegistry.addShapedRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
				"srs", "geg", "scs", 's', Blocks.stonebrick, 'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1), 'e', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1), 'c', ItemElectricMeter.getItem(), 'g', new ItemStack(RailcraftItem.rail.item(), 1, ItemRail.EnumRail.ELECTRIC.ordinal()));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
				" n ", "npr", " r ", 'p', Items.paper, 'n', "nuggetTin", 'r', Items.redstone));
		}
		if(Computronics.railcraft.digitalBox != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.digitalBox, 1, 0),
				"iri", "ibi", "isi", 'i', "ingotIron", 'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1), 'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.receiver", 1), 's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1)));
		}
		if(Computronics.railcraft.detector != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Computronics.railcraft.detector, 1, 0),
				"bbb", "bdp", "bbb", 'b', "ingotIron", 'p', Blocks.light_weighted_pressure_plate, 'd', GameRegistry.findItemStack(Mods.Railcraft, "detector.advanced", 1)));
		}
	}
}
