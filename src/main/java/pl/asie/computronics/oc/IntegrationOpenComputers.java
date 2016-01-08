package pl.asie.computronics.oc;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.UpgradeRenderer;
import pl.asie.computronics.integration.appeng.DriverSpatialIOPort;
import pl.asie.computronics.integration.armourersworkshop.DriverMannequin;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageNew;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageOld;
import pl.asie.computronics.integration.buildcraft.DriverHeatable;
import pl.asie.computronics.integration.buildcraft.IntegrationBuildCraft;
import pl.asie.computronics.integration.draconicevolution.DriverExtendedRFStorage;
import pl.asie.computronics.integration.enderio.DriverAbstractMachine;
import pl.asie.computronics.integration.enderio.DriverAbstractPoweredMachine;
import pl.asie.computronics.integration.enderio.DriverCapacitorBank;
import pl.asie.computronics.integration.enderio.DriverCapacitorBankOld;
import pl.asie.computronics.integration.enderio.DriverHasExperience;
import pl.asie.computronics.integration.enderio.DriverIOConfigurable;
import pl.asie.computronics.integration.enderio.DriverPowerMonitor;
import pl.asie.computronics.integration.enderio.DriverPowerStorage;
import pl.asie.computronics.integration.enderio.DriverProgressTile;
import pl.asie.computronics.integration.enderio.DriverRedstoneControllable;
import pl.asie.computronics.integration.enderio.DriverTelepad;
import pl.asie.computronics.integration.enderio.DriverTransceiver;
import pl.asie.computronics.integration.enderio.DriverVacuumChest;
import pl.asie.computronics.integration.enderio.DriverWeatherObelisk;
import pl.asie.computronics.integration.factorization.DriverChargeConductor;
import pl.asie.computronics.integration.flamingo.DriverFlamingo;
import pl.asie.computronics.integration.forestry.IntegrationForestry;
import pl.asie.computronics.integration.fsp.DriverSteamTransporter;
import pl.asie.computronics.integration.gregtech.DriverBaseMetaTileEntity;
import pl.asie.computronics.integration.gregtech.DriverBatteryBuffer;
import pl.asie.computronics.integration.gregtech.DriverDeviceInformation;
import pl.asie.computronics.integration.gregtech.DriverDigitalChest;
import pl.asie.computronics.integration.gregtech.DriverMachine;
import pl.asie.computronics.integration.mekanism.DriverStrictEnergyStorage;
import pl.asie.computronics.integration.railcraft.driver.DriverElectricGrid;
import pl.asie.computronics.integration.railcraft.driver.DriverRoutingDetector;
import pl.asie.computronics.integration.railcraft.driver.DriverRoutingSwitch;
import pl.asie.computronics.integration.railcraft.driver.track.DriverLauncherTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverLimiterTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverLocomotiveTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverPoweredTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverPrimingTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverRoutingTrack;
import pl.asie.computronics.integration.redlogic.DriverLamp;
import pl.asie.computronics.integration.storagedrawers.DriverDrawerGroup;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.oc.block.DriverBlockEnvironments;
import pl.asie.computronics.oc.manual.ComputronicsPathProvider;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;

import static pl.asie.computronics.Computronics.camera;
import static pl.asie.computronics.Computronics.chatBox;
import static pl.asie.computronics.Computronics.colorfulLamp;
import static pl.asie.computronics.Computronics.radar;

/**
 * @author Vexatos
 */
public class IntegrationOpenComputers {

	private final Compat compat;
	private final Computronics computronics;
	private final Logger log;

	public static ItemOpenComputers itemOCParts;
	public static UpgradeRenderer upgradeRenderer;
	public static ColorfulUpgradeHandler colorfulUpgradeHandler;

	public IntegrationOpenComputers(Computronics computronics) {
		this.computronics = computronics;
		this.compat = computronics.compat;
		this.log = Computronics.log;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void preInit() {

		if(Config.OC_UPGRADE_CAMERA
			|| Config.OC_UPGRADE_CHATBOX
			|| Config.OC_UPGRADE_RADAR
			|| Config.OC_CARD_FX
			|| Config.OC_CARD_SPOOF
			|| Config.OC_CARD_SOUND
			|| Config.OC_CARD_BOOM
			|| Config.OC_UPGRADE_COLORFUL) {
			itemOCParts = new ItemOpenComputers();
			GameRegistry.registerItem(itemOCParts, "computronics.ocParts");
			Driver.add(itemOCParts);
		}

		// OpenComputers needs a hook in updateEntity in order to proprly register peripherals.
		// Fixes Iron Note Block, among others.
		// To ensure less TE ticks for those who don't use OC, we keep this tidbit around.
		Config.MUST_UPDATE_TILE_ENTITIES = true;

		if(Mods.hasVersion(Mods.Forestry, Mods.Versions.Forestry)) {
			if(Config.FORESTRY_BEES) {
				Computronics.forestry = new IntegrationForestry();
				Computronics.forestry.preInitOC();
			}
		} else {
			log.warn("Detected outdated version of Forestry, Forestry integration will not be enabled. Please update to Forestry " + Mods.Versions.Forestry + " or later.");
		}

		if(Mods.isLoaded(Mods.BuildCraftTransport) && Mods.isLoaded(Mods.BuildCraftCore) && Config.BUILDCRAFT_STATION) {
			Computronics.buildcraft = new IntegrationBuildCraft();
			Computronics.buildcraft.preInitOC();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void init() {

		Driver.add(new DriverBlockEnvironments());
		ComputronicsPathProvider.initialize();

		if(colorfulUpgradeHandler == null) {
			colorfulUpgradeHandler = new ColorfulUpgradeHandler();
		}

		MinecraftForge.EVENT_BUS.register(colorfulUpgradeHandler);

		if(Mods.isLoaded(Mods.RedLogic)) {
			if(compat.isCompatEnabled(Compat.RedLogic_Lamps)) {
				Driver.add(new DriverLamp.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.BetterStorage)) {
			if(compat.isCompatEnabled(Compat.BetterStorage_Crates)) {
				try {
					Class.forName("net.mcft.copy.betterstorage.api.ICrateStorage");
					log.info("Using old (pre-0.10) BetterStorage crate API!");
					Driver.add(new DriverCrateStorageOld());
				} catch(Exception e) {
					//NO-OP
				}

				try {
					Class.forName("net.mcft.copy.betterstorage.api.crate.ICrateStorage");
					log.info("Using new (0.10+) BetterStorage crate API!");
					Driver.add(new DriverCrateStorageNew());
				} catch(Exception e) {
					//NO-OP
				}
			}
		}
		if(Mods.isLoaded(Mods.StorageDrawers)) {
			if(compat.isCompatEnabled(Compat.StorageDrawers)) {
				Driver.add(new DriverDrawerGroup.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				Driver.add(new DriverSteamTransporter.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				Driver.add(new DriverChargeConductor.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.Railcraft)) {
			if(compat.isCompatEnabled(Compat.Railcraft_Routing)) {
				Driver.add(new DriverPoweredTrack.OCDriver());
				Driver.add(new DriverRoutingTrack.OCDriver());
				Driver.add(new DriverRoutingDetector.OCDriver());
				Driver.add(new DriverRoutingSwitch.OCDriver());
				Driver.add(new DriverElectricGrid.OCDriver());
				Driver.add(new DriverLimiterTrack.OCDriver());
				Driver.add(new DriverLocomotiveTrack.OCDriver());
				Driver.add(new DriverLauncherTrack.OCDriver());
				Driver.add(new DriverPrimingTrack.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.GregTech)) {
			if(compat.isCompatEnabled(Compat.GregTech_Machines)) {
				Driver.add(new DriverBaseMetaTileEntity());
				Driver.add(new DriverDeviceInformation());
				Driver.add(new DriverMachine());
				Driver.add(new DriverBatteryBuffer());
			}
			if(compat.isCompatEnabled(Compat.GregTech_DigitalChests)) {
				Driver.add(new DriverDigitalChest());
			}
		}
		if(Mods.isLoaded(Mods.ArmourersWorkshop)) {
			if(compat.isCompatEnabled(Compat.AW_Mannequins)) {
				Driver.add(new DriverMannequin.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.AE2)) {
			if(compat.isCompatEnabled(Compat.AE2_SpatialIO)) {
				Driver.add(new DriverSpatialIOPort.OCDriver());
			}
		}
		if(Mods.isLoaded(Mods.EnderIO)) {
			if(compat.isCompatEnabled(Compat.EnderIO)) {
				Driver.add(new DriverRedstoneControllable.OCDriver());
				Driver.add(new DriverIOConfigurable.OCDriver());
				Driver.add(new DriverHasExperience.OCDriver());
				Driver.add(new DriverPowerStorage.OCDriver());
				Driver.add(new DriverProgressTile.OCDriver());
				Driver.add(new DriverAbstractMachine.OCDriver());
				Driver.add(new DriverAbstractPoweredMachine.OCDriver());
				Driver.add(new DriverPowerMonitor.OCDriver());
				Driver.add(new DriverCapacitorBank.OCDriver());
				Driver.add(new DriverCapacitorBankOld.OCDriver());
				Driver.add(new DriverTransceiver.OCDriver());
				Driver.add(new DriverVacuumChest.OCDriver());
				Driver.add(new DriverWeatherObelisk.OCDriver());
				Driver.add(new DriverTelepad.OCDriver());
			}
		}

		if(Mods.API.hasAPI(Mods.API.DraconicEvolution)
			&& compat.isCompatEnabled(Compat.DraconicEvolution)) {
			Driver.add(new DriverExtendedRFStorage.OCDriver());
		}

		if(Mods.API.hasAPI(Mods.API.Mekanism_Energy)
			&& compat.isCompatEnabled(Compat.MekanismEnergy)) {
			Driver.add(new DriverStrictEnergyStorage.OCDriver());
		}

		if(Mods.hasVersion(Mods.API.BuildCraftTiles, Mods.Versions.BuildCraftTiles)) {
			if(compat.isCompatEnabled(Compat.BuildCraft_Drivers)) {
				Driver.add(new DriverHeatable.OCDriver());
			}
		}

		if(Mods.isLoaded(Mods.Flamingo)) {
			if(compat.isCompatEnabled(Compat.Flamingo)) {
				Driver.add(new DriverFlamingo.OCDriver());
			}
		}

		if(Computronics.forestry != null) {
			Computronics.forestry.initOC();
		}

		if(Computronics.buildcraft != null) {
			Computronics.buildcraft.initOC();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void postInit() {
		if(Config.OC_UPGRADE_CAMERA) {
			if(camera != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 0),
					"mcm", 'c',
					new ItemStack(camera, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 0),
					"m", "c", "m",
					'c', new ItemStack(camera, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
			} else {
				log.warn("Could not add Camera Upgrade Recipe because Radar is disabled in the config.");
			}
		}
		if(Config.OC_UPGRADE_CHATBOX) {
			if(chatBox != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 1),
					"mcm", 'c',
					new ItemStack(chatBox, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 1),
					"m", "c", "m",
					'c', new ItemStack(chatBox, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
			} else {
				log.warn("Could not add Chat Box Upgrade Recipe because Radar is disabled in the config.");
			}
		}
		if(Config.OC_UPGRADE_RADAR) {
			if(radar != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 2),
					"mcm", 'c',
					new ItemStack(radar, 1, 0),
					'm', li.cil.oc.api.Items.get("chip3").createItemStack(1));
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 2),
					"m", "c", "m",
					'c', new ItemStack(radar, 1, 0),
					'm', li.cil.oc.api.Items.get("chip3").createItemStack(1));
			} else {
				log.warn("Could not add Radar Upgrade Recipe because Radar is disabled in the config.");
			}
		}
		if(Config.OC_CARD_FX) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 3),
				"mf", " b",
				'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'f', Items.firework_charge,
				'b', li.cil.oc.api.Items.get("card").createItemStack(1));

		}
		if(Config.OC_CARD_SPOOF) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 4),
				"mfl", "pb ", "   ",
				'm', li.cil.oc.api.Items.get("ram2").createItemStack(1),
				'f', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'b', li.cil.oc.api.Items.get("lanCard").createItemStack(1),
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1),
				'l', Items.brick);
		}
		if(Config.OC_CARD_SOUND) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 5),
				" l ", "mb ", " f ",
				'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'f', Computronics.ironNote,
				'b', li.cil.oc.api.Items.get("card").createItemStack(1),
				'l', li.cil.oc.api.Items.get("cu").createItemStack(1));
		}
		if(Config.OC_CARD_BOOM) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 6),
				"mf", "fb",
				'm', li.cil.oc.api.Items.get("cu").createItemStack(1),
				'f', Blocks.tnt,
				'b', li.cil.oc.api.Items.get("redstoneCard1").createItemStack(1));

		}
		if(Config.OC_UPGRADE_COLORFUL) {
			if(colorfulLamp != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 7),
					" f ", "mcm", " f ", 'c',
					new ItemStack(colorfulLamp, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
					'f', li.cil.oc.api.Items.get("chamelium").createItemStack(1));
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 7),
					" m ", "fcf", " m ",
					'c', new ItemStack(colorfulLamp, 1, 0),
					'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
					'f', li.cil.oc.api.Items.get("chamelium").createItemStack(1));
			} else {
				log.warn("Could not add Colorful Upgrade Recipe because Colorful Lamp is disabled in the config.");
			}
		}
		if(Computronics.buildcraft != null) {
			Computronics.buildcraft.postInitOC();
		}
	}

	public void remap(FMLMissingMappingsEvent event) {
		for(FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
			if(mapping.name.equals("computronics:computronics.robotUpgrade")) {
				if(mapping.type == GameRegistry.Type.ITEM) {
					mapping.remap(itemOCParts);
				}
			}
		}
	}
}
