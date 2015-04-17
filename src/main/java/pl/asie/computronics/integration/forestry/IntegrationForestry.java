package pl.asie.computronics.integration.forestry;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IMutation;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.genetics.AlleleFlowers;
import forestry.apiculture.genetics.BeeMutation;
import forestry.apiculture.genetics.BranchBees;
import forestry.apiculture.genetics.MutationReqRes;
import li.cil.oc.api.Items;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.item.ItemMultiple;

/**
 * @author Vexatos
 */
public class IntegrationForestry {

	public static IAlleleSpecies speciesScummy;
	public static IMutation scummyA;
	public static IMutation scummyB;
	public static IAlleleFlowers sea;

	public static ItemMultiple itemPartsForestry;

	private static final String
		speciesArgrarian = "forestry.speciesArgrarian",
		speciesExotic = "forestry.speciesExotic",
		speciesTipsy = "forestry.speciesTipsy";

	public void preInitOC() {
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			itemPartsForestry = new ItemMultiple(Mods.Computronics, new String[] { "for.combAcid", "for.dropAcid" });
			itemPartsForestry.setCreativeTab(Computronics.tab);
			GameRegistry.registerItem(itemPartsForestry, "computronics.partsForestry");
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void initOC() {
		Computronics.log.info("Adding Forestry Bees for OpenComputers.");
		IClassification pirates = new BranchBees("pirates", "Piraticus");
		AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(pirates);
		sea = new AlleleFlowers("flowersSea", new FlowerProviderSea(), true);

		Block shortMead = null;
		{
			Fluid shortMeadFluid = FluidRegistry.getFluid("short.mead");
			if(shortMeadFluid != null) {
				shortMead = FluidRegistry.getFluid("short.mead").getBlock();
			}
		}
		speciesScummy = new OCBeeSpecies("computronics.speciesScummy", false, "computronics.bees.species.scummy", pirates, "ebriosus", 0x00DF1F, 0xffdc16, shortMead, 0)
			.addSpecialty(new ItemStack(itemPartsForestry, 1, 0), 20).setEntityTexture("tropicalBee").setIsSecret()
			.setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP).setHasEffect().setIsNotCounted();
		if(shortMead != null) {
			scummyA = new MutationReqRes(AlleleManager.alleleRegistry.getAllele(speciesArgrarian),
				AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 2, new ItemStack(shortMead, 1, 0))
				.requireNight().restrictBiomeType(BiomeDictionary.Type.OCEAN).enableStrictBiomeCheck().setTemperature(0.5f, 1.0f)
				.setIsSecret();
		}
		if(scummyA == null) {
			scummyA = new BeeMutation(AlleleManager.alleleRegistry.getAllele(speciesArgrarian),
				AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 2)
				.requireNight().restrictBiomeType(BiomeDictionary.Type.OCEAN).enableStrictBiomeCheck().setTemperature(0.5f, 1.0f)
				.setIsSecret();
		}

		scummyB = new BeeMutation(AlleleManager.alleleRegistry.getAllele(speciesTipsy),
			AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 10).requireNight().setIsSecret();
		AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").registerTemplate(getScummyTemplate());
		RecipeManagers.centrifugeManager.addRecipe(40,
			new ItemStack(itemPartsForestry, 1, 0),
			new ItemStack(itemPartsForestry, 1, 1),
			new ItemStack(itemPartsForestry, 1, 1), 30);
		Item bottleItem = GameRegistry.findItem(Mods.Forestry, "beverage");
		ItemStack bottle = bottleItem != null ? new ItemStack(bottleItem, 1, 0)
			: new ItemStack(net.minecraft.init.Items.potionitem, 1, 32);
		GameRegistry.addShapelessRecipe(Items.get("acid").createItemStack(1),
			new ItemStack(itemPartsForestry, 1, 1),
			new ItemStack(itemPartsForestry, 1, 1),
			bottle);
	}

	public static IAllele[] getScummyTemplate() {
		IAllele[] alleles = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").getTemplate(speciesExotic).clone();

		//Just making sure
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.floweringSlowest");
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.territoryDefault");

		//Actual template
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = speciesScummy;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.fertilityLow");
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.SPEED.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.speedSlowest");
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.lifespanLonger");
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = sea;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.effectDrunkard");
		return alleles;
	}
}
