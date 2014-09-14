package pl.asie.computronics;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockCipher;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockEEPROMReader;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockLocomotiveRelay;
import pl.asie.computronics.block.BlockRadar;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.cc.CCPeripheralProvider;
import pl.asie.computronics.cc.MusicalTurtleUpgrade;
import pl.asie.computronics.cc.ParticleTurtleUpgrade;
import pl.asie.computronics.cc.RadarTurtleUpgrade;
import pl.asie.computronics.cc.SpeakingTurtleUpgrade;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.integration.ModRecipes;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageNew;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageOld;
import pl.asie.computronics.integration.factorization.ChargeConductorPeripheral;
import pl.asie.computronics.integration.factorization.DriverChargeConductor;
import pl.asie.computronics.integration.fsp.DriverSteamTransporter;
import pl.asie.computronics.integration.fsp.SteamTransporterPeripheral;
import pl.asie.computronics.integration.gregtech.DriverBaseMetaTileEntity;
import pl.asie.computronics.integration.gregtech.DriverBatteryBuffer;
import pl.asie.computronics.integration.gregtech.DriverDeviceInformation;
import pl.asie.computronics.integration.gregtech.DriverDigitalChest;
import pl.asie.computronics.integration.gregtech.DriverMachine;
import pl.asie.computronics.integration.mfr.DeepStorageUnitPeripheral;
import pl.asie.computronics.integration.mfr.DriverDeepStorageUnit;
import pl.asie.computronics.integration.railcraft.DriverReceiverBox;
import pl.asie.computronics.integration.railcraft.DriverRoutingDetector;
import pl.asie.computronics.integration.railcraft.DriverRoutingSwitch;
import pl.asie.computronics.integration.railcraft.DriverRoutingTrack;
import pl.asie.computronics.integration.railcraft.ReceiverBoxPeripheral;
import pl.asie.computronics.integration.railcraft.RoutingDetectorPeripheral;
import pl.asie.computronics.integration.railcraft.RoutingSwitchPeripheral;
import pl.asie.computronics.integration.railcraft.RoutingTrackPeripheral;
import pl.asie.computronics.integration.redlogic.CCBundledRedstoneProviderRedLogic;
import pl.asie.computronics.integration.redlogic.DriverLamp;
import pl.asie.computronics.integration.redlogic.LampPeripheral;
import pl.asie.computronics.item.ItemBlockChatBox;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.item.ItemRelaySensor;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.StorageManager;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.tile.TileEEPROMReader;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileLocomotiveRelay;
import pl.asie.computronics.tile.TileRadar;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.util.achievements.ComputronicsAchievements;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.item.ItemMultiple;
import pl.asie.lib.network.PacketHandler;
import pl.asie.lib.util.EnergyConverter;

import java.util.Random;

@Mod(modid = Mods.Computronics, name = Mods.Computronics_NAME, version = "1.0.1", dependencies = "required-after:asielib;after:ComputerCraft;after:OpenComputers;after:OpenComputers|Core;after:MineFactoryReloaded;after:RedLogic;after:ProjRed|Core;after:nedocomputers;after:Railcraft")
public class Computronics {
	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;

	public static FMLEventChannel channel;

	@Instance(value = Mods.Computronics)
	public static Computronics instance;
	public static StorageManager storage;
	public static GuiHandler gui;
	public static PacketHandler packet;
	public DFPWMPlaybackManager audio;

	public static int CHATBOX_DISTANCE = 40;
	public static int CAMERA_DISTANCE = 32;
	public static int TAPEDRIVE_DISTANCE = 24;
	public static int TAPEDRIVE_BUFFER_MS = 750;
	public static int RADAR_RANGE = 8;
	public static boolean RADAR_ONLY_DISTANCE = false;
	public static boolean CIPHER_CAN_LOCK = true;
	public static double RADAR_ENERGY_COST_RF = 5.0;
	public static double RADAR_CC_TIME = 0.5;
	public static double FX_ENERGY_COST = 0.5;
	public static String CHATBOX_PREFIX = "ChatBox";
	public static double LOCOMOTIVE_RELAY_RANGE = 128.0;
	public static boolean GREGTECH_RECIPES = false;

	public static String TAPE_LENGTHS;
	public static boolean REDSTONE_REFRESH, CHATBOX_CREATIVE;

	@SidedProxy(clientSide = "pl.asie.computronics.ClientProxy", serverSide = "pl.asie.computronics.CommonProxy")
	public static CommonProxy proxy;

	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockCipher cipher;
	public static BlockRadar radar;
	public static BlockEEPROMReader nc_eepromreader;
	public static BlockColorfulLamp colorfulLamp;
	public static BlockLocomotiveRelay locomotiveRelay;

	public static ItemTape itemTape;
	public static ItemMultiple itemParts;
	public static ItemMultiple itemPartsGreg;
	public static ItemOpenComputers itemRobotUpgrade;
	public static ItemRelaySensor relaySensor;

	public static boolean MUST_UPDATE_TILE_ENTITIES = false;
	public ComputronicsAchievements achievements;

	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
		public Item getTabIconItem() {
			return itemTape;
		}
	};

	private boolean isEnabled(String name, boolean def) {
		return config.get("enable", name, def).getBoolean(def);
	}

	private void registerBlockWithTileEntity(Block block, Class<? extends TileEntity> tile, String name) {
		registerBlockWithTileEntity(block, ItemBlock.class, tile, name);
	}

	private void registerBlockWithTileEntity(Block block, Class<? extends ItemBlock> itemBlock, Class<? extends TileEntity> tile, String name) {
		GameRegistry.registerBlock(block, itemBlock, name);
		GameRegistry.registerTileEntity(tile, name);
		//System.out.println("Registering " + name + " as TE " + tile.getCanonicalName());
		FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", tile.getCanonicalName());
		FMLInterModComms.sendMessage(Mods.AE2, "movabletile", tile.getCanonicalName());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = LogManager.getLogger(Mods.Computronics);

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		audio = new DFPWMPlaybackManager(proxy.isClient());
		packet = new PacketHandler(Mods.Computronics, new NetworkHandlerClient(), new NetworkHandlerServer());

		// Configs

		// Camera
		CAMERA_DISTANCE = config.getInt("maxDistance", "camera", 32, 16, 256, "The maximum camera distance, in blocks.");

		// Chat Box
		CHATBOX_CREATIVE = config.getBoolean("enableCreative", "chatbox", true, "Enable Creative Chat Boxes.");
		CHATBOX_DISTANCE = config.getInt("maxDistance", "chatbox", 40, 4, Integer.MAX_VALUE, "The maximum chat box distance, in blocks.");
		CHATBOX_PREFIX = config.getString("prefix", "chatbox", "ChatBox", "The Chat Box's default prefix.");

		// Cipher Block
		CIPHER_CAN_LOCK = config.getBoolean("canLock", "cipherblock", true, "Decides whether Cipher Blocks can or cannot be locked.");

		// Particle Card
		if(Loader.isModLoaded("OpenComputers")) {
			FX_ENERGY_COST = EnergyConverter.convertEnergy(
				config.getFloat("ocParticleCardCostPerParticle", "power", 0.5f, 0.0f, 10000.0f, "How much energy, in RF, 1 particle emission should take."),
				"RF", "OC");
		}

		// Radar
		RADAR_RANGE = config.getInt("maxRange", "radar", 8, 0, 256, "The maximum range of the Radar.");
		RADAR_ONLY_DISTANCE = config.getBoolean("onlyOutputDistance", "radar", false, "Stop Radars from outputting X/Y/Z coordinates and instead only output the distance from an entity.");

		// Tape Drive
		TAPEDRIVE_BUFFER_MS = config.getInt("audioPreloadMs", "tapedrive", 750, 500, 10000, "The amount of time (in milliseconds) used for pre-buffering the tape for audio playback. If you get audio playback glitches in SMP/your TPS is under 20, RAISE THIS VALUE!");
		TAPEDRIVE_DISTANCE = config.getInt("hearingDistance", "tapedrive", 24, 0, 64, "The distance up to which Tape Drives can be heard.");
		TAPE_LENGTHS = config.get("tapeLengths", "tapedrive", "4,8,16,32,64,2,6,16,128,128", "The lengths of the computronics tapes. [default: 4,8,16,32,64,2,6,16,128,128]").getString();

		// General
		REDSTONE_REFRESH = config.getBoolean("enableTickingRedstoneSupport", "general", true, "Set whether some machines should stop being tickless in exchange for redstone output support.");

		// Power
		RADAR_ENERGY_COST_RF = config.getFloat("radarCostPerBlock", "power", 500.0f, 0.0f, 10000.0f, "How much energy, in RF, each 1-block distance takes by OpenComputers radars.");

		// Railcraft integration
		LOCOMOTIVE_RELAY_RANGE = (double) config.getInt("locomotiveRelayRange", "railcraft", 128, 0, 512, "The range of Locomotive Relays in Blocks.");

		// GregTech recipe mode
		GREGTECH_RECIPES = config.getBoolean("gtRecipeMode", "gregtech", Loader.isModLoaded(Mods.GregTech), "Set this to true to enable GregTech-style recipes") && Loader.isModLoaded(Mods.GregTech);

		if(isEnabled("ironNoteBlock", true)) {
			ironNote = new BlockIronNote();
			registerBlockWithTileEntity(ironNote, TileIronNote.class, "computronics.ironNoteBlock");
		}

		if(isEnabled("tape", true)) {
			tapeReader = new BlockTapeReader();
			registerBlockWithTileEntity(tapeReader, TileTapeDrive.class, "computronics.tapeReader");
		}

		if(isEnabled("camera", true)) {
			camera = new BlockCamera();
			registerBlockWithTileEntity(camera, TileCamera.class, "computronics.camera");
		}

		if(isEnabled("chatBox", true)) {
			chatBox = new BlockChatBox();
			registerBlockWithTileEntity(chatBox, ItemBlockChatBox.class, TileChatBox.class, "computronics.chatBox");
		}

		if(isEnabled("cipher", true)) {
			cipher = new BlockCipher();
			registerBlockWithTileEntity(cipher, TileCipherBlock.class, "computronics.cipher");
		}

		if(isEnabled("radar", true)) {
			radar = new BlockRadar();
			registerBlockWithTileEntity(radar, TileRadar.class, "computronics.radar");
		}

		if(isEnabled("lamp", true)) {
			colorfulLamp = new BlockColorfulLamp();
			registerBlockWithTileEntity(colorfulLamp, TileColorfulLamp.class, "computronics.colorfulLamp");
		}

		if(Loader.isModLoaded(Mods.NedoComputers) && isEnabled("eepromReader", true)) {
			nc_eepromreader = new BlockEEPROMReader();
			registerBlockWithTileEntity(nc_eepromreader, TileEEPROMReader.class, "computronics.eepromReader");
		}

		if(isEnabled("tape", true)) {
			itemTape = new ItemTape(TAPE_LENGTHS);
			GameRegistry.registerItem(itemTape, "computronics.tape");

			if(Loader.isModLoaded(Mods.GregTech)) {
				itemPartsGreg = new ItemMultiple(Mods.Computronics, new String[] { "reelChromoxide" });
				itemPartsGreg.setCreativeTab(tab);
				GameRegistry.registerItem(itemPartsGreg, "computronics.gt_parts");
				proxy.registerEntities();
			}
		}

		itemParts = new ItemMultiple(Mods.Computronics, new String[] { "part_tape_track" });
		itemParts.setCreativeTab(tab);
		GameRegistry.registerItem(itemParts, "computronics.parts");

		if(Loader.isModLoaded(Mods.Railcraft) && isEnabled("railcraftLocomotiveRelay", true)) {
			locomotiveRelay = new BlockLocomotiveRelay();
			GameRegistry.registerBlock(locomotiveRelay, "computronics.locomotiveRelay");
			GameRegistry.registerTileEntity(TileLocomotiveRelay.class, "computronics.locomotiveRelay");

			relaySensor = new ItemRelaySensor();
			GameRegistry.registerItem(relaySensor, "computronics.relaySensor");
		}

		if(Loader.isModLoaded(Mods.OpenComputers)) {
			preInitOC();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void preInitOC() {
		if(isEnabled("ocRobotUpgrades", true)) {
			itemRobotUpgrade = new ItemOpenComputers();
			GameRegistry.registerItem(itemRobotUpgrade, "computronics.robotUpgrade");
			li.cil.oc.api.Driver.add(itemRobotUpgrade);
		}

		// OpenComputers needs a hook in updateEntity in order to proprly register peripherals.
		// Fixes Iron Note Block, among others.
		// To ensure less TE ticks for those who don't use OC, we keep this tidbit around.
		MUST_UPDATE_TILE_ENTITIES = true;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		gui = new GuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(Computronics.instance, gui);

		if(chatBox != null) {
			MinecraftForge.EVENT_BUS.register(new ChatBoxHandler());
		}

		proxy.registerGuis(gui);
		if(proxy.isClient() && colorfulLamp != null) {
			new LampRender();
		}

		FMLInterModComms.sendMessage(Mods.Waila, "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");

		if(Loader.isModLoaded(Mods.GregTech) && GREGTECH_RECIPES) {
			ModRecipes.GregTechRecipes.registerGregTechRecipes();
		} else {
			ModRecipes.registerRecipes();
		}

		// Mod compat - GregTech
		if(itemTape != null && Loader.isModLoaded(Mods.GregTech) && itemPartsGreg != null) {
			ModRecipes.GregTechRecipes.regsiterGregTechTapeRecipes();
		}

		if(Loader.isModLoaded(Mods.ComputerCraft)) {
			initCC();
		}
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC();
		}

		achievements = new ComputronicsAchievements();
		achievements.initialize();

		config.save();
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	private void initCC() {
		if(Loader.isModLoaded(Mods.RedLogic)) {
			if(config.get("modCompatibility", "enableRedLogicLamps", true).getBoolean(true)) {
				ComputerCraftAPI.registerPeripheralProvider(new LampPeripheral());
			}
			if(config.get("computercraft", "enableBundledRedstoneProviders", true).getBoolean(true)) {
				ComputerCraftAPI.registerBundledRedstoneProvider(new CCBundledRedstoneProviderRedLogic());
			}
		}
		if(Loader.isModLoaded(Mods.MFR) || Loader.isModLoaded(Mods.JABBA)) {
			if(config.get("modCompatibility", "enableDeepStorageUnit", true).getBoolean(true)) {
				ComputerCraftAPI.registerPeripheralProvider(new DeepStorageUnitPeripheral());
			}
		}
		if(Loader.isModLoaded(Mods.FSP)) {
			if(config.get("modCompatibility", "enableFlaxbeardSteamTransporters", true).getBoolean(true)) {
				ComputerCraftAPI.registerPeripheralProvider(new SteamTransporterPeripheral());
			}
		}
		if(Loader.isModLoaded(Mods.Factorization)) {
			if(config.get("modCompatibility", "enableFactorizationChargePeripheral", true).getBoolean(true)) {
				ComputerCraftAPI.registerPeripheralProvider(new ChargeConductorPeripheral());
			}
		}

		if(Loader.isModLoaded(Mods.Railcraft)) {
			if(config.get("modCompatibility", "enableRailcraftRoutingPeripherals", true).getBoolean(true)) {
				ComputerCraftAPI.registerPeripheralProvider(new RoutingTrackPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new RoutingDetectorPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new RoutingSwitchPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new ReceiverBoxPeripheral());
			}
		}

		ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
		if(itemTape != null) {
			ComputerCraftAPI.registerMediaProvider(itemTape);
		}

		if(isEnabled("ccTurtleUpgrades", true)) {
			ComputerCraftAPI.registerTurtleUpgrade(
				new SpeakingTurtleUpgrade(config.get("turtleUpgradeIDs", "speaking", 190).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new RadarTurtleUpgrade(config.get("turtleUpgradeIDs", "radar", 191).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new MusicalTurtleUpgrade(config.get("turtleUpgradeIDs", "musical", 192).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new ParticleTurtleUpgrade(config.get("turtleUpgradeIDs", "particle", 193).getInt()));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOC() {
		if(Loader.isModLoaded(Mods.RedLogic)) {
			if(config.get("modCompatibility", "enableRedLogicLamps", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverLamp());
			}
		}
		if(Loader.isModLoaded(Mods.BetterStorage)) {
			if(config.get("modCompatibility", "enableBetterStorageCrates", true).getBoolean(true)) {
				try {
					Class.forName("net.mcft.copy.betterstorage.api.ICrateStorage");
					log.info("Using old (pre-0.10) BetterStorage crate API!");
					li.cil.oc.api.Driver.add(new DriverCrateStorageOld());
				} catch(Exception e) {
					//NO-OP
				}

				try {
					Class.forName("net.mcft.copy.betterstorage.api.crate.ICrateStorage");
					log.info("Using new (0.10+) BetterStorage crate API!");
					li.cil.oc.api.Driver.add(new DriverCrateStorageNew());
				} catch(Exception e) {
					//NO-OP
				}
			}
		}
		if(Loader.isModLoaded(Mods.MFR) || Loader.isModLoaded(Mods.JABBA)) {
			if(config.get("modCompatibility", "enableDeepStorageUnit", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverDeepStorageUnit());
			}
		}
		if(Loader.isModLoaded(Mods.FSP)) {
			if(config.get("modCompatibility", "enableFlaxbeardSteamTransporters", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverSteamTransporter());
			}
		}
		if(Loader.isModLoaded(Mods.Factorization)) {
			if(config.get("modCompatibility", "enableFactorizationChargePeripheral", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverChargeConductor());
			}
		}
		if(Loader.isModLoaded(Mods.Railcraft)) {
			if(config.get("modCompatibility", "enableRailcraftRoutingComponents", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverRoutingTrack());
				li.cil.oc.api.Driver.add(new DriverRoutingDetector());
				li.cil.oc.api.Driver.add(new DriverRoutingSwitch());
				li.cil.oc.api.Driver.add(new DriverReceiverBox());
			}
		}
		if(Loader.isModLoaded(Mods.GregTech)) {
			if(config.get("modCompatibility", "enableGregTechMachines", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverBaseMetaTileEntity());
				li.cil.oc.api.Driver.add(new DriverDeviceInformation());
				li.cil.oc.api.Driver.add(new DriverMachine());
				li.cil.oc.api.Driver.add(new DriverBatteryBuffer());
			}
			if(config.get("modCompatibility", "enableGregTechDigitalChests", true).getBoolean(true)) {
				li.cil.oc.api.Driver.add(new DriverDigitalChest());
			}
		}

		if(isEnabled("ocRobotUpgrades", true)) {
			Block[] b = { camera, chatBox, radar };
			try {
				for(int i = 0; i < b.length; i++) {
					Block t = b[i];
					GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "mcm", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
					GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "m", "c", "m", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
				}
				GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, 3), "mf", " b", 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1), 'f', Items.firework_charge, 'b', li.cil.oc.api.Items.get("card").createItemStack(1));
			} catch(Exception e) {
				log.error("Could not create robot upgrade recipes! You are most likely using OpenComputers 1,2 - please upgrade to 1.3.0+!");
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {
		Computronics.storage = new StorageManager();
	}
}
