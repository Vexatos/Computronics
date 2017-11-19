package pl.asie.computronics.integration.forestry;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.recipes.RecipeManagers;
import li.cil.oc.api.Items;
import li.cil.oc.api.Nanomachines;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.forestry.client.entity.ParticleSwarm;
import pl.asie.computronics.integration.forestry.entity.EntitySwarm;
import pl.asie.computronics.integration.forestry.entity.RenderSwarm;
import pl.asie.computronics.integration.forestry.nanomachines.SwarmProvider;
import pl.asie.computronics.item.ItemMultipleComputronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class IntegrationForestry {

	public static IAlleleSpecies speciesScummy;
	public static IBeeMutation scummyA;
	public static IBeeMutation scummyB;
	public static IAlleleFlowers sea;

	public static ItemMultipleComputronics itemPartsForestry;
	public static Item itemStickImpregnated;

	private static final String
		speciesAgrarian = "forestry.speciesAgrarian",
		speciesExotic = "forestry.speciesExotic",
		speciesTipsy = "forestry.speciesTipsy";

	public void preInitOC() {
		if(Mods.isLoaded(Mods.OpenComputers)) {
			itemPartsForestry = new ItemMultipleComputronics(Mods.Computronics, new String[] { "acid_comb", "acid_drop" });
			itemPartsForestry.setCreativeTab(Computronics.tab);
			Computronics.instance.registerItem(itemPartsForestry, "forestry_parts");
			itemPartsForestry.registerItemModels();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void initOC() {
		Computronics.log.info("Adding Forestry Bees for OpenComputers.");
		IClassification pirates = BeeManager.beeFactory.createBranch("pirates", "Piraticus");
		AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(pirates);
		FlowerProviderSea providerSea = new FlowerProviderSea();
		sea = AlleleManager.alleleFactory.createFlowers(Mods.Computronics, "flowers", "sea", providerSea, true, EnumBeeChromosome.FLOWER_PROVIDER);
		FlowerManager.flowerRegistry.registerAcceptableFlowerRule(providerSea, providerSea.getFlowerType());

		Block shortMead = null;
		{
			Fluid shortMeadFluid = FluidRegistry.getFluid("short.mead");
			if(shortMeadFluid != null) {
				shortMead = FluidRegistry.getFluid("short.mead").getBlock();
			}
		}
		speciesScummy = BeeManager.beeFactory.createSpecies(Mods.Computronics, "speciesScummy", false, "Sangar",
			"computronics.bees.species.scummy", "computronics.bees.species.scummy.description", pirates, "ebriosus", 0x00DF1F, 0xffdc16)
			.setNocturnal().setJubilanceProvider(shortMead != null ? new JubilanceSea(shortMead.getDefaultState()) : new JubilanceSea())
			.addSpecialty(new ItemStack(itemPartsForestry, 1, 0), 0.2f).setIsSecret()
			.setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP).setHasEffect().setIsNotCounted().build();

		IBeeMutationBuilder scummyA = BeeManager.beeMutationFactory.createMutation(
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesAgrarian),
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 2);
		if(shortMead != null) {
			scummyA.requireResource(shortMead.getDefaultState());
		}
		scummyA.restrictBiomeType(Type.OCEAN, Type.HOT).restrictBiomeType(Type.OCEAN, Type.WET)
			.requireNight()
			.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HELLISH)
			.setIsSecret();
		IntegrationForestry.scummyA = scummyA.build();

		IBeeMutationBuilder scummyB = BeeManager.beeMutationFactory.createMutation(
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesTipsy),
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 10);
		scummyB.requireNight().setIsSecret();
		IntegrationForestry.scummyB = scummyB.build();
		BeeManager.beeRoot.registerTemplate(getScummyTemplate());
		HashMap<ItemStack, Float> acidRecipe = new HashMap<ItemStack, Float>();
		acidRecipe.put(new ItemStack(itemPartsForestry, 1, 1), 1.0f);
		acidRecipe.put(new ItemStack(itemPartsForestry, 1, 1), 0.3f);
		RecipeManagers.centrifugeManager.addRecipe(40,
			new ItemStack(itemPartsForestry, 1, 0),
			acidRecipe);
		Item bottleItem = Item.REGISTRY.getObject(new ResourceLocation(Mods.Forestry, "beverage"));
		ItemStack bottle = bottleItem != null ? new ItemStack(bottleItem, 1, 0)
			: new ItemStack(net.minecraft.init.Items.POTIONITEM, 1, 32);
		RecipeUtils.addShapelessRecipe(undisassemblable(Items.get("acid").createItemStack(1)),
			new ItemStack(itemPartsForestry, 1, 1),
			new ItemStack(itemPartsForestry, 1, 1),
			bottle);

		/*if(Mods.hasVersion(Mods.API.Gendustry, Mods.Versions.Gendustry)) { TODO Gendustry
			registerBees();
		}*/

		itemStickImpregnated = Item.REGISTRY.getObject(new ResourceLocation(Mods.Forestry, "oak_stick"));
		EntityRegistry.registerModEntity(new ResourceLocation(Mods.Computronics, "swarm"), EntitySwarm.class, "swarm", 9, Computronics.instance, 64, 1, true);
		SwarmProvider provider = new SwarmProvider();
		MinecraftForge.EVENT_BUS.register(provider);
		//FMLCommonHandler.instance().bus().register(provider);
		Nanomachines.addProvider(provider);
	}

	private ItemStack undisassemblable(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null) {
			tag = new NBTTagCompound();
		}
		tag.setBoolean("oc:undisassemblable", true);
		stack.setTagCompound(tag);
		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Mods.OpenComputers)
	public void registerOCEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySwarm.class, new RenderSwarm.Factory());
	}

	/*@Optional.Method(modid = Mods.API.Gendustry) TODO Gendustry
	private void registerBees() {
		if(GendustryAPI.Registries != null && GendustryAPI.Registries.getMutatronOverrides() != null) {
			GendustryAPI.Registries.getMutatronOverrides().set(speciesScummy, EnumMutationSetting.REQUIREMENTS);
		}
	}*/

	public static IAllele[] getScummyTemplate() {
		IAllele[] alleles = BeeManager.beeRoot.getTemplate(speciesExotic).clone();

		//Just making sure
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.floweringSlowest");
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.territoryAverage");

		//Actual template
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = speciesScummy;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.fertilityLow");
		alleles[EnumBeeChromosome.TOLERATES_RAIN.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolTrue");
		alleles[EnumBeeChromosome.NEVER_SLEEPS.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.SPEED.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.speedSlowest");
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.lifespanLonger");
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = sea;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.effectDrunkard");
		return alleles;
	}

	@SideOnly(Side.CLIENT)
	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
		ParticleSwarm entity = new ParticleSwarm(worldObj, xPos, yPos, zPos, color);
		Minecraft.getMinecraft().effectRenderer.addEffect(entity);
		//Minecraft.getMinecraft().effectRenderer.addEffect(entity);
	}
}
