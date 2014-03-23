package pl.asie.computronics;

import java.util.Random;
import java.util.logging.Logger;

import li.cil.oc.api.Driver;
import li.cil.oc.api.Items;
import openperipheral.api.OpenPeripheralAPI;
import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockSorter;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.gui.GuiOneSlot;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.tile.sorter.TileSorter;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.item.ItemMultiple;
import pl.asie.lib.network.PacketFactory;
import pl.asie.lib.util.ModIntegrationHandler;
import pl.asie.lib.util.ModIntegrationHandler.Stage;
import pl.asie.lib.util.color.RecipeColorizer;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="computronics", name="Computronics", version="0.2.4", dependencies="required-after:asielib;after:OpenPeripheralCore;after:ComputerCraft;after:OpenComputers;after:OpenComputers|Core")
@NetworkMod(channels={"computronics"}, clientSideRequired=true, packetHandler=NetworkHandler.class)
public class Computronics {
	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;
	
	@Instance(value="computronics")
	public static Computronics instance;
	public static ModIntegrationHandler integration;
	public static StorageManager storage;
	public static GuiHandler gui;
	public static PacketFactory packet;
	public DFPWMPlaybackManager audio;
	
	public static int CHATBOX_DISTANCE = 40;
	public static int CAMERA_DISTANCE = 32;
	public static int BUFFER_MS = 750;
	public static boolean CAMERA_REDSTONE_REFRESH = true;
	
	@SidedProxy(clientSide="pl.asie.computronics.ClientProxy", serverSide="pl.asie.computronics.CommonProxy")	
	public static CommonProxy proxy;
	
	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockSorter sorter;
	public static ItemTape itemTape;
	public static ItemMultiple itemParts;
	public static ItemOpenComputers itemRobotUpgrade;
	
	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
        public ItemStack getIconItemStack() {
                return new ItemStack(itemTape, 1, 3);
        }
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = Logger.getLogger("computronics");
		log.setParent(FMLLog.getLogger());

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		integration = new ModIntegrationHandler(log);
		audio = new DFPWMPlaybackManager(proxy.isClient());
		packet = new PacketFactory("computronics");
		
		// Configs
		CHATBOX_DISTANCE = config.get("options", "chatboxDistance", 40).getInt();
		CAMERA_DISTANCE = config.get("options", "cameraDistance", 32).getInt();
		CAMERA_REDSTONE_REFRESH = config.get("options", "cameraRedstoneRefresh", true).getBoolean(true);
		BUFFER_MS = config.get("options", "audioBufferMilisec", 750).getInt();
		
		config.get("options", "cameraRedstoneRefresh", true).comment = "Setting this to false might help Camera tick lag issues, at the cost of making them useless with redstone circuitry.";
				
		//integration.init(Stage.PRE_INIT);
		
		ironNote = new BlockIronNote(config.getBlock("ironNote", 2710).getInt());
		GameRegistry.registerBlock(ironNote, "computronics.ironNoteBlock");
		GameRegistry.registerTileEntity(TileIronNote.class, "computronics.ironNoteBlock");

		tapeReader = new BlockTapeReader(config.getBlock("tapeReader", 2711).getInt());
		GameRegistry.registerBlock(tapeReader, "computronics.tapeReader");
		GameRegistry.registerTileEntity(TileTapeDrive.class, "computronics.tapeReader");

		camera = new BlockCamera(config.getBlock("camera", 2712).getInt());
		GameRegistry.registerBlock(camera, "computronics.camera");
		GameRegistry.registerTileEntity(TileCamera.class, "computronics.camera");

		chatBox = new BlockChatBox(config.getBlock("chatBox", 2713).getInt());
		GameRegistry.registerBlock(chatBox, "computronics.chatBox");
		GameRegistry.registerTileEntity(TileChatBox.class, "computronics.chatBox");

		sorter = new BlockSorter(config.getBlock("sorter", 2714).getInt());
		GameRegistry.registerBlock(sorter, "computronics.sorter");
		GameRegistry.registerTileEntity(TileSorter.class, "computronics.sorter");

		if(Loader.isModLoaded("OpenPeripheralCore")) {
			OpenPeripheralAPI.createAdapter(TileSorter.class);
			OpenPeripheralAPI.createAdapter(TileTapeDrive.class);
			OpenPeripheralAPI.createAdapter(TileIronNote.class);
			OpenPeripheralAPI.createAdapter(TileCamera.class);
		}			
		
		itemTape = new ItemTape(config.getItem("tape", 27850).getInt());
		GameRegistry.registerItem(itemTape, "computronics.tape");
		
		itemParts = new ItemMultiple(config.getItem("parts", 27851).getInt(), "computronics",
				new String[]{"part_tape_track"});
		itemParts.setCreativeTab(tab);
		GameRegistry.registerItem(itemParts, "computronics.parts");
		
		if(Loader.isModLoaded("OpenComputers")) {
			itemRobotUpgrade = new ItemOpenComputers(config.getItem("robotUpgrades", 27852).getInt());
			GameRegistry.registerItem(itemRobotUpgrade, "computronics.robotUpgrade");
			Driver.add(itemRobotUpgrade);
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//integration.init(Stage.INIT);
		gui = new GuiHandler();
		NetworkRegistry.instance().registerGuiHandler(Computronics.instance, gui);
		
		MinecraftForge.EVENT_BUS.register(new ComputronicsEventHandler());
		
		proxy.registerGuis(gui);
		
		FMLInterModComms.sendMessage("Waila", "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");
		
		GameRegistry.addShapedRecipe(new ItemStack(camera, 1, 0), "sss", "geg", "iii", 's', Block.stoneBrick, 'i', Item.ingotIron, 'e', Item.enderPearl, 'g', Block.glass);
		GameRegistry.addShapedRecipe(new ItemStack(chatBox, 1, 0), "sss", "ses", "iri", 's', Block.stoneBrick, 'i', Item.ingotIron, 'e', Item.enderPearl, 'r', Item.redstone);
		GameRegistry.addShapedRecipe(new ItemStack(ironNote, 1, 0), "iii", "ini", "iii", 'i', Item.ingotIron, 'n', Block.music);
		GameRegistry.addShapedRecipe(new ItemStack(tapeReader, 1, 0), "iii", "iri", "iai", 'i', Item.ingotIron, 'r', Item.redstone, 'a', ironNote);
		// Tape recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 0),
				" i ", "iii", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Item.ingotIron));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 1),
				" i ", "ngn", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Item.ingotIron, 'n', Item.goldNugget, 'g', Item.ingotGold));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 2),
				" i ", "ggg", "nTn", 'T', new ItemStack(itemParts, 1, 0), 'i', Item.ingotIron, 'n', Item.goldNugget, 'g', Item.ingotGold));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTape, 1, 3),
				" i ", "ddd", " T ", 'T', new ItemStack(itemParts, 1, 0), 'i', Item.ingotIron, 'd', Item.diamond));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, 0),
				" i ", "rrr", "iii", 'r', Item.redstone, 'i', Item.ingotIron));
		GameRegistry.addRecipe(new RecipeColorizer(itemTape));
		
		if(Loader.isModLoaded("OpenComputers")) {
			GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, 0), "mcm", 'c', new ItemStack(camera, 1, 0), 'm', Items.MicroChipTier2);
			GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, 0), "m", "c", "m", 'c', new ItemStack(camera, 1, 0), 'm', Items.MicroChipTier2);
		}
		config.save();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//integration.init(Stage.POST_INIT);
	}
	
	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {
		Computronics.storage = new StorageManager();
	}
}
