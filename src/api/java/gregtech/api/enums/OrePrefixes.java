package gregtech.api.enums;

import gregtech.api.GregTech_API;
import gregtech.api.enums.TC_Aspects.TC_AspectStack;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.objects.MaterialStack;
import gregtech.api.objects.OrePrefixMaterialData;
import gregtech.api.util.GT_Utility;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public enum OrePrefixes {
	@Deprecated pulp		(""								, ""								, false, false, false, false, false,									-1),
	@Deprecated leaves		(""								, ""								, false, false, false, false, false,									-1),
	@Deprecated sapling		(""								, ""								, false, false, false, false, false,									-1),
	@Deprecated itemDust	(""								, ""								, false, false, false, false, false,									-1),
	
	oreBlackgranite			("Granite "						, " Ore"							,  true,  true, false, false, false,									-1), // In case of an End-Ores Mod. Ore -> Material is a Oneway Operation!
	oreRedgranite			("Granite "						, " Ore"							,  true,  true, false, false, false,									-1), // In case of an End-Ores Mod. Ore -> Material is a Oneway Operation!
	oreNetherrack			("Nether "						, " Ore"							,  true,  true, false, false, false,									-1), // Prefix of the Nether-Ores Mod. Causes Ores to double. Ore -> Material is a Oneway Operation!
	oreNether				("Nether "						, " Ore"							,  true,  true, false, false, false,									-1), // Prefix of the Nether-Ores Mod. Causes Ores to double. Ore -> Material is a Oneway Operation!
	oreDense				("Dense "						, " Ore"							,  true,  true, false, false, false,									-1), // Prefix of the Dense-Ores Mod. Causes Ores to double. Ore -> Material is a Oneway Operation!
	orePoor					("Poor "						, " Ore"							,  true,  true, false, false, false,									-1), // Prefix of Railcraft.
	oreEndstone				("End "							, " Ore"							,  true,  true, false, false, false,									-1), // In case of an End-Ores Mod. Ore -> Material is a Oneway Operation!
	oreEnd					("End "							, " Ore"							,  true,  true, false, false, false,									-1), // In case of an End-Ores Mod. Ore -> Material is a Oneway Operation!
	@Deprecated oreGem		(""								, ""								, false, false, false, false, false,									-1),
	ore						(""								, " Ore"							,  true,  true, false, false, false,									-1), // Regular Ore Prefix. Ore -> Material is a Oneway Operation! Introduced by Eloraam
	crushedCentrifuged		("Centrifuged "					, " Ore"							,  true,  true, false, false, false,									-1), 
	crushedPurified			("Purified "					, " Ore"							,  true,  true, false, false, false,									-1), 
	crushed					("Crushed "						, " Ore"							,  true,  true, false, false, false,									-1), 
	ingotQuintuple			("Quintuple "					, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 5), // A quintuple Ingot.
	ingotQuadruple			("Quadruple "					, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // A quadruple Ingot.
	@Deprecated ingotQuad	(""								, ""								, false, false, false, false, false,									-1),
	ingotTriple				("Triple "						, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // A triple Ingot.
	ingotDouble				("Double "						, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // A double Ingot. Similar to the double Ingots of TerrafirmaCraft.
	ingotHot				("Hot "							, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // A hot Ingot, which has to be cooled down by a Vacuum Freezer.
	ingot					(""								, " Ingot"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // A regular Ingot. Introduced by Eloraam
	gem						(""								, ""								,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // A regular Gem or crystallized Metal worth one Dust. Introduced by Eloraam
	@Deprecated dustDirty	(""								, ""								, false, false, false, false, false,									-1),
	dustTiny				("Tiny Pile of "				, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 9), // 1/9th of a Dust.
	dustSmall				("Small Pile of "				, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 4), // 1/4th of a Dust.
	dustImpure				("Impure Pile of "				, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // Dust with impurities. 1 Unit of Main Material and 1/9 - 1/4 Unit of secondary Material
	dustRefined				("Refined Pile of "				, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1),
	dustPure				("Purified Pile of "			, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // Dust without impurities.
	dust					(""								, " Dust"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // Pure Dust worth of one Ingot or Gem. Introduced by Alblaka.
	nugget					(""								, " Nugget"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 9), // A Nugget. Introduced by Eloraam
	plateAlloy				(""								, ""								,  true, false, false, false, false,									-1), // Special Alloys have this prefix.
	plateDense				("Dense "						, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 9), // 9 Plates combined in one Item.
	plateQuintuple			("Quintuple "					, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 5),
	plateQuadruple			("Quadruple "					, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4),
	@Deprecated plateQuad	(""								, ""								, false, false, false, false, false,									-1),
	plateTriple				("Triple "						, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3),
	plateDouble				("Double "						, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2),
	plate					(""								, " Plate"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // Regular Plate made of one Ingot/Dust. Introduced by Calclavia
	foil					(""								, " Foil"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 4), // Foil made of 1/4 Ingot/Dust.
	glass					(""								, ""								, false, false,  true, false, false,									-1),
	paneGlass				(""								, ""								, false, false,  true, false, false,									-1),
	blockGlass				(""								, ""								, false, false,  true, false, false,									-1),
	block_					(""								, ""								, false, false, false, false, false,									-1), // IGNORE
	block					("Block of "					, ""								,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 9), // Storage Block consisting out of 9 Ingots/Gems/Dusts. Introduced by CovertJaguar
	stick					(""								, " Rod"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 2), // Stick made of half an Ingot. Introduced by Eloraam
	lens					(""								, " Lens"							,  true,  true, false, false, false,(GregTech_API.MATERIAL_UNIT * 3)   / 4), // 3/4 of a Plate or Gem used to shape a Lense. Normally only used on Transparent Materials.
	round					(""								, " Round"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 9), // consisting out of one Nugget.
	bolt					(""								, " Bolt"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 8), // consisting out of 1/8 Ingot or 1/4 Stick.
	screw					(""								, " Screw"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 9), // consisting out of a Bolt.
	ring					(""								, " Ring"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 4), // consisting out of 1/2 Stick.
	crateGtDust				("Crate of "					, " Dust"							,  true,  true, false,  true, false,									-1), // consisting out of 16 Dusts.
	crateGtPlate			("Crate of "					, " Plate"							,  true,  true, false,  true, false,									-1), // consisting out of 16 Plates.
	crateGtIngot			("Crate of "					, " Ingot"							,  true,  true, false,  true, false,									-1), // consisting out of 16 Ingots.
	crateGtGem				("Crate of "					, " Gem"							,  true,  true, false,  true, false,									-1), // consisting out of 16 Gems.
	cellPlasma				(""								, " Plasma Cell"					,  true,  true,  true,  true, false, GregTech_API.MATERIAL_UNIT *		 1), // Hot Cell full of Plasma, which can be used in the Plasma Generator.
	cell					(""								, " Cell"							,  true,  true,  true,  true, false, GregTech_API.MATERIAL_UNIT *		 1), // Regular Gas/Fluid Cell. Introduced by Calclavia
	bucket					(""								, " Bucket"							,  true,  true,  true,  true, false, GregTech_API.MATERIAL_UNIT *		 1), // A vanilla Iron Bucket filled with the Material.
	bottle					(""								, " Bottle"							,  true,  true,  true,  true, false, 									-1), // Glas Bottle containing a Fluid.
	capsule					(""								, " Capsule"						, false,  true,  true,  true, false, GregTech_API.MATERIAL_UNIT *		 1),
	crystal					(""								, " Crystal"						, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1),
	craftingTool			(""								, ""								, false, false, false, false, false,									-1), // Special Prefix used mainly for the Crafting Handler.
	crafting				(""								, ""								, false, false, false, false, false,									-1), // Special Prefix used mainly for the Crafting Handler.
	craft					(""								, ""								, false, false, false, false, false,									-1), // Special Prefix used mainly for the Crafting Handler.
	log						(""								, ""								, false, false, false, false, false,									-1), // Prefix used for Logs. Usually as "logWood". Introduced by Eloraam
	slab					(""								, ""								, false, false, false, false, false,									-1), // Prefix used for Slabs. Usually as "slabWood" or "slabStone". Introduced by SirSengir
	stair					(""								, ""								, false, false, false, false, false,									-1), // Prefix used for Stairs. Usually as "stairWood" or "stairStone". Introduced by SirSengir
	plank					(""								, ""								, false, false, false, false, false, 									-1), // Prefix for Planks. Usually "plankWood". Introduced by Eloraam
	treeSapling				(""								, ""								, false, false,  true, false, false,									-1), // Prefix for Saplings.
	treeLeaves				(""								, ""								, false, false,  true, false, false,									-1), // Prefix for Leaves.
	tree					(""								, ""								, false, false, false, false, false,									-1), // Prefix for Tree Parts.
	stoneCobble				(""								, ""								, false, false,  true, false, false,									-1), // Cobblestone Prefix for all Cobblestones.
	stoneSmooth				(""								, ""								, false, false,  true, false, false,									-1), // Smoothstone Prefix.
	stoneMossyBricks		(""								, ""								, false, false,  true, false, false,									-1), // Mossy Stone Bricks.
	stoneMossy				(""								, ""								, false, false,  true, false, false,									-1), // Mossy Cobble.
	@Deprecated stoneBricksMossy(""							, ""								, false, false, false, false, false,									-1),
	stoneBricks				(""								, ""								, false, false,  true, false, false,									-1), // Stone Bricks.
	@Deprecated stoneBrick	(""								, ""								, false, false, false, false, false,									-1),
	stoneCracked			(""								, ""								, false, false,  true, false, false,									-1), // Cracked Bricks.
	stoneChiseled			(""								, ""								, false, false,  true, false, false,									-1), // Chiseled Stone.
	stone					(""								, ""								, false,  true,  true, false,  true,									-1), // Prefix to determine which kind of Rock this is.
	cobblestone				(""								, ""								, false,  true,  true, false, false,									-1),
	record					(""								, ""								, false, false,  true, false, false,									-1),
	rubble					(""								, ""								,  true,  true,  true, false, false,									-1),
	scraps					(""								, ""								,  true,  true, false, false, false,									-1),
	scrap					(""								, ""								, false, false, false, false, false,									-1),
	item_					(""								, ""								, false, false, false, false, false,									-1), // IGNORE
	item					(""								, ""								, false, false, false, false, false,									-1), // Random Item. Introduced by Alblaka
	book					(""								, ""								, false, false, false, false, false,									-1), // Used for Books of any kind.
	paper					(""								, ""								, false, false, false, false, false,									-1), // Used for Papers of any kind.
	dye						(""								, ""								, false, false,  true, false, false,									-1), // Used for the 16 dyes. Introduced by Eloraam
	armorHelmet				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 5), // vanilly Helmet
	armorChestplate			(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 8), // vanilly Chestplate
	armorLeggins			(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 7), // vanilly Pants
	armorBoots				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // vanilly Boots
	armor					(""								, ""								, false, false, false, false, false, 									-1),
	toolHeadSword			(""								, " Sword Blade"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // consisting out of 2 Ingots.
	toolHeadPickaxe			(""								, " Pickaxe Head"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // consisting out of 3 Ingots.
	toolHeadShovel			(""								, " Shovel Head"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // consisting out of 1 Ingots.
	toolHeadUniversalSpade	(""								, " Universal Spade Head"			,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // consisting out of 1 Ingots.
	toolHeadAxe				(""								, " Axe Head"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // consisting out of 3 Ingots.
	toolHeadHoe				(""								, " Hoe Head"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // consisting out of 2 Ingots.
	toolHeadSense			(""								, " Sense Blade"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // consisting out of 3 Ingots.
	toolHeadFile			(""								, " File Head"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // consisting out of 2 Ingots.
	toolHeadHammer			(""								, " Hammer Head"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 6), // consisting out of 6 Ingots.
	toolHeadPlow			(""								, " Plow Head"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // consisting out of 4 Ingots.
	toolHeadSaw				(""								, " Saw Blade"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // consisting out of 2 Ingots.
	toolHeadScrewdriver		(""								, " Screwdriver Tip"				,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // consisting out of 1 Ingots.
	toolHeadDrill			(""								, " Drill Tip"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // consisting out of 4 Ingots.
	toolHeadChainsaw		(""								, " Chainsaw Tip"					,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // consisting out of 2 Ingots.
	toolHeadWrench			(""								, " Wrench Tip"						,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // consisting out of 4 Ingots.
	toolSword				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // vanilly Sword
	toolPickaxe				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // vanilly Pickaxe
	toolShovel				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1), // vanilly Shovel
	toolAxe					(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 3), // vanilly Axe
	toolHoe					(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // vanilly Hoe
	toolShears				(""								, ""								, false,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2), // vanilly Shears
	tool					(""								, ""								, false, false, false, false, false, 									-1), // toolPot, toolSkillet, toolSaucepan, toolBakeware, toolCuttingboard, toolMortarandpestle, toolMixingbowl, toolJuicer
	frameGt					(""								, ""								,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 2),
	pipeTiny				("Tiny "						, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT /		 2),
	pipeSmall				("Small "						, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 1),
	pipeMedium				("Medium "						, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 3),
	pipeLarge				("Large "						, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 6),
	pipeHuge				("Huge "						, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		12),
	pipeRestrictiveTiny		("Tiny Restrictive "			, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT /		 2),
	pipeRestrictiveSmall	("Small Restrictive "			, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 1),
	pipeRestrictiveMedium	("Medium Restrictive "			, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 3),
	pipeRestrictiveLarge	("Large Restrictive "			, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		 6),
	pipeRestrictiveHuge		("Huge Restrictive "			, " Pipe"							,  true,  true, false, false,  true, GregTech_API.MATERIAL_UNIT *		12),
	pipe					(""								, " Pipe"							, false, false, false, false, false, 									-1),
	gearGt					(""								, " Gear"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4), // Introduced by me because BuildCraft has ruined the gear Prefix...
	wireGt16				("16x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 8),
	wireGt12				("12x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 6),
	wireGt08				("8x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4),
	wireGt04				("4x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2),
	wireGt02				("2x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1),
	wireGt01				("1x "							, " Wire"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 2),
	cableGt12				("12x "							, " Cable"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 6),
	cableGt08				("8x "							, " Cable"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 4),
	cableGt04				("4x "							, " Cable"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 2),
	cableGt02				("2x "							, " Cable"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT *		 1),
	cableGt01				("1x "							, " Cable"							,  true,  true, false, false, false, GregTech_API.MATERIAL_UNIT /		 2),
	
	/* Electric Components.
	 * 
	 * usual Materials for this are:
	 * Primitive (Tier 1)
	 * Basic (Tier 2) as used by UE as well : IC2 Circuit and RE-Battery
	 * Good (Tier 3)
	 * Advanced (Tier 4) as used by UE as well : Advanced Circuit, Advanced Battery and Lithium Battery
	 * Data (Tier 5) : Data Storage Circuit
	 * Elite (Tier 6) as used by UE as well : Energy Crystal and Data Control Circuit
	 * Master (Tier 7) : Energy Flow Circuit and Lapotron Crystal
	 * Ultimate (Tier 8) : Data Orb and Lapotronic Energy Orb
	 * Infinite (Cheaty)
	 */
	batterySingleuse	(""								, ""								, false,  true, false, false, false,										-1),
	battery				(""								, ""								, false,  true, false, false, false,										-1), // Introduced by Calclavia
	circuitBoard		(""								, ""								,  true,  true, false, false, false,										-1), // Board needed for creating a Circuit of the same Tier
	circuitPart			(""								, ""								,  true,  true, false, false, false,										-1), // Part needed for creating a Circuit of the same Tier
	circuit				(""								, ""								,  true,  true, false, false, false,										-1), // Introduced by Calclavia
	computer			(""								, ""								,  true,  true, false, false,  true,										-1), // A whole Computer. "computerMaster" = ComputerCube
	
	// random known prefixes without special abilities.
	cluster				(""								, ""								, false, false, false, false, false,										-1),
	grafter				(""								, ""								, false, false, false, false, false,										-1),
	scoop				(""								, ""								, false, false, false, false, false,										-1),
	frame				(""								, ""								, false, false, false, false, false,										-1),
	tome				(""								, ""								, false, false, false, false, false,										-1),
	junk				(""								, ""								, false, false, false, false, false,										-1),
	bee					(""								, ""								, false, false, false, false, false,										-1),
	rod					(""								, ""								, false, false, false, false, false,										-1),
	dirt				(""								, ""								, false, false, false, false, false,										-1),
	sand				(""								, ""								, false, false, true , false, false,										-1),
	grass				(""								, ""								, false, false, false, false, false,										-1),
	gravel				(""								, ""								, false, false, false, false, false,										-1),
	mushroom			(""								, ""								, false, false, false, false, false,										-1),
	wood				(""								, ""								, false, false, false, false, false,										-1), // Introduced by Eloraam
	drop				(""								, ""								, false, false, false, false, false,										-1),
	fuel				(""								, ""								, false, false, false, false, false,										-1),
	panel				(""								, ""								, false, false, false, false, false,										-1),
	brick				(""								, ""								, false, false, false, false, false,										-1),
	chunk				(""								, ""								, false, false, false, false, false,										-1),
	wire				(""								, ""								, false, false, false, false, true ,										-1),
	seed				(""								, ""								, false, false, false, false, false,										-1),
	reed				(""								, ""								, false, false, false, false, false,										-1),
	sheet				(""								, ""								, false, false, false, false, false,										-1),
	crop				(""								, ""								, false, false, false, false, false,										-1),
	plant				(""								, ""								, false, false, false, false, false,										-1),
	coin				(""								, ""								, false, false, false, false, false,										-1),
	lumar				(""								, ""								, false, false, false, false, false,										-1),
	ground				(""								, ""								, false, false, false, false, false,										-1),
	cable				(""								, ""								, false, false, false, false, false,										-1),
	reduced				(""								, ""								, false, false, false, false, false,										-1),
	component			(""								, ""								, false, false, false, false, false,										-1),
	crystalline			(""								, ""								, false, false, false, false, false,										-1),
	cleanGravel			(""								, ""								, false, false, false, false, false,										-1),
	dirtyGravel			(""								, ""								, false, false, false, false, false,										-1),
	wax					(""								, ""								, false, false, false, false, false,										-1),
	wall				(""								, ""								, false, false, false, false, false,										-1),
	tube				(""								, ""								, false, false, false, false, false,										-1),
	list				(""								, ""								, false, false, false, false, false,										-1),
	food				(""								, ""								, false, false, false, false, false,										-1),
	gear				(""								, ""								, false, false, false, false, false,										-1), // Introduced by SirSengir
	coral				(""								, ""								, false, false, false, false, false,										-1),
	shard				(""								, ""								, false, false, false, false, false,										-1),
	clump				(""								, ""								, false, false, false, false, false,										-1),
	flower				(""								, ""								, false, false, false, false, false,										-1),
	storage				(""								, ""								, false, false, false, false, false,										-1),
	material			(""								, ""								, false, false, false, false, false,										-1),
	plasma				(""								, ""								, false, false, false, false, false,										-1),
	element				(""								, ""								, false, false, false, false, false,										-1),
	molecule			(""								, ""								, false, false, false, false, false,										-1),
	wafer				(""								, ""								, false, false, false, false, false,										-1),
	orb					(""								, ""								, false, false, false, false, false,										-1),
	handle				(""								, ""								, false, false, false, false, false,										-1),
	blade				(""								, ""								, false, false, false, false, false,										-1),
	head				(""								, ""								, false, false, false, false, false,										-1),
	motor				(""								, ""								, false, false, false, false, false,										-1),
	bit					(""								, ""								, false, false, false, false, false,										-1),
	shears				(""								, ""								, false, false, false, false, false,										-1),
	turbine				(""								, ""								, false, false, false, false, false,										-1),
	compressed			(""								, ""								, false, false, false, false, false,										-1),
	fertilizer			(""								, ""								, false, false, false, false, false,										-1),
	chest				(""								, ""								, false, false, false, false, false,										-1),
	raw					(""								, ""								, false, false, false, false, false,										-1),
	stainedGlass		(""								, ""								, false, false, false, false, false,										-1),
	mystic				(""								, ""								, false, false, false, false, false,										-1),
	mana				(""								, ""								, false, false, false, false, false,										-1),
	rune				(""								, ""								, false, false, false, false, false,										-1),
	petal				(""								, ""								, false, false, false, false, false,										-1),
	pearl				(""								, ""								, false, false, false, false, false,										-1),
	powder				(""								, ""								, false, false, false, false, false,										-1);
	
	
	static {
		pulp.mPrefixInto = dust;
		oreGem.mPrefixInto = ore;
		leaves.mPrefixInto = treeLeaves;
		sapling.mPrefixInto = treeSapling;
		itemDust.mPrefixInto = dust;
		dustDirty.mPrefixInto = dustImpure;
		ingotQuad.mPrefixInto = ingotQuadruple;
		plateQuad.mPrefixInto = plateQuadruple;
		stoneBrick.mPrefixInto = stoneBricks;
		stoneBricksMossy.mPrefixInto = stoneMossyBricks;
		
		block.ignoreMaterials(Materials.Concrete, Materials.Glass, Materials.Glowstone, Materials.DarkIron, Materials.Marble, Materials.Quartz, Materials.CertusQuartz, Materials.Limestone);
		ingot.ignoreMaterials(Materials.Brick, Materials.NetherBrick);
		
		// These are only the important ones.
		gem.mNotGeneratedItems.add(Materials.Coal);
		gem.mNotGeneratedItems.add(Materials.Charcoal);
		gem.mNotGeneratedItems.add(Materials.NetherStar);
		gem.mNotGeneratedItems.add(Materials.Diamond);
		gem.mNotGeneratedItems.add(Materials.Emerald);
		gem.mNotGeneratedItems.add(Materials.NetherQuartz);
		gem.mNotGeneratedItems.add(Materials.EnderPearl);
		gem.mNotGeneratedItems.add(Materials.EnderEye);
		gem.mNotGeneratedItems.add(Materials.Flint);
		gem.mNotGeneratedItems.add(Materials.Lapis);
		gem.mNotGeneratedItems.add(Materials.Glass);
		dust.mNotGeneratedItems.add(Materials.Bone);
		dust.mNotGeneratedItems.add(Materials.Redstone);
		dust.mNotGeneratedItems.add(Materials.Glowstone);
		dust.mNotGeneratedItems.add(Materials.Gunpowder);
		dust.mNotGeneratedItems.add(Materials.Sugar);
		dust.mNotGeneratedItems.add(Materials.Blaze);
		stick.mNotGeneratedItems.add(Materials.Wood);
		stick.mNotGeneratedItems.add(Materials.Bone);
		stick.mNotGeneratedItems.add(Materials.Blaze);
		ingot.mNotGeneratedItems.add(Materials.Iron);
		ingot.mNotGeneratedItems.add(Materials.Gold);
		ingot.mNotGeneratedItems.add(Materials.Brick);
		ingot.mNotGeneratedItems.add(Materials.BrickNether);
		ingot.mNotGeneratedItems.add(Materials.WoodSealed);
		nugget.mNotGeneratedItems.add(Materials.Gold);
		plate.mNotGeneratedItems.add(Materials.Paper);
		cell.mNotGeneratedItems.add(Materials.Empty);
		cell.mNotGeneratedItems.add(Materials.Water);
		cell.mNotGeneratedItems.add(Materials.Lava);
		cell.mNotGeneratedItems.add(Materials.Oxygen);
		cell.mNotGeneratedItems.add(Materials.ConstructionFoam);
		cell.mNotGeneratedItems.add(Materials.UUMatter);
		cell.mNotGeneratedItems.add(Materials.BioFuel);
		cell.mNotGeneratedItems.add(Materials.CoalFuel);
		cellPlasma.mNotGeneratedItems.add(Materials.Empty);
		bucket.mNotGeneratedItems.add(Materials.Empty);
		bucket.mNotGeneratedItems.add(Materials.Lava);
		bucket.mNotGeneratedItems.add(Materials.Milk);
		bucket.mNotGeneratedItems.add(Materials.Water);
		bottle.mNotGeneratedItems.add(Materials.Empty);
		bottle.mNotGeneratedItems.add(Materials.Water);
		bottle.mNotGeneratedItems.add(Materials.Milk);
		block.mNotGeneratedItems.add(Materials.Iron);
		block.mNotGeneratedItems.add(Materials.Gold);
		block.mNotGeneratedItems.add(Materials.Lapis);
		block.mNotGeneratedItems.add(Materials.Emerald);
		block.mNotGeneratedItems.add(Materials.Redstone);
		block.mNotGeneratedItems.add(Materials.Diamond);
		block.mNotGeneratedItems.add(Materials.Coal);

		pipeRestrictiveTiny.mSecondaryMaterial		= new MaterialStack(Materials.Steel, OrePrefixes.ring.mMaterialAmount * 1);
		pipeRestrictiveSmall.mSecondaryMaterial		= new MaterialStack(Materials.Steel, OrePrefixes.ring.mMaterialAmount * 2);
		pipeRestrictiveMedium.mSecondaryMaterial	= new MaterialStack(Materials.Steel, OrePrefixes.ring.mMaterialAmount * 3);
		pipeRestrictiveLarge.mSecondaryMaterial		= new MaterialStack(Materials.Steel, OrePrefixes.ring.mMaterialAmount * 4);
		pipeRestrictiveHuge.mSecondaryMaterial		= new MaterialStack(Materials.Steel, OrePrefixes.ring.mMaterialAmount * 5);
		cableGt12.mSecondaryMaterial = new MaterialStack(Materials.Rubber, GregTech_API.MATERIAL_UNIT * 4);
		cableGt08.mSecondaryMaterial = new MaterialStack(Materials.Rubber, GregTech_API.MATERIAL_UNIT * 3);
		cableGt04.mSecondaryMaterial = new MaterialStack(Materials.Rubber, GregTech_API.MATERIAL_UNIT * 2);
		cableGt02.mSecondaryMaterial = new MaterialStack(Materials.Rubber, GregTech_API.MATERIAL_UNIT * 1);
		cableGt01.mSecondaryMaterial = new MaterialStack(Materials.Rubber, GregTech_API.MATERIAL_UNIT * 1);
		bucket.mSecondaryMaterial = new MaterialStack(Materials.Iron, GregTech_API.MATERIAL_UNIT * 3);
		cell.mSecondaryMaterial = new MaterialStack(Materials.Tin, GregTech_API.MATERIAL_UNIT / 2);
		cellPlasma.mSecondaryMaterial = new MaterialStack(Materials.Tin, GregTech_API.MATERIAL_UNIT / 2);
		oreRedgranite.mSecondaryMaterial = new MaterialStack(Materials.GraniteRed, GregTech_API.MATERIAL_UNIT * 1);
		oreBlackgranite.mSecondaryMaterial = new MaterialStack(Materials.GraniteBlack, GregTech_API.MATERIAL_UNIT * 1);
		oreNetherrack.mSecondaryMaterial = new MaterialStack(Materials.Netherrack, GregTech_API.MATERIAL_UNIT * 1);
		oreNether.mSecondaryMaterial = new MaterialStack(Materials.Netherrack, GregTech_API.MATERIAL_UNIT * 1);
		oreEndstone.mSecondaryMaterial = new MaterialStack(Materials.Endstone, GregTech_API.MATERIAL_UNIT * 1);
		oreEnd.mSecondaryMaterial = new MaterialStack(Materials.Endstone, GregTech_API.MATERIAL_UNIT * 1);
		oreDense.mSecondaryMaterial = new MaterialStack(Materials.Stone, GregTech_API.MATERIAL_UNIT * 1);
		orePoor.mSecondaryMaterial = new MaterialStack(Materials.Stone, GregTech_API.MATERIAL_UNIT * 1);
		ore.mSecondaryMaterial = new MaterialStack(Materials.Stone, GregTech_API.MATERIAL_UNIT * 1);
		crushed.mSecondaryMaterial = new MaterialStack(Materials.Stone, GregTech_API.MATERIAL_UNIT * 1);
		toolHeadDrill.mSecondaryMaterial = new MaterialStack(Materials.Steel, plate.mMaterialAmount * 4);
		toolHeadChainsaw.mSecondaryMaterial = new MaterialStack(Materials.Steel, plate.mMaterialAmount * 4 + ring.mMaterialAmount * 2);
		toolHeadWrench.mSecondaryMaterial = new MaterialStack(Materials.Steel, ring.mMaterialAmount * 1 + screw.mMaterialAmount * 2);
	}
	
	public final ArrayList<ItemStack> mPrefixedItems = new ArrayList<ItemStack>();
	
	public boolean add(ItemStack aStack) {
		if (aStack == null) return false;
		if (!contains(aStack)) mPrefixedItems.add(aStack);
		while (mPrefixedItems.contains(null)) mPrefixedItems.remove(null);
		return true;
	}
	
	public boolean contains(ItemStack aStack) {
		if (aStack == null) return false;
		for (ItemStack tStack : mPrefixedItems) if (GT_Utility.areStacksEqual(aStack, tStack, !tStack.hasTagCompound())) return true;
		return false;
	}
	
	public boolean dontGenerateItem(Materials aMaterial) {
		return mNotGeneratedItems.contains(aMaterial);
	}
	
	public boolean ignoreMaterials(Materials... aMaterials) {
		for (Materials tMaterial : aMaterials) mIgnoredMaterials.add(tMaterial);
		return true;
	}
	
	public boolean isIgnored(Materials aMaterial) {
		return mIgnoredMaterials.contains(aMaterial);
	}
	
	public boolean add(IOreRecipeRegistrator aRegistrator) {
		if (aRegistrator == null) return false;
		return mOreProcessing.add(aRegistrator);
	}
	
	public void processOre(Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
		if (aMaterial != null && (aMaterial != Materials._NULL || mIsSelfReferencing || !mIsMaterialBased) && GT_Utility.isStackValid(aStack)) for (IOreRecipeRegistrator tRegistrator : mOreProcessing) tRegistrator.registerOre(this, aMaterial, aOreDictName, aModName, GT_Utility.copyAmount(1, aStack));
	}
	
	public final String mLocalizedMaterialPre, mLocalizedMaterialPost;
	public final boolean mIsUnificatable, mIsMaterialBased, mIsSelfReferencing, mIsContainer, mDontUnificateActively;
	public MaterialStack mSecondaryMaterial = null;
	public OrePrefixes mPrefixInto = this;
	public final List<TC_AspectStack> mAspects = new ArrayList<TC_AspectStack>();
	private final ArrayList<Materials> mNotGeneratedItems = new ArrayList<Materials>(), mIgnoredMaterials = new ArrayList<Materials>();
	private final ArrayList<IOreRecipeRegistrator> mOreProcessing = new ArrayList<IOreRecipeRegistrator>();
	
	/**
	 * Used to determine the amount of Material this Prefix contains.
	 * Multiply or Divide GregTech_API.MATERIAL_UNIT to set the Amounts in comparision to one Ingot.
	 * 0 = Null
	 * Negative = Undefined Amount
	 */
	public final long mMaterialAmount;
	
	private OrePrefixes(String aLocalizedMaterialPre, String aLocalizedMaterialPost, boolean aIsUnificatable, boolean aIsMaterialBased, boolean aIsSelfReferencing, boolean aIsContainer, boolean aDontUnificateActively, long aMaterialAmount) {
		mIsUnificatable = aIsUnificatable;
		mIsMaterialBased = aIsMaterialBased;
		mIsSelfReferencing = aIsSelfReferencing;
		mIsContainer = aIsContainer;
		mDontUnificateActively = aDontUnificateActively;
		mMaterialAmount = aMaterialAmount;
		mLocalizedMaterialPre = aLocalizedMaterialPre;
		mLocalizedMaterialPost = aLocalizedMaterialPost;
		
		if (name().startsWith("ore")) {
			new TC_AspectStack(TC_Aspects.TERRA, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("wire") || name().startsWith("cable")) {
			new TC_AspectStack(TC_Aspects.ELECTRUM, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("dust")) {
			new TC_AspectStack(TC_Aspects.PERDITIO, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("crushed")) {
			new TC_AspectStack(TC_Aspects.PERFODIO, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("ingot")) {
			new TC_AspectStack(TC_Aspects.METALLUM, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("armor")) {
			new TC_AspectStack(TC_Aspects.TUTAMEN, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("stone")) {
			new TC_AspectStack(TC_Aspects.TERRA, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("pipe")) {
			new TC_AspectStack(TC_Aspects.ITER, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("gear")) {
			new TC_AspectStack(TC_Aspects.MOTUS, 1).addToAspectList(mAspects);
			new TC_AspectStack(TC_Aspects.MACHINA, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("frame") || name().startsWith("plate")) {
			new TC_AspectStack(TC_Aspects.FABRICO, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("tool")) {
			new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 2).addToAspectList(mAspects);
		} else
		if (name().startsWith("gem") || name().startsWith("crystal") || name().startsWith("lens")) {
			new TC_AspectStack(TC_Aspects.VITREUS, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("crate")) {
			new TC_AspectStack(TC_Aspects.ITER, 2).addToAspectList(mAspects);
		} else
		if (name().startsWith("circuit")) {
			new TC_AspectStack(TC_Aspects.COGNITO, 1).addToAspectList(mAspects);
		} else
		if (name().startsWith("computer")) {
			new TC_AspectStack(TC_Aspects.COGNITO, 4).addToAspectList(mAspects);
		} else
		if (name().startsWith("battery")) {
			new TC_AspectStack(TC_Aspects.ELECTRUM, 1).addToAspectList(mAspects);
		}
	}
	
	public static OrePrefixes getOrePrefix(String aOre) {
		for (OrePrefixes tPrefix : values()) if (aOre.startsWith(tPrefix.toString())) {
			if (tPrefix == oreNether && aOre.equals("oreNetherQuartz")) return ore;
			return tPrefix;
		}
		return null;
	}
	
	public static String stripPrefix(String aOre) {
		for (OrePrefixes tPrefix : values()) {
			if (aOre.startsWith(tPrefix.toString())) {
				return aOre.replaceFirst(tPrefix.toString(), "");
			}
		}
		return aOre;
	}
	
	public static String replacePrefix(String aOre, OrePrefixes aPrefix) {
		for (OrePrefixes tPrefix : values()) {
			if (aOre.startsWith(tPrefix.toString())) {
				return aOre.replaceFirst(tPrefix.toString(), aPrefix.toString());
			}
		}
		return "";
	}
	
	public static OrePrefixes getPrefix(String aPrefixName) {
		return getPrefix(aPrefixName, null);
	}
	
	public static OrePrefixes getPrefix(String aPrefixName, OrePrefixes aReplacement) {
		Object tObject = GT_Utility.getFieldContent(OrePrefixes.class, aPrefixName, false, false);
		if (tObject != null && tObject instanceof OrePrefixes) return (OrePrefixes)tObject;
		return aReplacement;
	}
	
	public Object get(Object aMaterial) {
		if (aMaterial instanceof Materials) return new OrePrefixMaterialData(this, (Materials)aMaterial);
		return name() + aMaterial;
	}
	
	public static Materials getMaterial(String aOre) {
		return Materials.get(stripPrefix(aOre));
	}

	public static Materials getMaterial(String aOre, OrePrefixes aPrefix) {
		return Materials.get(aOre.replaceFirst(aPrefix.toString(), ""));
	}
	
	public static Materials getRealMaterial(String aOre, OrePrefixes aPrefix) {
		return Materials.getRealMaterial(aOre.replaceFirst(aPrefix.toString(), ""));
	}
	
	public static boolean isInstanceOf(String aName, OrePrefixes aPrefix) {
		return aName == null ? false : aName.startsWith(aPrefix.toString());
	}
	
	public static volatile int VERSION = 503;
}