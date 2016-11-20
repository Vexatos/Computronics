package pl.asie.computronics.oc;

import li.cil.oc.api.Driver;
import li.cil.oc.api.IMC;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.fs.FileSystem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.SoundCardPlaybackManager;
import pl.asie.computronics.integration.flamingo.DriverFlamingo;
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

import java.util.concurrent.Callable;

import static pl.asie.computronics.Computronics.camera;
import static pl.asie.computronics.Computronics.chatBox;
import static pl.asie.computronics.Computronics.colorfulLamp;
import static pl.asie.computronics.Computronics.ironNote;
import static pl.asie.computronics.Computronics.proxy;
import static pl.asie.computronics.Computronics.radar;
import static pl.asie.computronics.Computronics.speaker;

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
			|| Config.OC_BOARD_SWITCH) {
			itemOCParts = new ItemOpenComputers();
			Computronics.instance.registerItem(itemOCParts, "oc_parts");
			itemOCParts.registerItemModels();
			Driver.add((Item) itemOCParts);
			Driver.add((EnvironmentProvider) itemOCParts);
		}

		if(Config.OC_MAGICAL_MEMORY) {
			itemOCSpecialParts = new ItemOCSpecialParts();
			Computronics.instance.registerItem(itemOCSpecialParts, "oc_special_parts");
			itemOCSpecialParts.registerItemModels();
			Driver.add(itemOCSpecialParts);
			if(Config.OC_MAGICAL_MEMORY) {
				Driver.add(new DriverMagicalMemory());
			}
		}

		// OpenComputers needs a hook in updateEntity in order to proprly register peripherals.
		// Fixes Iron Note Block, among others.
		// To ensure less TE ticks for those who don't use OC, we keep this tidbit around.
		//Config.MUST_UPDATE_TILE_ENTITIES = true;

		if(Config.OC_CARD_SOUND) {
			audio = new SoundCardPlaybackManager(proxy.isClient());

			managerId = AudioPacketRegistry.INSTANCE.registerManager(audio);
		}

		/*if(Mods.isLoaded(Mods.Forestry)) {
			if(Config.FORESTRY_BEES) {
				Computronics.forestry = new IntegrationForestry();
				Computronics.forestry.preInitOC();
			}
		}*/

		/*if(Mods.isLoaded(Mods.BuildCraftTransport) && Mods.isLoaded(Mods.BuildCraftCore) && Config.BUILDCRAFT_STATION) { // TODO BuildCraft Drone Docking
			Computronics.buildcraft = new IntegrationBuildCraft();
			Computronics.buildcraft.preInitOC();
		}*/
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
			li.cil.oc.api.Items.registerFloppy("tape", EnumDyeColor.WHITE, new ReadOnlyFS("tape"));
			IMC.registerProgramDiskLabel("tape", "tape", "Lua 5.2", "Lua 5.3", "LuaJ");
		}

		if(Config.OC_CARD_BOOM || Config.OC_BOARD_BOOM) {
			li.cil.oc.api.Items.registerFloppy("explode", EnumDyeColor.RED, new ReadOnlyFS("explode"));
			IMC.registerProgramDiskLabel("explode", "explode", "Lua 5.2", "Lua 5.3", "LuaJ");
		}

		if(colorfulUpgradeHandler == null && Config.OC_UPGRADE_COLORFUL) {
			colorfulUpgradeHandler = new ColorfulUpgradeHandler();
			MinecraftForge.EVENT_BUS.register(colorfulUpgradeHandler);
		}

		if(boomBoardHandler == null && Config.OC_BOARD_BOOM) {
			boomBoardHandler = new DriverBoardBoom.BoomHandler();
			MinecraftForge.EVENT_BUS.register(boomBoardHandler);
		}

		/*if(Mods.isLoaded(Mods.RedLogic)) {
			if(compat.isCompatEnabled(Compat.RedLogic_Lamps)) {
				Driver.add(new DriverLamp.OCDriver());
			}
		}*//*
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
		}*/
		/*if(Mods.isLoaded(Mods.StorageDrawers)) {
			if(compat.isCompatEnabled(Compat.StorageDrawers)) {
				Driver.add(new DriverDrawerGroup.OCDriver());
			}
		}*//*
		if(Mods.isLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				Driver.add(new DriverSteamTransporter.OCDriver());
			}
		}*//*
		if(Mods.isLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				Driver.add(new DriverChargeConductor.OCDriver());
			}
		}*/
		/*if(Mods.isLoaded(Mods.Railcraft)) {
			if(compat.isCompatEnabled(Compat.Railcraft_Routing)) {
				Driver.add(new DriverPoweredTrack.OCDriver());
				Driver.add(new DriverRoutingTrack.OCDriver());
				Driver.add(new DriverRoutingDetector.OCDriver());
				Driver.add(new DriverRoutingSwitch.OCDriver());
				Driver.add(new DriverElectricGrid.OCDriver());
				Driver.add(new DriverThrottleTrack.OCDriver());
				Driver.add(new DriverLocomotiveTrack.OCDriver());
				Driver.add(new DriverLauncherTrack.OCDriver());
				Driver.add(new DriverPrimingTrack.OCDriver());
			}
		}*//*
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
		}*//*
		if(Mods.isLoaded(Mods.ArmourersWorkshop)) {
			if(compat.isCompatEnabled(Compat.AW_Mannequins)) {
				Driver.add(new DriverMannequin.OCDriver());
			}
		}*//*
		if(Mods.isLoaded(Mods.AE2)) {
			if(compat.isCompatEnabled(Compat.AE2_SpatialIO)) {
				Driver.add(new DriverSpatialIOPort.OCDriver());
			}
		}*/
		/*if(Mods.isLoaded(Mods.EnderIO)) {
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
				Driver.add(new DriverTransceiver.OCDriver());
				Driver.add(new DriverVacuumChest.OCDriver());
				Driver.add(new DriverWeatherObelisk.OCDriver());
				Driver.add(new DriverTelepad.OCDriver());
			}
		}*//*

		if(Mods.API.hasAPI(Mods.API.DraconicEvolution)
			&& compat.isCompatEnabled(Compat.DraconicEvolution)) {
			Driver.add(new DriverExtendedRFStorage.OCDriver());
		}*//*

		if(Mods.API.hasAPI(Mods.API.Mekanism_Energy)
			&& compat.isCompatEnabled(Compat.MekanismEnergy)) {
			Driver.add(new DriverStrictEnergyStorage.OCDriver());
		}*/

		/*if(Mods.hasVersion(Mods.API.BuildCraftTiles, Mods.Versions.BuildCraftTiles)) {
			if(compat.isCompatEnabled(Compat.BuildCraft_Drivers)) {
				Driver.add(new DriverHeatable.OCDriver());
			}
		}*/

		if(Mods.isLoaded(Mods.Flamingo)) {
			if(compat.isCompatEnabled(Compat.Flamingo)) {
				Driver.add(new DriverFlamingo.OCDriver());
			}
		}

		/*if(Computronics.forestry != null) {
			Computronics.forestry.initOC();
		}*/

		/*if(Computronics.buildcraft != null) { TODO BuildCraft Drone Docking
			Computronics.buildcraft.initOC();
		}*/

		if(Config.OC_CARD_SOUND && proxy.isClient()) {
			MinecraftForge.EVENT_BUS.register(new DriverCardSound.SyncHandler());
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
				'f', Items.FIRE_CHARGE,
				'b', li.cil.oc.api.Items.get("card").createItemStack(1));

		}
		if(Config.OC_CARD_SPOOF) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 4),
				"mfl", "pb ", "   ",
				'm', li.cil.oc.api.Items.get("ram2").createItemStack(1),
				'f', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'b', li.cil.oc.api.Items.get("lanCard").createItemStack(1),
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1),
				'l', Items.BRICK);
		}
		if(Config.OC_CARD_BEEP) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 5),
				" l ", "mb ", " f ",
				'm', li.cil.oc.api.Items.get("chip1").createItemStack(1),
				'f', speaker != null ? speaker : ironNote != null ? ironNote : Blocks.NOTEBLOCK,
				'b', li.cil.oc.api.Items.get("card").createItemStack(1),
				'l', li.cil.oc.api.Items.get("cu").createItemStack(1));
		}
		if(Config.OC_CARD_BOOM) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 6),
				"mf", "fb",
				'm', li.cil.oc.api.Items.get("cu").createItemStack(1),
				'f', Blocks.TNT,
				'b', li.cil.oc.api.Items.get("redstoneCard1").createItemStack(1));

		}
		if(Config.OC_UPGRADE_COLORFUL) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 7),
				" f ", "mcm", " f ",
				'c', colorfulLamp != null ? colorfulLamp : "glowstone",
				'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'f', li.cil.oc.api.Items.get("chamelium").createItemStack(1));
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 7),
				" m ", "fcf", " m ",
				'c', colorfulLamp != null ? colorfulLamp : "glowstone",
				'm', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'f', li.cil.oc.api.Items.get("chamelium").createItemStack(1));
		}
		if(Config.OC_CARD_NOISE) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 8),
				" l ", "mbn", " f ",
				'm', li.cil.oc.api.Items.get("ram1").createItemStack(1),
				'f', li.cil.oc.api.Items.get("alu").createItemStack(1),
				'b', Config.OC_CARD_BEEP ? new ItemStack(itemOCParts, 1, 5) : speaker != null ? speaker : ironNote != null ? ironNote : Blocks.NOTEBLOCK,
				'l', li.cil.oc.api.Items.get("chip2").createItemStack(1),
				'n', "gemQuartz");
		}
		if(Config.OC_CARD_SOUND) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 9),
				" l ", "mb ", " f ",
				'm', li.cil.oc.api.Items.get("ram5").createItemStack(1),
				'f', li.cil.oc.api.Items.get("cpu1").createItemStack(1),
				'b', Config.OC_CARD_NOISE ? new ItemStack(itemOCParts, 1, 8) :
					Config.OC_CARD_BEEP ? new ItemStack(itemOCParts, 1, 5) : speaker != null ? speaker : ironNote != null ? ironNote : Blocks.NOTEBLOCK,
				'l', li.cil.oc.api.Items.get("chip2").createItemStack(1));
		}
		if(Config.OC_BOARD_LIGHT) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 10),
				"oso", "gcg", "opo",
				's', "paneGlassColorless",
				'g', li.cil.oc.api.Items.get("chip1").createItemStack(1),
				'c', colorfulLamp != null ? colorfulLamp : "glowstone",
				'o', "obsidian",
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1)
			);
		}
		if(Config.OC_BOARD_BOOM) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 11),
				"lsl", "gcg", "opo",
				's', li.cil.oc.api.Items.get("chip1").createItemStack(1),
				'g', Config.OC_CARD_BOOM ? new ItemStack(itemOCParts, 1, 6) : Items.BLAZE_POWDER,
				'c', Blocks.TNT,
				'o', "obsidian",
				'l', "gunpowder",
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1)
			);
		}
		if(Config.OC_BOARD_CAPACITOR) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 12),
				"lsl", "gcg", "opo",
				's', li.cil.oc.api.Items.get("chip1").createItemStack(1),
				'g', "nuggetGold",
				'c', li.cil.oc.api.Items.get("capacitor").createItemStack(1),
				'o', "obsidian",
				'l', "ingotIron",
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1)
			);
		}
		if(Config.OC_BOARD_SWITCH) {
			RecipeUtils.addShapedRecipe(new ItemStack(itemOCParts, 1, 13),
				"oso", "gcg", "opo",
				's', "paneGlassColorless",
				'g', li.cil.oc.api.Items.get("chip1").createItemStack(1),
				'c', li.cil.oc.api.Items.get("buttonGroup").createItemStack(1),
				'o', "obsidian",
				'p', li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1)
			);
		}
		/*if(Computronics.buildcraft != null) { TODO BuildCraft Drone Docking
			Computronics.buildcraft.postInitOC();
		}*/
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void scheduleForNetworkJoin(final TileEntity tile) {
		if(!tile.isInvalid() && !tile.getWorld().isRemote) {
			Computronics.serverTickHandler.schedule(new Runnable() {
				@Override
				public void run() {
					Network.joinOrCreateNetwork(tile);
				}
			});
		}
	}

	public void onServerStop(FMLServerStoppedEvent event) {
		if(Config.OC_CARD_SOUND && this.audio != null) {
			this.audio.removeAll();
		}
	}

	/*public void remap(FMLMissingMappingsEvent event) {
		for(FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
			if(mapping.name.equals("computronics:computronics.robotUpgrade")) {
				if(mapping.type == GameRegistry.Type.ITEM) {
					mapping.remap(itemOCParts);
				}
			}
		}
	}*/
}
