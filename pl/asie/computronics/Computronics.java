package pl.asie.computronics;

import java.util.Random;
import java.util.logging.Logger;

import openperipheral.api.OpenPeripheralAPI;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.gui.GuiOneSlot;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.audio.DFPWMPlaybackManager;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.item.ItemParts;
import pl.asie.lib.network.PacketFactory;
import pl.asie.lib.util.ModIntegrationHandler;
import pl.asie.lib.util.ModIntegrationHandler.Stage;
import pl.asie.lib.util.color.RecipeColorizer;
import net.minecraft.block.Block;
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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="computronics", name="Computronics", version="0.1.1", dependencies="required-after:asielib;after:OpenPeripheralCore;after:ComputerCraft;after:OpenComputers;after:OpenComputers|Core")
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
	
	@SidedProxy(clientSide="pl.asie.computronics.ClientProxy", serverSide="pl.asie.computronics.CommonProxy")	
	public static CommonProxy proxy;
	
	public BlockIronNote ironNote;
	public BlockTapeReader tapeReader;
	public BlockCamera camera;
	public ItemTape itemTape;
	public ItemParts itemParts;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = Logger.getLogger("computronics");
		log.setParent(FMLLog.getLogger());

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		integration = new ModIntegrationHandler(log);
		audio = new DFPWMPlaybackManager(proxy.isClient());
		packet = new PacketFactory("computronics");
		
		//integration.init(Stage.PRE_INIT);
		
		ironNote = new BlockIronNote(config.getBlock("ironNote", 2710).getInt());
		GameRegistry.registerBlock(ironNote, "computronics.ironNoteBlock");
		GameRegistry.registerTileEntity(TileIronNote.class, "computronics.ironNoteBlock");
		OpenPeripheralAPI.createAdapter(TileIronNote.class);
		
		tapeReader= new BlockTapeReader(config.getBlock("tapeReader", 2711).getInt());
		GameRegistry.registerBlock(tapeReader, "computronics.tapeReader");
		GameRegistry.registerTileEntity(TileTapeDrive.class, "computronics.tapeReader");
		OpenPeripheralAPI.createAdapter(TileTapeDrive.class);
		
		camera = new BlockCamera(config.getBlock("camera", 2712).getInt());
		GameRegistry.registerBlock(camera, "computronics.camera");
		GameRegistry.registerTileEntity(TileCamera.class, "computronics.camera");
		OpenPeripheralAPI.createAdapter(TileCamera.class);
		
		itemTape = new ItemTape(config.getItem("tape", 27850).getInt());
		GameRegistry.registerItem(itemTape, "computronics.tape");
		
		itemParts = new ItemParts(config.getItem("parts", 27851).getInt(), "computronics",
				new String[]{"tape_track"});
		GameRegistry.registerItem(itemParts, "computronics.parts");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//integration.init(Stage.INIT);
		gui = new GuiHandler();
		NetworkRegistry.instance().registerGuiHandler(Computronics.instance, gui);
		
		MinecraftForge.EVENT_BUS.register(new ComputronicsEventHandler());
		
		proxy.registerGuis(gui);
		
		GameRegistry.addShapedRecipe(new ItemStack(camera, 1, 0), "sss", "geg", "iii", 's', Block.stoneBrick, 'i', Item.ingotIron, 'e', Item.enderPearl, 'g', Block.glass);
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
