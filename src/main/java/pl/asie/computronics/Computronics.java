package pl.asie.computronics;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import li.cil.oc.api.Driver;
import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockCipher;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockRadar;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.cc.CCPeripheralProvider;
import pl.asie.computronics.cc.SpeakingTurtleUpgrade;
import pl.asie.computronics.gui.GuiOneSlot;
import pl.asie.computronics.item.ItemBlockChatBox;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.tape.StorageManager;
import pl.asie.computronics.tape.TileTapeDrive;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileRadar;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.item.ItemMultiple;
import pl.asie.lib.network.PacketHandler;
import pl.asie.lib.util.ModIntegrationHandler;
import pl.asie.lib.util.ModIntegrationHandler.Stage;
import pl.asie.lib.util.color.RecipeColorizer;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid="computronics", name="Computronics", version="0.5.0", dependencies="required-after:asielib;after:ComputerCraft;after:OpenComputers;after:OpenComputers|Core;after:BuildCraft|Core")
public class Computronics {
	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;
	
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
	public static String CHATBOX_PREFIX = "ChatBox";

	public static String TAPE_LENGTHS;
	public static boolean CAMERA_REDSTONE_REFRESH, CHATBOX_ME_DETECT, CHATBOX_CREATIVE;
	
	@SidedProxy(clientSide="pl.asie.computronics.ClientProxy", serverSide="pl.asie.computronics.CommonProxy")	
	public static CommonProxy proxy;
	
	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockCipher cipher;
    public static BlockRadar radar;
	
	public static ItemTape itemTape;
	public static ItemMultiple itemParts;
	public static ItemOpenComputers itemRobotUpgrade;
	
	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
        public Item getTabIconItem() {
                return itemTape;
        }
	};

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
		CAMERA_REDSTONE_REFRESH = config.get("camera", "sendRedstoneSignal", true).getBoolean(true);
		BUFFER_MS = config.get("tapedrive", "audioPreloadMs", 750).getInt();
		CHATBOX_PREFIX = config.get("chatbox", "prefix", "ChatBox").getString();
		CHATBOX_ME_DETECT = config.get("chatbox", "readCommandMe", false).getBoolean(false);
		CHATBOX_CREATIVE = config.get("chatbox", "enableCreative", true).getBoolean(true);
		TAPEDRIVE_DISTANCE = config.get("tapedrive", "hearingDistance", 24).getInt();
		TAPE_LENGTHS = config.get("tapedrive", "tapeLengths", "4,8,16,32,64,2,6,16,128").getString();
		RADAR_RANGE = config.get("radar", "maxRange", 8).getInt();
		RADAR_ONLY_DISTANCE = config.get("radar", "onlyOutputDistance", false).getBoolean(false);
		
		if(Loader.isModLoaded("ComputerCraft")) {
			RADAR_CC_TIME = config.get("computercraft", "radarSpeedPerDistanceUnit", 0.5).getDouble(0.5);
		}
		if(Loader.isModLoaded("OpenComputers")) {
			RADAR_OC_ENERGY_COST = config.get("opencomputers", "radarEnergyPerDistanceUnit", 50.0).getDouble(50.0);
		}
		
		config.get("camera", "sendRedstoneSignal", true).comment = "Setting this to false might help Camera tick lag issues, at the cost of making them useless with redstone circuitry.";
		
		ironNote = new BlockIronNote();
		GameRegistry.registerBlock(ironNote, "computronics.ironNoteBlock");
		GameRegistry.registerTileEntity(TileIronNote.class, "computronics.ironNoteBlock");

		tapeReader = new BlockTapeReader();
		GameRegistry.registerBlock(tapeReader, "computronics.tapeReader");
		GameRegistry.registerTileEntity(TileTapeDrive.class, "computronics.tapeReader");

		camera = new BlockCamera();
		GameRegistry.registerBlock(camera, "computronics.camera");
		GameRegistry.registerTileEntity(TileCamera.class, "computronics.camera");

		chatBox = new BlockChatBox();
		GameRegistry.registerBlock(chatBox, ItemBlockChatBox.class, "computronics.chatBox");
		GameRegistry.registerTileEntity(TileChatBox.class, "computronics.chatBox");

		cipher = new BlockCipher();
		GameRegistry.registerBlock(cipher, "computronics.cipher");
		GameRegistry.registerTileEntity(TileCipherBlock.class, "computronics.cipher");
		
		radar = new BlockRadar();
		GameRegistry.registerBlock(radar, "computronics.radar");
		GameRegistry.registerTileEntity(TileRadar.class, "computronics.radar");
		
		itemTape = new ItemTape(TAPE_LENGTHS);
		GameRegistry.registerItem(itemTape, "computronics.tape");
		
		itemParts = new ItemMultiple("computronics", new String[]{"part_tape_track"});
		itemParts.setCreativeTab(tab);
		GameRegistry.registerItem(itemParts, "computronics.parts");
		
		if(Loader.isModLoaded("OpenComputers")) preInitOC();
		if(Loader.isModLoaded("ComputerCraft")) preInitCC();
	}

	@Optional.Method(modid="ComputerCraft")
	private void preInitCC() {
		ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
		ComputerCraftAPI.registerMediaProvider(itemTape);
		
		new SpeakingTurtleUpgrade(config.get("turtleUpgradeIDs", "speaking", 190).getInt());
	}
	
	@Optional.Method(modid="OpenComputers")
	private void preInitOC() {
		itemRobotUpgrade = new ItemOpenComputers();
		GameRegistry.registerItem(itemRobotUpgrade, "computronics.robotUpgrade");
		Driver.add(itemRobotUpgrade);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		gui = new GuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(Computronics.instance, gui);
		
		MinecraftForge.EVENT_BUS.register(new ChatBoxHandler());
		
		proxy.registerGuis(gui);
		
		FMLInterModComms.sendMessage("Waila", "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");
		
		GameRegistry.addShapedRecipe(new ItemStack(camera, 1, 0), "sss", "geg", "iii", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'g', Blocks.glass);
		GameRegistry.addShapedRecipe(new ItemStack(chatBox, 1, 0), "sss", "ses", "iri", 's', Blocks.stonebrick, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'r', Items.redstone);
		GameRegistry.addShapedRecipe(new ItemStack(ironNote, 1, 0), "iii", "ini", "iii", 'i', Items.iron_ingot, 'n', Blocks.noteblock);
		GameRegistry.addShapedRecipe(new ItemStack(tapeReader, 1, 0), "iii", "iri", "iai", 'i', Items.iron_ingot, 'r', Items.redstone, 'a', ironNote);
		GameRegistry.addShapedRecipe(new ItemStack(cipher, 1, 0), "sss", "srs", "eie", 'i', Items.iron_ingot, 'r', Items.redstone, 'e', Items.ender_pearl, 's', Blocks.stonebrick);
        GameRegistry.addShapedRecipe(new ItemStack(radar, 1, 0), "sts", "rbr", "scs", 'i', Items.iron_ingot, 'r', Items.redstone, 't', Blocks.redstone_torch, 's', Blocks.stonebrick, 'b', Items.bowl, 'c', Items.comparator);
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
		
		if(Loader.isModLoaded("OpenComputers")) initOC();

		config.save();
	}
	
	@Optional.Method(modid="OpenComputers")
	private void initOC() {
		Block[] b = {camera, chatBox, radar};
		for(int i = 0; i < b.length; i++) {
			Block t = b[i];
			GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "mcm", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
			GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "m", "c", "m", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
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
