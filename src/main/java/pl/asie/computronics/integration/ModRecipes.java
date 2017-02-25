package pl.asie.computronics.integration;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.items.ItemElectricMeter;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
				"sss", "geg", "iii", 's', Blocks.stonebrick, 'i', "ingotIron", 'e', Items.ender_pearl, 'g', "blockGlassColorless");
		}
		if(Computronics.chatBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.chatBox, 1, 0),
				"sss", "ses", "iri", 's', Blocks.stonebrick, 'i', "ingotIron", 'e', Items.ender_pearl, 'r', "dustRedstone");
		}
		if(Computronics.ironNote != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.ironNote, 1, 0),
				"iii", "ini", "iii", 'i', "ingotIron", 'n', Blocks.noteblock);
		}
		if(Computronics.audioCable != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.audioCable, 8, 0),
				"ini", 'i', "ingotIron", 'n', Blocks.noteblock);
		}
		if(Computronics.speaker != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.speaker, 1, 0),
				"sIs", "ini", "sIs", 's', Blocks.stonebrick, 'I', "ingotIron", 'i', Blocks.iron_bars, 'n', Blocks.noteblock);
		}
		if(Computronics.tapeReader != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.tapeReader, 1, 0),
				"iii", "iri", "iai", 'i', "ingotIron", 'r', "dustRedstone",
				'a', Computronics.ironNote != null ? Computronics.ironNote : Blocks.noteblock);
		}
		if(Computronics.cipher != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher, 1, 0),
				"sss", "srs", "eie", 'i', "ingotIron", 'r', "dustRedstone", 'e', Items.ender_pearl, 's', Blocks.stonebrick);
		}
		if(Computronics.radar != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.radar, 1, 0),
				"sts", "rbr", "scs", 'i', "ingotIron", 'r', "dustRedstone", 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'b', Items.bowl, 'c', Items.comparator);
		}
		if(Computronics.colorfulLamp != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.colorfulLamp, 1, 0),
				"igi", "glg", "igi", 'i', "ingotIron", 'g', "blockGlassColorless", 'l', "dustGlowstone");
		}
		if(!(Mods.isLoaded(Mods.OpenComputers) && !Config.NON_OC_RECIPES && registerOCRecipes())) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.cipher_advanced, 1, 0),
				"gdg", "gcg", "eie", 'g', "ingotGold",
				'c', Computronics.cipher != null ? Computronics.cipher : "gemDiamond", 'e', Items.ender_pearl, 'i', "ingotIron",
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
				" d ", "dnd", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', Items.nether_star, 'd', "gemDiamond");
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.itemTape, 1, 8),
				" n ", "nnn", " T ", 'T', new ItemStack(Computronics.itemParts, 1, 0), 'n', Items.nether_star);

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
		Item item = GameRegistry.findItem(Mods.Railcraft, "part.plate");
		if(Computronics.railcraft.locomotiveRelay != null && Computronics.railcraft.relaySensor != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.locomotiveRelay, 1, 0),
				"srs", "geg", "scs",
				's', item != null ? new ItemStack(item, 1, 2) : Blocks.stonebrick,
				'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1),
				'e', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1),
				'c', ItemElectricMeter.getItem(),
				'g', new ItemStack(RailcraftItem.rail.item(), 1, ItemRail.EnumRail.ELECTRIC.ordinal()));

			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.relaySensor, 1, 0),
				" n ", "npr", " r ", 'p', Items.paper, 'n', "nuggetTin", 'r', "dustRedstone");
		}
		if(Computronics.railcraft.digitalReceiverBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalReceiverBox, 1, 0),
				"iri", "ibi", "isi", 'i', "ingotIron",
				'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.receiver", 1),
				'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.receiver", 1),
				's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1));
		}
		if(Computronics.railcraft.digitalControllerBox != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.digitalControllerBox, 1, 0),
				"iri", "ibi", "isi", 'i', "ingotIron",
				'r', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.controller", 1),
				'b', GameRegistry.findItemStack(Mods.Railcraft, "signal.box.controller", 1),
				's', GameRegistry.findItemStack(Mods.Railcraft, "part.circuit.signal", 1));
		}
		if(Computronics.railcraft.detector != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.detector, 1, 0),
				"sss", "sdp", "sss",
				's', item != null ? new ItemStack(item, 1, 1) : "ingotSteel",
				'p', Blocks.light_weighted_pressure_plate,
				'd', GameRegistry.findItemStack(Mods.Railcraft, "detector.advanced", 1));
		}
		if(Computronics.railcraft.ticketMachine != null) {
			RecipeUtils.addShapedRecipe(new ItemStack(Computronics.railcraft.ticketMachine, 1, 0),
				"tst", "sdg", "tpt",
				'd', Blocks.dispenser,
				't', item != null ? new ItemStack(item, 1, 2) : Blocks.stonebrick,
				's', item != null ? new ItemStack(item, 1, 1) : "ingotIron",
				'p', Blocks.piston,
				'g', "paneGlassColorless");
		}
	}
}
