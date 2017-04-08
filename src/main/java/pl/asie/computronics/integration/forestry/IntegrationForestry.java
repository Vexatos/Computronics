package pl.asie.computronics.integration.forestry;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeMutationCustom;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.render.ParticleRenderer;
import li.cil.oc.api.Items;
import li.cil.oc.api.Nanomachines;
import net.bdew.gendustry.api.EnumMutationSetting;
import net.bdew.gendustry.api.GendustryAPI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.forestry.client.SwarmTextureHandler;
import pl.asie.computronics.integration.forestry.client.entity.EntitySwarmBeeFX;
import pl.asie.computronics.integration.forestry.entity.EntitySwarm;
import pl.asie.computronics.integration.forestry.entity.SwarmRenderer;
import pl.asie.computronics.integration.forestry.nanomachines.SwarmProvider;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;
import pl.asie.lib.item.ItemMultiple;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class IntegrationForestry {

	public static IAlleleSpecies speciesScummy;
	public static IBeeMutationCustom scummyA;
	public static IBeeMutationCustom scummyB;
	public static IAlleleFlowers sea;

	public static ItemMultiple itemPartsForestry;
	public static Item itemStickImpregnated;

	private static final String
		speciesAgrarian = "forestry.speciesAgrarian",
		speciesExotic = "forestry.speciesExotic",
		speciesTipsy = "forestry.speciesTipsy";

	public void preInitOC() {
		if(Mods.isLoaded(Mods.OpenComputers)) {
			itemPartsForestry = new ItemMultiple(Mods.Computronics, new String[] { "for.combAcid", "for.dropAcid" });
			itemPartsForestry.setCreativeTab(Computronics.tab);
			GameRegistry.registerItem(itemPartsForestry, "computronics.partsForestry");
			if(Computronics.proxy.isClient()) {
				MinecraftForge.EVENT_BUS.register(new SwarmTextureHandler());
			}
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
		speciesScummy = BeeManager.beeFactory.createSpecies("computronics.speciesScummy", false, "Sangar",
			"computronics.bees.species.scummy", "computronics.bees.species.scummy.description", pirates, "ebriosus", 0x00DF1F, 0xffdc16)
			.setNocturnal().setJubilanceProvider(new JubilanceSea(shortMead, 0))
			.addSpecialty(new ItemStack(itemPartsForestry, 1, 0), 0.2f).setIsSecret()
			.setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP).setHasEffect().setIsNotCounted();

		scummyA = BeeManager.beeMutationFactory.createMutation(
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesAgrarian),
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 2);
		if(shortMead != null) {
			scummyA.requireResource(shortMead, 0);
		}
		scummyA.restrictBiomeType(Type.OCEAN, Type.HOT).restrictBiomeType(Type.OCEAN, Type.WET)
			.requireNight()
			.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HELLISH)
			.setIsSecret();

		scummyB = BeeManager.beeMutationFactory.createMutation(
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesTipsy),
			(IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(speciesExotic), getScummyTemplate(), 10);
		scummyB.requireNight().setIsSecret();
		BeeManager.beeRoot.registerTemplate(getScummyTemplate());
		HashMap<ItemStack, Float> acidRecipe = new HashMap<ItemStack, Float>();
		acidRecipe.put(new ItemStack(itemPartsForestry, 1, 1), 1.0f);
		acidRecipe.put(new ItemStack(itemPartsForestry, 1, 1), 0.3f);
		RecipeManagers.centrifugeManager.addRecipe(40,
			new ItemStack(itemPartsForestry, 1, 0),
			acidRecipe);
		Item bottleItem = GameRegistry.findItem(Mods.Forestry, "beverage");
		ItemStack bottle = bottleItem != null ? new ItemStack(bottleItem, 1, 0)
			: new ItemStack(net.minecraft.init.Items.potionitem, 1, 32);
		RecipeUtils.addShapelessRecipe(undisassemblable(Items.get("acid").createItemStack(1)),
			new ItemStack(itemPartsForestry, 1, 1),
			new ItemStack(itemPartsForestry, 1, 1),
			bottle);

		if(Mods.hasVersion(Mods.API.Gendustry, Mods.Versions.Gendustry)) {
			registerBees();
		}

		itemStickImpregnated = GameRegistry.findItem(Mods.Forestry, "oakStick");
		EntityRegistry.registerModEntity(EntitySwarm.class, "swarm", 9, Computronics.instance, 64, 1, true);
		SwarmProvider provider = new SwarmProvider();
		MinecraftForge.EVENT_BUS.register(provider);
		//FMLCommonHandler.instance().bus().register(provider);
		Nanomachines.addProvider(provider);
	}

	private ItemStack undisassemblable(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			tag = new NBTTagCompound();
		tag.setBoolean("oc:undisassemblable", true);
		stack.setTagCompound(tag);
		return stack;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	@SideOnly(Side.CLIENT)
	public void registerOCRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySwarm.class, new SwarmRenderer());
	}

	@Optional.Method(modid = Mods.API.Gendustry)
	private void registerBees() {
		if(GendustryAPI.Registries != null && GendustryAPI.Registries.getMutatronOverrides() != null) {
			GendustryAPI.Registries.getMutatronOverrides().set(speciesScummy, EnumMutationSetting.REQUIREMENTS);
		}
	}

	public static IAllele[] getScummyTemplate() {
		IAllele[] alleles = BeeManager.beeRoot.getTemplate(speciesExotic).clone();

		//Just making sure
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.floweringSlowest");
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.territoryDefault");

		//Actual template
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = speciesScummy;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.fertilityLow");
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolTrue");
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.boolFalse");
		alleles[EnumBeeChromosome.SPEED.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.speedSlowest");
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.lifespanLonger");
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.toleranceUp1");
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = sea;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = AlleleManager.alleleRegistry.getAllele("forestry.effectDrunkard");
		return alleles;
	}

	@SideOnly(Side.CLIENT)
	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
		EntitySwarmBeeFX entity = new EntitySwarmBeeFX(worldObj, xPos, yPos, zPos, color);
		ParticleRenderer.getInstance().addEffect(entity);
		//Minecraft.getMinecraft().effectRenderer.addEffect(entity);
	}
}
