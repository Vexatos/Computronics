package pl.asie.computronics.integration;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.SignalBoxVariant;
import mods.railcraft.common.items.ItemCircuit.EnumCircuit;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;
import pl.asie.lib.util.color.RecipeColorizer;
import pl.asie.lib.util.color.RecipeDecolorizer;

/**
 * @author Vexatos
 */
public class ModRecipes {

	public static ModRecipes instance;

	public void registerRecipes() {
		if(Computronics.camera != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.camera, 1, 0),
				"sss", "geg", "iii", 's', Blocks.STONEBRICK, 'i', "ingotIron", 'e', "enderpearl", 'g', "blockGlassColorless");
		}
		if(Computronics.chatBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.chatBox, 1, 0),
				"sss", "ses", "iri", 's', Blocks.STONEBRICK, 'i', "ingotIron", 'e', "enderpearl", 'r', "dustRedstone");
		}
		if(Computronics.ironNote != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.ironNote, 1, 0),
				"iii", "ini", "iii", 'i', "ingotIron", 'n', Blocks.NOTEBLOCK);
		}
		if(Computronics.audioCable != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.audioCable, 8, 0),
				"ini", 'i', "ingotIron", 'n', Blocks.NOTEBLOCK);
		}
		if(Computronics.speaker != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.speaker, 1, 0),
				"sIs", "ini", "sIs", 's', Blocks.STONEBRICK, 'I', "ingotIron", 'i', Blocks.IRON_BARS, 'n', Blocks.NOTEBLOCK);
		}
		if(Computronics.tapeReader != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.tapeReader, 1, 0),
				"iii", "iri", "iai", 'i', "ingotIron", 'r', "dustRedstone",
				'a', Computronics.ironNote != null ? Computronics.ironNote : Blocks.NOTEBLOCK);
		}
		if(Computronics.cipher != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher, 1, 0),
				"sss", "srs", "eie", 'i', "ingotIron", 'r', "dustRedstone", 'e', "enderpearl", 's', Blocks.STONEBRICK);
		}
		if(Computronics.radar != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.radar, 1, 0),
				"sts", "rbr", "scs", 'i', "ingotIron", 'r', "dustRedstone", 't', Blocks.REDSTONE_TORCH, 's', Blocks.STONEBRICK, 'b', Items.BOWL, 'c', Items.COMPARATOR);
		}
		if(Computronics.colorfulLamp != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.colorfulLamp, 1, 0),
				"igi", "glg", "igi", 'i', "ingotIron", 'g', "blockGlassColorless", 'l', "dustGlowstone");
		}
		if(!(Mods.isLoaded(Mods.OpenComputers) && !Config.NON_OC_RECIPES && registerOCRecipes())) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "gcg", "eie", 'g', "ingotGold",
				'c', Computronics.cipher != null ? Computronics.cipher : "gemDiamond", 'e', "enderpearl", 'i', "ingotIron",
				'd', Computronics.cipher != null ? "gemDiamond" : "ingotGold");
		}
		if(Mods.isLoaded(Mods.Railcraft) && Computronics.railcraft != null) {
			registerRailcraftRecipes();
		}
		if(Computronics.itemTape != null) {
			// Tape recipes
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 0),
				" i ", "iii", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 1),
				" i ", "ngn", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron", 'n', "nuggetGold", 'g', "ingotGold");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 2),
				" i ", "ggg", "nTn", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron", 'n', "nuggetGold", 'g', "ingotGold");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 3),
				" i ", "ddd", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron", 'd', "gemDiamond");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 4),
				" d ", "dnd", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', "netherStar", 'd', "gemDiamond");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 8),
				" n ", "nnn", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', "netherStar");

			// Mod compat - copper/steel
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 5),
				" i ", " c ", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron", 'c', "ingotCopper");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 6),
				" i ", "isi", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "ingotIron", 's', "ingotSteel");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 7),
				" i ", "isi", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'i', "plateIridium", 's', "plateTungstenSteel");

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemParts, 1, 0),
				" i ", "rrr", "iii", 'r', "dustRedstone", 'i', "ingotIron");
			GameRegistry.addRecipe(new RecipeColorizer(Computronics.itemTape));
			GameRegistry.addRecipe(new RecipeDecolorizer(Computronics.itemTape));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected boolean registerOCRecipes() {
		RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
			"gdg", "mcm", "gbg",
			'g', "ingotGold",
			'd', Computronics.cipher != null ? "oc:cpu2" : "gemDiamond",
			'm', "oc:circuitChip2",
			'c', Computronics.cipher != null ? Computronics.cipher : "oc:cpu2",
			'b', "oc:capacitor");
		return true;
	}

	@Optional.Method(modid = Mods.Railcraft)
	protected void registerRailcraftRecipes() {
		if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
				"srs", "geg", "scs",
				's', RailcraftItems.PLATE.getStack(Metal.TIN),
				'r', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.RECEIVER),
				'e', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.CONTROLLER),
				'c', RailcraftItems.CHARGE_METER.getRecipeObject(),
				'g', RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ELECTRIC));

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
				" n ", "npr", " r ", 'p', "paper", 'n', "nuggetTin", 'r', "dustRedstone");
		}
		if(Computronics.railcraft.digitalReceiverBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalReceiverBox, 1, 0),
				"iri", "ibi", "isi", 'i', "ingotIron",
				'r', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.RECEIVER),
				'b', RailcraftBlocks.SIGNAL_BOX.getRecipeObject(SignalBoxVariant.RECEIVER),
				's', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.SIGNAL));
		}
		if(Computronics.railcraft.digitalControllerBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalControllerBox, 1, 0),
				"iri", "ibi", "isi", 'i', "ingotIron",
				'r', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.CONTROLLER),
				'b', RailcraftBlocks.SIGNAL_BOX.getRecipeObject(SignalBoxVariant.CONTROLLER),
				's', RailcraftItems.CIRCUIT.getRecipeObject(EnumCircuit.SIGNAL));
		}
		if(Computronics.railcraft.detector != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.detector, 1, 0),
				"sss", "sdp", "sss",
				's', RailcraftItems.PLATE.getStack(Metal.STEEL),
				'p', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
				'd', RailcraftBlocks.DETECTOR.getRecipeObject(EnumDetector.ADVANCED));
		}
		if(Computronics.railcraft.ticketMachine != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.ticketMachine, 1, 0),
				"tst", "sdg", "tpt",
				'd', Blocks.DISPENSER,
				't', RailcraftItems.PLATE.getStack(Metal.TIN),
				's', RailcraftItems.PLATE.getStack(Metal.STEEL),
				'p', Blocks.PISTON,
				'g', "paneGlassColorless");
		}
	}
}
