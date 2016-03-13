package pl.asie.computronics;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralRegistry;
import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.block.BlockAudioCable;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockCipher;
import pl.asie.computronics.block.BlockCipherAdvanced;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockRadar;
import pl.asie.computronics.block.BlockSpeaker;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.cc.IntegrationComputerCraft;
import pl.asie.computronics.cc.multiperipheral.MultiPeripheralRegistry;
import pl.asie.computronics.gui.providers.GuiProviderCipher;
import pl.asie.computronics.gui.providers.GuiProviderTapeDrive;
import pl.asie.computronics.integration.ModRecipes;
import pl.asie.computronics.integration.buildcraft.IntegrationBuildCraftBuilder;
import pl.asie.computronics.integration.buildcraft.statements.ActionProvider;
import pl.asie.computronics.integration.buildcraft.statements.StatementParameters;
import pl.asie.computronics.integration.buildcraft.statements.TriggerProvider;
import pl.asie.computronics.integration.charset.IntegrationCharset;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;
import pl.asie.computronics.item.ItemMultiple;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.item.block.ComputronicsItemBlock;
import pl.asie.computronics.network.NetworkHandlerClient;
import pl.asie.computronics.network.NetworkHandlerServer;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.StorageManager;
import pl.asie.computronics.tape.TapeStorageEventHandler;
import pl.asie.computronics.tile.TileAudioCable;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileCipherBlockAdvanced;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileRadar;
import pl.asie.computronics.tile.TileSpeaker;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.util.achievements.ComputronicsAchievements;
import pl.asie.computronics.util.chat.ChatHandler;
import pl.asie.computronics.util.event.ServerTickHandler;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.gui.managed.IGuiProvider;
import pl.asie.lib.gui.managed.ManagedGuiHandler;
import pl.asie.lib.network.PacketHandler;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(modid = Mods.Computronics, name = Mods.Computronics_NAME, version = "@VERSION@",
	dependencies = "required-after:asielib;required-after:Forge@[11.15.1.1764,);"
		+ "after:ComputerCraft@[1.79,);after:OpenComputers@[1.5.22.6,);after:tis3d@[0.8.3.13,);"
		+ "before:OpenPeripheralCore@[1.1,);before:OpenPeripheralApi@[3.2,);"
		+ "after:MineFactoryReloaded;after:RedLogic@[59.1.9,);after:ProjRed|Core;"
		+ "after:BuildCraft|Core@[7.2.0,);after:Railcraft@[9.8.0.3,);"
		+ "after:gregtech;after:EnderIO@[1.8.9-3.0,);"
		+ "after:Forestry;after:Waila@[1.5.10,);"
		+ "after:MekanismAPI|energy@[8.0.0,);after:Flamingo@[1.7.10-1.3,);"
		+ "after:armourersWorkshop@[1.7.10-0.33,);"
		+ "before:CharsetAPI|Wires@[0.3,);before:CharsetWires")
public class Computronics {

	public Config config;
	public Compat compat;
	public static Random rand = new Random();
	public static Logger log = LogManager.getLogger(Mods.Computronics);

	public static FMLEventChannel channel;

	@Instance(value = Mods.Computronics)
	public static Computronics instance;
	public static StorageManager storage;
	public static TapeStorageEventHandler storageEventHandler;
	public static ManagedGuiHandler gui;
	public static PacketHandler packet;
	public DFPWMPlaybackManager audio;
	public static ExecutorService rsaThreads;
	public static ServerTickHandler serverTickHandler;

	@SidedProxy(clientSide = "pl.asie.computronics.ClientProxy", serverSide = "pl.asie.computronics.CommonProxy")
	public static CommonProxy proxy;

	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockAudioCable audioCable;
	public static BlockSpeaker speaker;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockCipher cipher;
	public static BlockCipherAdvanced cipher_advanced;
	public static BlockRadar radar;
	public static BlockColorfulLamp colorfulLamp;

	public static IntegrationOpenComputers opencomputers;
	public static IntegrationComputerCraft computercraft;

	//public static IntegrationBuildCraft buildcraft;
	//public static IntegrationRailcraft railcraft;
	//public static IntegrationForestry forestry;
	public static IntegrationTIS3D tis3D;
	public static IntegrationCharset charset;

	public static ItemTape itemTape;
	public static ItemMultiple itemParts;
	//public static ItemMultiple itemPartsGreg;

	public static IGuiProvider guiTapeDrive;
	public static IGuiProvider guiCipher;

	public ComputronicsAchievements achievements;

	public static MultiPeripheralRegistry peripheralRegistry;

	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
		public Item getTabIconItem() {
			return itemTape;
		}
	};

	public boolean isEnabled(String name, boolean def) {
		return config.isEnabled(name, def);
	}

	private void registerBlockWithTileEntity(BlockBase block, Class<? extends TileEntity> tile, String name) {
		registerBlockWithTileEntity(block, ComputronicsItemBlock.class, tile, name);
	}

	private void registerBlockWithTileEntity(BlockBase block, Class<? extends ItemBlock> itemBlock, Class<? extends TileEntity> tile, String name) {
		GameRegistry.registerBlock(block, itemBlock, name);
		GameRegistry.registerTileEntity(tile, name);
		proxy.registerItemModel(block, 0, "computronics:" + name);
		//System.out.println("Registering " + name + " as TE " + tile.getCanonicalName());
		FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", tile.getCanonicalName());
		IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(block);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();

		MinecraftForge.EVENT_BUS.register(serverTickHandler = new ServerTickHandler());

		config = new Config(event);

		audio = new DFPWMPlaybackManager(proxy.isClient());

		packet = new PacketHandler(Mods.Computronics, new NetworkHandlerClient(), new NetworkHandlerServer());

		compat = new Compat(this.config.config);

		config.preInit();

		gui = new ManagedGuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(Computronics.instance, gui);

		if(isEnabled("ironNoteBlock", true)) {
			ironNote = new BlockIronNote();
			registerBlockWithTileEntity(ironNote, TileIronNote.class, "iron_note_block");
		}

		if(isEnabled("audioCable", true)) {
			audioCable = new BlockAudioCable();
			registerBlockWithTileEntity(audioCable, TileAudioCable.class, "audio_cable");
		}

		if(isEnabled("speaker", true)) {
			speaker = new BlockSpeaker();
			registerBlockWithTileEntity(speaker, TileSpeaker.class, "speaker");
		}

		if(isEnabled("tape", true)) {
			guiTapeDrive = new GuiProviderTapeDrive();
			gui.registerGuiProvider(Computronics.guiTapeDrive);
			tapeReader = new BlockTapeReader();
			registerBlockWithTileEntity(tapeReader, TileTapeDrive.class, "tape_reader");
		}

		if(isEnabled("camera", true)) {
			camera = new BlockCamera();
			registerBlockWithTileEntity(camera, TileCamera.class, "camera");
		}

		if(isEnabled("chatBox", true)) {
			chatBox = new BlockChatBox();
			registerBlockWithTileEntity(chatBox, TileChatBox.class, "chat_box");
			proxy.registerItemModel(chatBox, 8, "computronics:chat_box");
		}

		if(isEnabled("cipher", true)) {
			guiCipher = new GuiProviderCipher();
			gui.registerGuiProvider(Computronics.guiCipher);
			cipher = new BlockCipher();
			registerBlockWithTileEntity(cipher, TileCipherBlock.class, "cipher");
		}

		if(isEnabled("cipher_advanced", true)) {
			cipher_advanced = new BlockCipherAdvanced();
			registerBlockWithTileEntity(cipher_advanced, TileCipherBlockAdvanced.class, "cipher_advanced");
			rsaThreads = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setPriority(Thread.MIN_PRIORITY).build());
		}

		if(isEnabled("radar", true)) {
			radar = new BlockRadar();
			registerBlockWithTileEntity(radar, TileRadar.class, "radar");
		}

		if(isEnabled("lamp", true)) {
			colorfulLamp = new BlockColorfulLamp();
			registerBlockWithTileEntity(colorfulLamp, TileColorfulLamp.class, "colorful_lamp");
		}

		if(isEnabled("tape", true)) {
			itemTape = new ItemTape(Config.TAPE_LENGTHS);
			GameRegistry.registerItem(itemTape, "tape");
			itemTape.registerItemModels();

			/*if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5)) { TODO GregTech
				itemPartsGreg = new ItemMultiple(Mods.Computronics, new String[] { "reelChromoxide" });
				itemPartsGreg.setCreativeTab(tab);
				GameRegistry.registerItem(itemPartsGreg, "computronics.gt_parts");
				proxy.registerEntities();
			}*/

			itemParts = new ItemMultiple(Mods.Computronics, new String[] { "part_tape_track" });
			itemParts.setCreativeTab(tab);
			GameRegistry.registerItem(itemParts, "parts");
			itemParts.registerItemModels();
		}

		/*if(Mods.isLoaded(Mods.Railcraft)) {
			railcraft = new IntegrationRailcraft();
			railcraft.preInit(config.config);
		}*/

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft = new IntegrationComputerCraft(this);
			peripheralRegistry = new MultiPeripheralRegistry();
		}

		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers = new IntegrationOpenComputers(this);
			opencomputers.preInit();
		}

		if(Mods.isLoaded(Mods.TIS3D)) {
			tis3D = new IntegrationTIS3D();
			tis3D.preInit();
		}

		charset = new IntegrationCharset();
		charset.preInit();

		proxy.registerAudioHandlers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ChatHandler());

		if(tapeReader != null) {
			storageEventHandler = new TapeStorageEventHandler();
			MinecraftForge.EVENT_BUS.register(storageEventHandler);
		}

		FMLInterModComms.sendMessage(Mods.Waila, "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft.init();
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers.init();
		}

		if(Mods.API.hasAPI(Mods.API.BuildCraftBlueprints)) {
			IntegrationBuildCraftBuilder.INSTANCE.init();
		}

		if(Mods.isLoaded(Mods.TIS3D) && tis3D != null) {
			tis3D.init(compat);
		}

		achievements = new ComputronicsAchievements();
		achievements.initialize();

		proxy.init();
		config.save();
	}

	/**
	 * Registers a new {@link IMultiPeripheralProvider}.
	 * If you want to hook into this, do it between Computronics' preInit and init phase
	 */
	@Optional.Method(modid = Mods.ComputerCraft)
	public static void registerMultiPeripheralProvider(IMultiPeripheralProvider provider) {
		if(peripheralRegistry != null) {
			peripheralRegistry.registerPeripheralProvider(provider);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		/*if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5) && Config.GREGTECH_RECIPES) { TODO GregTech
			ModRecipes.instance = new GregTechRecipes();
		} else */
		{
			ModRecipes.instance = new ModRecipes();
		}
		if(ModRecipes.instance != null) {
			ModRecipes.instance.registerRecipes();
		} else {
			log.error("Could not register recipes, an error occured!");
		}

		// Mod compat - GregTech
		/*if(itemTape != null && Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5) && itemPartsGreg != null) { TODO GregTech
			GregTechRecipes.registerGregTechTapeRecipes();
		}*/

		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers.postInit();
		}

		if(Mods.API.hasAPI(Mods.API.BuildCraftStatements)) {
			TriggerProvider.initialize();
			ActionProvider.initialize();
			StatementParameters.initialize();
		}

		if(Mods.isLoaded(Mods.TIS3D) && tis3D != null) {
			tis3D.postInit();
		}
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {
		Computronics.storage = new StorageManager();
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft.serverStart();
		}
	}

	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		storage = null;
		proxy.onServerStop();
	}

	/**
	 * You need to call this between Computronics' preInit and init phase
	 * <p/>
	 * using {@link FMLInterModComms#sendMessage}.
	 * <p/>
	 * Example:
	 * FMLInterModComms.sendMessage("Computronics", "addmultiperipherals", "pl.asie.computronics.cc.multiperipheral.MultiPeripheralRegistry.register")
	 * @see IMultiPeripheralRegistry
	 */
	@EventHandler
	@SuppressWarnings("unchecked")
	public void receiveIMC(FMLInterModComms.IMCEvent event) {
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			ImmutableList<FMLInterModComms.IMCMessage> messages = event.getMessages();
			for(FMLInterModComms.IMCMessage message : messages) {
				if(message.key.equalsIgnoreCase("addmultiperipherals") && message.isStringMessage()) {
					if(peripheralRegistry != null) {
						try {
							String methodString = message.getStringValue();
							String[] methodParts = methodString.split("\\.");
							String methodName = methodParts[methodParts.length - 1];
							String className = methodString.substring(0, methodString.length() - methodName.length() - 1);
							try {
								Class c = Class.forName(className);
								Method method = c.getDeclaredMethod(methodName, IMultiPeripheralRegistry.class);
								method.invoke(null, peripheralRegistry);
							} catch(ClassNotFoundException e) {
								log.warn("Could not find class " + className, e);
							} catch(NoSuchMethodException e) {
								log.warn("Could not find method " + methodString, e);
							} catch(Exception e) {
								log.warn("Exception while trying to call method " + methodString, e);
							}
						} catch(Exception e) {
							log.warn("Exception while trying to register a MultiPeripheral", e);
						}
					} else {
						log.warn(String.format(Locale.ENGLISH, "Mod (%s) tried to register MultiPeripheral before Computronics' preInit!", message.getSender()));
					}
				}
			}
		}
	}
}
