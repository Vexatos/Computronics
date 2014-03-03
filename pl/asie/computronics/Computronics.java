package pl.asie.computronics;

import java.util.Random;
import java.util.logging.Logger;

import openperipheral.api.OpenPeripheralAPI;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.storage.StorageManager;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.api.AsieLibAPI;
import pl.asie.lib.util.ModIntegrationHandler;
import pl.asie.lib.util.ModIntegrationHandler.Stage;
import net.minecraftforge.common.Configuration;
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
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="computronics", name="Computronics", version="0.1.0", dependencies="required-after:asielib;after:OpenPeripheralCore;after:ComputerCraft;after:OpenComputers")
public class Computronics {
	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;
	
	@Instance(value="computronics")
	public static Computronics instance;
	public static ModIntegrationHandler integration;
	public static StorageManager storage;
	
	@SidedProxy(clientSide="pl.asie.computronics.ClientProxy", serverSide="pl.asie.computronics.CommonProxy")	
	public static CommonProxy proxy;
	
	public BlockIronNote ironNote;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = Logger.getLogger("computronics");
		log.setParent(FMLLog.getLogger());

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		integration = new ModIntegrationHandler(log);
		
		//integration.init(Stage.PRE_INIT);
		
		ironNote = new BlockIronNote(config.getBlock("ironNote", 2710).getInt());
		GameRegistry.registerBlock(ironNote, "computronics.ironNoteBlock");
		GameRegistry.registerTileEntity(TileIronNote.class, "computronics.ironNoteBlock");
		OpenPeripheralAPI.createAdapter(TileIronNote.class);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//integration.init(Stage.INIT);
		
		config.save();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//integration.init(Stage.POST_INIT);
	}
}
