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
import li.cil.oc.api.Driver;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockCipher;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockEEPROMReader;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockRadar;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.cc.CCPeripheralProvider;
import pl.asie.computronics.cc.MusicalTurtleUpgrade;
import pl.asie.computronics.cc.ParticleTurtleUpgrade;
import pl.asie.computronics.cc.RadarTurtleUpgrade;
import pl.asie.computronics.cc.SpeakingTurtleUpgrade;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorage;
import pl.asie.computronics.integration.factorization.ChargeConductorPeripheral;
import pl.asie.computronics.integration.factorization.DriverChargeConductor;
import pl.asie.computronics.integration.fsp.DriverSteamTransporter;
import pl.asie.computronics.integration.fsp.SteamTransporterPeripheral;
import pl.asie.computronics.integration.gregtech.DriverBaseMetaTileEntity;
import pl.asie.computronics.integration.gregtech.DriverDeviceInformation;
import pl.asie.computronics.integration.gregtech.DriverDigitalChest;
import pl.asie.computronics.integration.gregtech.DriverMachine;
import pl.asie.computronics.integration.mfr.DeepStorageUnitPeripheral;
import pl.asie.computronics.integration.mfr.DriverDeepStorageUnit;
import pl.asie.computronics.integration.projectred.CCBundledRedstoneProviderProjectRed;
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
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.tape.StorageManager;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.tile.TileEEPROMReader;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileRadar;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.item.ItemMultiple;
import pl.asie.lib.network.PacketHandler;
import pl.asie.lib.util.color.RecipeColorizer;

import java.util.Random;

@Mod(modid="computronics", name="Computronics", version="0.6.5", dependencies="required-after:asielib;after:ComputerCraft;after:OpenComputers;after:OpenComputers|Core;after:MineFactoryReloaded;after:RedLogic;after:ProjRed|Core;after:nedocomputers")
public class Computronics {
	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;

	public static FMLEventChannel channel;

	@Instance(value="computronics")
	public static Computronics instance;
	public static StorageManager storage;
	public static GuiHandler gui;
	public static PacketHandler packet;
	public DFPWMPlaybackManager audio;

	public static int CHATBOX_DISTANCE = 40;
	public static int CAMERA_DISTANCE = 32;
	public static int TAPEDRIVE_DISTANCE = 24;
	public static int BUFFER_MS = 750;
	public static int RADAR_RANGE = 8;
	public static boolean RADAR_ONLY_DISTANCE = false;
	public static double RADAR_OC_ENERGY_COST = 5.0;
	public static double RADAR_CC_TIME = 0.5;
	public static double FX_ENERGY_COST = 0.5;
	public static String CHATBOX_PREFIX = "ChatBox";

	public static String TAPE_LENGTHS;
	public static boolean REDSTONE_REFRESH, CHATBOX_ME_DETECT, CHATBOX_CREATIVE, DISABLE_IRONNOTE_FORGE_EVENTS;

	@SidedProxy(clientSide="pl.asie.computronics.ClientProxy", serverSide="pl.asie.computronics.CommonProxy")
	public static CommonProxy proxy;

	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockCipher cipher;
    public static BlockRadar radar;
    public static BlockEEPROMReader nc_eepromreader;
	public static BlockColorfulLamp colorfulLamp;

	public static ItemTape itemTape;
	public static ItemMultiple itemParts;
	public static ItemOpenComputers itemRobotUpgrade;

	public static boolean MUST_UPDATE_TILE_ENTITIES = false;

	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
        public Item getTabIconItem() {
                return itemTape;
        }
	};

	private boolean isEnabled(String name, boolean def) {
		return config.get("enable", name, def).getBoolean(def);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = LogManager.getLogger("computronics");

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		audio = new DFPWMPlaybackManager(proxy.isClient());
		packet = new PacketHandler("computronics", new NetworkHandlerClient(), new NetworkHandlerServer());

		// Configs
		CHATBOX_DISTANCE = config.get("chatbox", "maxDistance", 40).getInt();
		CAMERA_DISTANCE = config.get("camera", "maxDistance", 32).getInt();
		REDSTONE_REFRESH = config.get("general", "enableTickingRedstoneSupport", true).getBoolean(true);
		BUFFER_MS = config.get("tapedrive", "audioPreloadMs", 750).getInt();
		CHATBOX_PREFIX = config.get("chatbox", "prefix", "ChatBox").getString();
		CHATBOX_ME_DETECT = config.get("chatbox", "readCommandMe", false).getBoolean(false);
		CHATBOX_CREATIVE = config.get("chatbox", "enableCreative", true).getBoolean(true);
		TAPEDRIVE_DISTANCE = config.get("tapedrive", "hearingDistance", 24).getInt();
		TAPE_LENGTHS = config.get("tapedrive", "tapeLengths", "4,8,16,32,64,2,6,16,128").getString();
		RADAR_RANGE = config.get("radar", "maxRange", 8).getInt();
		RADAR_ONLY_DISTANCE = config.get("radar", "onlyOutputDistance", false).getBoolean(false);
		DISABLE_IRONNOTE_FORGE_EVENTS = config.get("ironnoteblock", "disableForgeEvents", false).getBoolean(false);

		RADAR_CC_TIME = config.get("computercraft", "radarSpeedPerDistanceUnit", 0.5).getDouble(0.5);

		RADAR_OC_ENERGY_COST = config.get("opencomputers", "radarEnergyPerDistanceUnit", 50.0).getDouble(50.0);
		FX_ENERGY_COST = config.get("opencomputers", "particleEnergyCost", 0.5).getDouble(0.5);

		config.get("camera", "sendRedstoneSignal", true).comment = "Setting this to false might help Camera tick lag issues, at the cost of making them useless with redstone circuitry.";

		if(isEnabled("ironNoteBlock", true)) {
			ironNote = new BlockIronNote();
			GameRegistry.registerBlock(ironNote, "computronics.ironNoteBlock");
			GameRegistry.registerTileEntity(TileIronNote.class, "computronics.ironNoteBlock");
		}

		if(isEnabled("tape", true)) {
			tapeReader = new BlockTapeReader();
			GameRegistry.registerBlock(tapeReader, "computronics.tapeReader");
			GameRegistry.registerTileEntity(TileTapeDrive.class, "computronics.tapeReader");
		}

		if(isEnabled("camera", true)) {
			camera = new BlockCamera();
			GameRegistry.registerBlock(camera, "computronics.camera");
			GameRegistry.registerTileEntity(TileCamera.class, "computronics.camera");
		}

		if(isEnabled("chatBox", true)) {
			chatBox = new BlockChatBox();
			GameRegistry.registerBlock(chatBox, ItemBlockChatBox.class, "computronics.chatBox");
			GameRegistry.registerTileEntity(TileChatBox.class, "computronics.chatBox");
		}

		if(isEnabled("cipher", true)) {
			cipher = new BlockCipher();
			GameRegistry.registerBlock(cipher, "computronics.cipher");
			GameRegistry.registerTileEntity(TileCipherBlock.class, "computronics.cipher");
		}

		if(isEnabled("radar", true)) {
			radar = new BlockRadar();
			GameRegistry.registerBlock(radar, "computronics.radar");
			GameRegistry.registerTileEntity(TileRadar.class, "computronics.radar");
		}

		if(isEnabled("lamp", true)) {
			colorfulLamp = new BlockColorfulLamp();
			GameRegistry.registerBlock(colorfulLamp, "computronics.colorfulLamp");
			GameRegistry.registerTileEntity(TileColorfulLamp.class, "computronics.colorfulLamp");
		}

		if(Loader.isModLoaded("nedocomputers") && isEnabled("eepromReader", true)) {
			nc_eepromreader = new BlockEEPROMReader();
			GameRegistry.registerBlock(nc_eepromreader, "computronics.eepromReader");
			GameRegistry.registerTileEntity(TileEEPROMReader.class, "computronics.eepromReader");
		}

		if(isEnabled("tape", true)) {
			itemTape = new ItemTape(TAPE_LENGTHS);
			GameRegistry.registerItem(itemTape, "computronics.tape");
		}

		itemParts = new ItemMultiple("computronics", new String[]{"part_tape_track"});
		itemParts.setCreativeTab(tab);
		GameRegistry.registerItem(itemParts, "computronics.parts");

		if(Loader.isModLoaded("OpenComputers")) preInitOC();
	}

	@Optional.Method(modid="OpenComputers")
	private void preInitOC() {
		if(isEnabled("ocRobotUpgrades", true)) {
			itemRobotUpgrade = new ItemOpenComputers();
			GameRegistry.registerItem(itemRobotUpgrade, "computronics.robotUpgrade");
			Driver.add(itemRobotUpgrade);
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

		MinecraftForge.EVENT_BUS.register(new ChatBoxHandler());

		proxy.registerGuis(gui);
		if(proxy.isClient()) {
			new LampRender();
		}

		FMLInterModComms.sendMessage("Waila", "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");

		if(camera != null)
			GameRegistry.addShapedRecipe(new ItemStack(camera, 1, 0), "sss", "geg", "iii", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'g', Blocks.glass);
		if(chatBox != null)
			GameRegistry.addShapedRecipe(new ItemStack(chatBox, 1, 0), "sss", "ses", "iri", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'r', Items.redstone);
		if(ironNote != null)
			GameRegistry.addShapedRecipe(new ItemStack(ironNote, 1, 0), "iii", "ini", "iii", 'i', Items.iron_ingot, 'n', Blocks.noteblock);
		if(tapeReader != null)
			GameRegistry.addShapedRecipe(new ItemStack(tapeReader, 1, 0), "iii", "iri", "iai", 'i', Items.iron_ingot, 'r', Items.redstone, 'a', ironNote);
		if(cipher != null)
			GameRegistry.addShapedRecipe(new ItemStack(cipher, 1, 0), "sss", "srs", "eie", 'i', Items.iron_ingot, 'r', Items.redstone, 'e', Items.ender_pearl, 's', Blocks.stonebrick);
		if(radar != null)
			GameRegistry.addShapedRecipe(new ItemStack(radar, 1, 0), "sts", "rbr", "scs", 'i', Items.iron_ingot, 'r', Items.redstone, 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'b', Items.bowl, 'c', Items.comparator);
        if(nc_eepromreader != null)
            GameRegistry.addShapedRecipe(new ItemStack(nc_eepromreader, 1, 0), "sts", "iei", "srs", 'i', Items.iron_ingot, 'r', Items.redstone, 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'e', GameRegistry.findItem("nedocomputers", "EEPROM"));
        if(colorfulLamp != null)
        	GameRegistry.addShapedRecipe(new ItemStack(colorfulLamp, 1, 0), "igi", "glg", "igi", 'i', Items.iron_ingot, 'g', Blocks.glass, 'l', Items.glowstone_dust);
        if(itemTape != null) {
			// Tape recipes
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 0),
					" i ", "iii", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 1),
					" i ", "ngn", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot, 'n', Items.gold_nugget, 'g', Items.gold_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 2),
					" i ", "ggg", "nTn", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot, 'n', Items.gold_nugget, 'g', Items.gold_ingot));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 3),
					" i ", "ddd", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot, 'd', Items.diamond));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 4),
					" d ", "dnd", " T ", 'T', new ItemStack(itemParts, 1, 0), 'n', Items.nether_star, 'd', Items.diamond));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 8),
					" n ", "nnn", " T ", 'T', new ItemStack(itemParts, 1, 0), 'n', Items.nether_star));

			// Mod compat - copper/steel
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 5),
					" i ", " c ", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot, 'c', "ingotCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 6),
					" i ", "isi", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Items.iron_ingot, 's', "ingotSteel"));

			// Mod compat - GregTech
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 7),
					" i ", "isi", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', "plateIridium", 's', "plateTungstenSteel"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, 0),
					" i ", "rrr", "iii", 'r', Items.redstone, 'i', Items.iron_ingot));
			GameRegistry.addRecipe(new RecipeColorizer(itemTape));
        }

		if(Loader.isModLoaded("ComputerCraft")) initCC();
		if(Loader.isModLoaded("OpenComputers")) initOC();

		config.save();
	}

	@Optional.Method(modid="ComputerCraft")
	private void initCC() {
		if(Loader.isModLoaded("RedLogic")) {
			if(config.get("modCompatibility", "enableRedLogicLamps", true).getBoolean(true)) ComputerCraftAPI.registerPeripheralProvider(new LampPeripheral());
			if(config.get("computercraft", "enableBundledRedstoneProviders", true).getBoolean(true)) ComputerCraftAPI.registerBundledRedstoneProvider(new CCBundledRedstoneProviderRedLogic());
		}
		if(Loader.isModLoaded("ProjRed|Core")) {
			if(config.get("computercraft", "enableBundledRedstoneProviders", true).getBoolean(true)) ComputerCraftAPI.registerBundledRedstoneProvider(new CCBundledRedstoneProviderProjectRed());
		}
		if(Loader.isModLoaded("MineFactoryReloaded") || Loader.isModLoaded("JABBA")) {
			if(config.get("modCompatibility", "enableDeepStorageUnit", true).getBoolean(true)) ComputerCraftAPI.registerPeripheralProvider(new DeepStorageUnitPeripheral());
		}
		if(Loader.isModLoaded("Steamcraft")) {
			if(config.get("modCompatibility", "enableFlaxbeardSteamTransporters", true).getBoolean(true)) ComputerCraftAPI.registerPeripheralProvider(new SteamTransporterPeripheral());
		}
		if(Loader.isModLoaded("factorization")) {
			if(config.get("modCompatibility", "enableFactorizationChargePeripheral", true).getBoolean(true)) ComputerCraftAPI.registerPeripheralProvider(new ChargeConductorPeripheral());
		}

		if(Loader.isModLoaded("Railcraft")) {
			if(config.get("modCompatibility", "enableRailcraftRoutingPeripherals", true).getBoolean(true)){
				ComputerCraftAPI.registerPeripheralProvider(new RoutingTrackPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new RoutingDetectorPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new RoutingSwitchPeripheral());
				ComputerCraftAPI.registerPeripheralProvider(new ReceiverBoxPeripheral());
			}
		}

		ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
		if(itemTape != null) ComputerCraftAPI.registerMediaProvider(itemTape);

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

	@Optional.Method(modid="OpenComputers")
	private void initOC() {
		if(Loader.isModLoaded("RedLogic")) {
			if(config.get("modCompatibility", "enableRedLogicLamps", true).getBoolean(true)) li.cil.oc.api.Driver.add(new DriverLamp());
		}
		if(Loader.isModLoaded("betterstorage")) {
			if(config.get("modCompatibility", "enableBetterStorageCrates", true).getBoolean(true)) li.cil.oc.api.Driver.add(new DriverCrateStorage());
		}
		if(Loader.isModLoaded("MineFactoryReloaded") || Loader.isModLoaded("JABBA")) {
			if(config.get("modCompatibility", "enableDeepStorageUnit", true).getBoolean(true)) li.cil.oc.api.Driver.add(new DriverDeepStorageUnit());
		}
		if(Loader.isModLoaded("Steamcraft")) {
			if(config.get("modCompatibility", "enableFlaxbeardSteamTransporters", true).getBoolean(true)) li.cil.oc.api.Driver.add(new DriverSteamTransporter());
		}
		if(Loader.isModLoaded("factorization")) {
			if(config.get("modCompatibility", "enableFactorizationChargePeripheral", true).getBoolean(true)) li.cil.oc.api.Driver.add(new DriverChargeConductor());
		}
        if(Loader.isModLoaded("Railcraft")) {
            if(config.get("modCompatibility", "enableRailcraftRoutingComponents", true).getBoolean(true)){
                li.cil.oc.api.Driver.add(new DriverRoutingTrack());
                li.cil.oc.api.Driver.add(new DriverRoutingDetector());
                li.cil.oc.api.Driver.add(new DriverRoutingSwitch());
                li.cil.oc.api.Driver.add(new DriverReceiverBox());
            }
        }
        if(Loader.isModLoaded("gregtech_addon")) {
        	if(config.get("modCompatibility", "enableGregTechMachines", true).getBoolean(true)) {
        		li.cil.oc.api.Driver.add(new DriverBaseMetaTileEntity());
        		li.cil.oc.api.Driver.add(new DriverDeviceInformation());
        		li.cil.oc.api.Driver.add(new DriverMachine());
        	}
        	if(config.get("modCompatibility", "enableGregTechDigitalChests", true).getBoolean(true)) {
        		li.cil.oc.api.Driver.add(new DriverDigitalChest());
        	}
        }

		if(isEnabled("ocRobotUpgrades", true)) {
			Block[] b = {camera, chatBox, radar};
			for(int i = 0; i < b.length; i++) {
				Block t = b[i];
				GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "mcm", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
				GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "m", "c", "m", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
			}
			GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, 3), "mf", " b", 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1), 'f', Items.firework_charge, 'b', li.cil.oc.api.Items.get("card").createItemStack(1));
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
