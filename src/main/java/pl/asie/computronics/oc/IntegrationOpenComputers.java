package pl.asie.computronics.oc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import li.cil.oc.api.IMC;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.fs.FileSystem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.SoundCardPlaybackManager;
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
import pl.asie.computronics.integration.gregtech.gregtech5.DriverBaseMetaTileEntity;
import pl.asie.computronics.integration.gregtech.gregtech5.DriverBatteryBuffer;
import pl.asie.computronics.integration.gregtech.gregtech5.DriverDeviceInformation;
import pl.asie.computronics.integration.gregtech.gregtech5.DriverDigitalChest;
import pl.asie.computronics.integration.gregtech.gregtech5.DriverMachine;
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
import pl.asie.computronics.item.ItemOCSpecialParts;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.oc.block.ComputronicsBlockEnvironmentProvider;
import pl.asie.computronics.oc.client.RackMountableRenderer;
import pl.asie.computronics.oc.client.UpgradeRenderer;
import pl.asie.computronics.oc.driver.DriverBoardBoom;
import pl.asie.computronics.oc.driver.DriverCardSound;
import pl.asie.computronics.oc.driver.DriverMagicalMemory;
import pl.asie.computronics.oc.manual.ComputronicsPathProvider;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;
import pl.asie.lib.util.ColorUtils.Color;

import java.util.concurrent.Callable;

import static pl.asie.computronics.Computronics.camera;
import static pl.asie.computronics.Computronics.chatBox;
import static pl.asie.computronics.Computronics.colorfulLamp;
import static pl.asie.computronics.Computronics.ironNote;
import static pl.asie.computronics.Computronics.proxy;
import static pl.asie.computronics.Computronics.radar;
import static pl.asie.computronics.Computronics.speaker;
import static pl.asie.computronics.Computronics.speechBox;

/**
 * @author Vexatos
 */
public class IntegrationOpenComputers {

	private final Compat compat;
	private final Computronics computronics;
	private final Logger log;

	public static ItemOpenComputers itemOCParts;
	public static ItemOCSpecialParts itemOCSpecialParts;
	public static UpgradeRenderer upgradeRenderer;
	public static RackMountableRenderer mountableRenderer;
	public static ColorfulUpgradeHandler colorfulUpgradeHandler;
	public static DriverBoardBoom.BoomHandler boomBoardHandler;
	public SoundCardPlaybackManager audio;
	public int managerId;

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
			|| Config.OC_CARD_BEEP
			|| Config.OC_CARD_BOOM
			|| Config.OC_UPGRADE_COLORFUL
			|| Config.OC_CARD_NOISE
			|| Config.OC_CARD_SOUND
			|| Config.OC_BOARD_LIGHT
			|| Config.OC_BOARD_BOOM
			|| Config.OC_BOARD_CAPACITOR
			|| Config.OC_BOARD_SWITCH
			|| Config.OC_UPGRADE_SPEECH) {
			itemOCParts = new ItemOpenComputers();
			GameRegistry.registerItem(itemOCParts, "computronics.ocParts");
			Driver.add((Item) itemOCParts);
			Driver.add((EnvironmentProvider) itemOCParts);
		}

		if(Config.OC_MAGICAL_MEMORY) {
			itemOCSpecialParts = new ItemOCSpecialParts();
			GameRegistry.registerItem(itemOCSpecialParts, "computronics.ocSpecialParts");
			Driver.add(itemOCSpecialParts);
			if(Config.OC_MAGICAL_MEMORY) {
				Driver.add(new DriverMagicalMemory());
			}
		}

		// OpenComputers needs a hook in updateEntity in order to proprly register peripherals.
		// Fixes Iron Note Block, among others.
		// To ensure less TE ticks for those who don't use OC, we keep this tidbit around.
		Config.MUST_UPDATE_TILE_ENTITIES = true;

		if(Config.OC_CARD_SOUND) {
			audio = new SoundCardPlaybackManager(proxy.isClient());

			managerId = AudioPacketRegistry.INSTANCE.registerManager(audio);
		}

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

	private static class ReadOnlyFS implements Callable<FileSystem> {

		private final String name;

		ReadOnlyFS(String name) {
			this.name = name;
		}

		@Override
		@Optional.Method(modid = Mods.OpenComputers)
		public FileSystem call() throws Exception {
			return li.cil.oc.api.FileSystem.asReadOnly(li.cil.oc.api.FileSystem.fromClass(Computronics.class, Mods.Computronics, "loot/" + name));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void init() {

		Driver.add(new ComputronicsBlockEnvironmentProvider());
		ComputronicsPathProvider.initialize();

		if(Computronics.tapeReader != null) {
			li.cil.oc.api.Items.registerFloppy("tape", Color.White.ordinal(), new ReadOnlyFS("tape"), true);
			IMC.registerProgramDiskLabel("tape", "tape", "Lua 5.2", "Lua 5.3", "LuaJ");
		}

		if(Config.OC_CARD_BOOM || Config.OC_BOARD_BOOM) {
			li.cil.oc.api.Items.registerFloppy("explode", Color.Red.ordinal(), new ReadOnlyFS("explode"), true);
			IMC.registerProgramDiskLabel("explode", "explode", "Lua 5.2", "Lua 5.3", "LuaJ");
		}

		if(colorfulUpgradeHandler == null && Config.OC_UPGRADE_COLORFUL) {
			colorfulUpgradeHandler = new ColorfulUpgradeHandler();
			MinecraftForge.EVENT_BUS.register(colorfulUpgradeHandler);
		}

		if(boomBoardHandler == null && Config.OC_BOARD_BOOM) {
			boomBoardHandler = new DriverBoardBoom.BoomHandler();
			FMLCommonHandler.instance().bus().register(boomBoardHandler);
		}

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
		if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5)) {
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

		if(Config.OC_CARD_SOUND && proxy.isClient()) {
			MinecraftForge.EVENT_BUS.register(new DriverCardSound.SyncHandler());
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void postInit() {
		if(Config.OC_UPGRADE_CAMERA) {
			if(camera != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 0),
					"idi", "mcm", "ibi",
					'c', camera,
					'd', "oc:materialTransistor",
					'm', "oc:circuitChip2",
					'i', "ingotIron",
					'b', "oc:materialCircuitBoardPrinted"
				);
			} else {
				log.warn("Could not add Camera Upgrade Recipe because Camera is disabled in the config.");
			}
		}
		if(Config.OC_UPGRADE_CHATBOX) {
			if(chatBox != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 1),
					"idi", "mcm", "ibi",
					'c', new ItemStack(chatBox, 1, 0),
					'd', "oc:materialTransistor",
					'm', "oc:circuitChip2",
					'i', "ingotIron",
					'b', "oc:materialCircuitBoardPrinted"
				);
			} else {
				log.warn("Could not add Chat Box Upgrade Recipe because Chat Box is disabled in the config.");
			}
		}
		if(Config.OC_UPGRADE_RADAR) {
			if(radar != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 2),
					"idi", "mcm", "ibi",
					'c', radar,
					'd', "oc:materialInterweb",
					'm', "oc:circuitChip3",
					'i', "ingotGold",
					'b', "oc:materialCircuitBoardPrinted"
				);
			} else {
				log.warn("Could not add Radar Upgrade Recipe because Radar is disabled in the config.");
			}
		}
		if(Config.OC_CARD_FX) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 3),
				"mf", " b",
				'm', "oc:circuitChip2",
				'f', Items.firework_charge,
				'b', "oc:materialCard");

		}
		if(Config.OC_CARD_SPOOF) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 4),
				"mfl", "pb ", "   ",
				'm', "oc:ram2",
				'f', "oc:circuitChip2",
				'b', "oc:lanCard",
				'p', "oc:materialCircuitBoardPrinted",
				'l', Items.brick);
		}
		if(Config.OC_CARD_BEEP) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 5),
				" l ", "mb ", " f ",
				'm', "oc:circuitChip1",
				'f', speaker != null ? speaker : ironNote != null ? ironNote : Blocks.noteblock,
				'b', "oc:materialCard",
				'l', "oc:materialCU");
		}
		if(Config.OC_CARD_BOOM) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 6),
				"mf", "fb",
				'm', "oc:materialCU",
				'f', Blocks.tnt,
				'b', "oc:redstoneCard1");

		}
		if(Config.OC_UPGRADE_COLORFUL) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 7),
				"fdf", "mcm", "fbf",
				'c', colorfulLamp != null ? colorfulLamp : "glowstone",
				'd', "oc:materialTransistor",
				'm', "oc:circuitChip2",
				'f', "oc:chamelium",
				'b', "oc:materialCircuitBoardPrinted"
			);
		}
		if(Config.OC_CARD_NOISE) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 8),
				" l ", "mbn", " f ",
				'm', "oc:ram1",
				'f', "oc:materialALU",
				'b', Config.OC_CARD_BEEP ? new ItemStack(itemOCParts, 1, 5) : speaker != null ? speaker : ironNote != null ? ironNote : Blocks.noteblock,
				'l', "oc:circuitChip2",
				'n', "gemQuartz");
		}
		if(Config.OC_CARD_SOUND) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 9),
				" l ", "mb ", " f ",
				'm', "oc:ram5",
				'f', "oc:cpu1",
				'b', Config.OC_CARD_NOISE ? new ItemStack(itemOCParts, 1, 8) :
					Config.OC_CARD_BEEP ? new ItemStack(itemOCParts, 1, 5) : speaker != null ? speaker : ironNote != null ? ironNote : Blocks.noteblock,
				'l', "oc:circuitChip2");
		}
		if(Config.OC_BOARD_LIGHT) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 10),
				"oso", "gcg", "opo",
				's', "paneGlassColorless",
				'g', "oc:circuitChip1",
				'c', colorfulLamp != null ? colorfulLamp : "glowstone",
				'o', "obsidian",
				'p', "oc:materialCircuitBoardPrinted"
			);
		}
		if(Config.OC_BOARD_BOOM) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 11),
				"lsl", "gcg", "opo",
				's', "oc:circuitChip1",
				'g', Config.OC_CARD_BOOM ? new ItemStack(itemOCParts, 1, 6) : Items.blaze_powder,
				'c', Blocks.tnt,
				'o', "obsidian",
				'l', Items.gunpowder,
				'p', "oc:materialCircuitBoardPrinted"
			);
		}
		if(Config.OC_BOARD_CAPACITOR) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 12),
				"lsl", "gcg", "opo",
				's', "oc:circuitChip1",
				'g', "nuggetGold",
				'c', "oc:capacitor",
				'o', "obsidian",
				'l', "ingotIron",
				'p', "oc:materialCircuitBoardPrinted"
			);
		}
		if(Config.OC_BOARD_SWITCH) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 13),
				"oso", "gcg", "opo",
				's', "paneGlassColorless",
				'g', "oc:circuitChip1",
				'c', "oc:materialButtonGroup",
				'o', "obsidian",
				'p', "oc:materialCircuitBoardPrinted"
			);
		}
		if(Config.OC_UPGRADE_SPEECH) {
			if(speechBox != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 14),
					"idi", "mcm", "ibi",
					'c', speechBox,
					'd', "oc:materialTransistor",
					'm', "oc:circuitChip2",
					'i', "ingotIron",
					'b', "oc:materialCircuitBoardPrinted"
				);
			}
		}
		if(Computronics.buildcraft != null) {
			Computronics.buildcraft.postInitOC();
		}
	}

	public void onServerStop(FMLServerStoppedEvent event) {
		if(Config.OC_CARD_SOUND && this.audio != null) {
			this.audio.removeAll();
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
