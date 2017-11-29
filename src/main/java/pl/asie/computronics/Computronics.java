package pl.asie.computronics;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralRegistry;
import pl.asie.computronics.audio.DFPWMPlaybackManager;
import pl.asie.computronics.audio.SoundCardPlaybackManager;
import pl.asie.computronics.audio.tts.TextToSpeech;
import pl.asie.computronics.audio.tts.TextToSpeechLoader;
import pl.asie.computronics.block.BlockAudioCable;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.block.BlockChatBox;
import pl.asie.computronics.block.BlockCipher;
import pl.asie.computronics.block.BlockCipherAdvanced;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockIronNote;
import pl.asie.computronics.block.BlockRadar;
import pl.asie.computronics.block.BlockSpeaker;
import pl.asie.computronics.block.BlockSpeechBox;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.cc.IntegrationComputerCraft;
import pl.asie.computronics.cc.multiperipheral.MultiPeripheralRegistry;
import pl.asie.computronics.gui.providers.GuiProviderCipher;
import pl.asie.computronics.gui.providers.GuiProviderPortableTapeDrive;
import pl.asie.computronics.gui.providers.GuiProviderTapeDrive;
import pl.asie.computronics.integration.ModRecipes;
import pl.asie.computronics.integration.charset.IntegrationCharset;
import pl.asie.computronics.integration.conventional.IntegrationConventional;
import pl.asie.computronics.integration.forestry.IntegrationForestry;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;
import pl.asie.computronics.item.ItemMultipleComputronics;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.item.block.ComputronicsItemBlock;
import pl.asie.computronics.network.NetworkHandlerClient;
import pl.asie.computronics.network.NetworkHandlerServer;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.reference.Capabilities;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.PortableDriveManager;
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
import pl.asie.computronics.tile.TileSpeechBox;
import pl.asie.computronics.tile.TileTapeDrive;
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
	dependencies = "required-after:asielib;required-after:forge@[14.23.1.2555,);"
		+ "after:computercraft;after:opencomputers@[1.7,);after:tis3d@[1.2.0,);"
		+ "before:OpenPeripheralCore@[1.1,);before:OpenPeripheralApi@[3.2,);"
		+ "after:MineFactoryReloaded;after:RedLogic@[59.1.9,);after:ProjRed|Core;"
		+ "after:BuildCraft|Core@[7.2.0,);after:railcraft@[10.0,);"
		+ "after:gregtech;after:EnderIO@[1.10.2-3.0.1,);"
		+ "after:forestry@[5.7.0,);after:waila@[1.5.10,);"
		+ "after:MekanismAPI|energy@[8.0.0,);after:Flamingo@[1.7.10-1.3,);"
		+ "after:armourersWorkshop@[1.7.10-0.33,);after:theoneprobe@[1.0.5,)")
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
	public static ExecutorService rsaThreads;
	public static ServerTickHandler serverTickHandler;
	public DFPWMPlaybackManager audio;
	public int managerId;

	public SoundCardPlaybackManager soundCardAudio;
	public int soundCardManagerId;

	@SidedProxy(clientSide = "pl.asie.computronics.ClientProxy", serverSide = "pl.asie.computronics.CommonProxy")
	public static CommonProxy proxy;

	public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockAudioCable audioCable;
	public static BlockSpeaker speaker;
	public static BlockSpeechBox speechBox;
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
	public static IntegrationForestry forestry;
	public static IntegrationTIS3D tis3D;
	public static IntegrationCharset charset;

	public static ItemPortableTapeDrive portableTapeDrive;
	public static ItemTape itemTape;
	public static ItemMultipleComputronics itemParts;
	//public static ItemMultiple itemPartsGreg;

	public static IGuiProvider guiTapeDrive;
	public static IGuiProvider guiPortableTapeDrive;
	public static IGuiProvider guiCipher;

	//public ComputronicsAchievements achievements; TODO Advancements?

	public static MultiPeripheralRegistry peripheralRegistry;

	public static CreativeTabs tab = new CreativeTabs("tabComputronics") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(itemTape, 1, 0);
		}
	};

	public boolean isEnabled(String name, boolean def) {
		return config.isEnabled(name, def);
	}

	public void registerBlockWithTileEntity(BlockBase block, Class<? extends TileEntity> tile, String name) {
		registerBlockWithTileEntity(block, new ComputronicsItemBlock(block), tile, name);
	}

	public void registerBlockWithTileEntity(Block block, ItemBlock itemBlock, Class<? extends TileEntity> tile, String name) {
		GameRegistry.findRegistry(Block.class).register(block.setRegistryName(new ResourceLocation(Mods.Computronics, name)));
		GameRegistry.findRegistry(Item.class).register(itemBlock.setRegistryName(block.getRegistryName()));
		GameRegistry.registerTileEntity(tile, name);
		proxy.registerItemModel(block, 0, "computronics:" + name);
		//System.out.println("Registering " + name + " as TE " + tile.getCanonicalName());
		FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", tile.getCanonicalName());
		//IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(block);
	}

	public void registerItem(Item item, String name) {
		GameRegistry.findRegistry(Item.class).register(item.setRegistryName(new ResourceLocation(Mods.Computronics, name)));
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();

		MinecraftForge.EVENT_BUS.register(serverTickHandler = new ServerTickHandler());

		config = new Config(event);

		storage = new StorageManager();

		audio = new DFPWMPlaybackManager(proxy.isClient());

		managerId = AudioPacketRegistry.INSTANCE.registerManager(audio);

		soundCardAudio = new SoundCardPlaybackManager(proxy.isClient());

		soundCardManagerId = AudioPacketRegistry.INSTANCE.registerManager(soundCardAudio);

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
			registerItem(itemTape, "tape");
			itemTape.registerItemModels();

			/*if(Mods.isLoaded(Mods.GregTech)) {
				itemPartsGreg = new ItemPartsGreg();
				GameRegistry.registerItem(itemPartsGreg, "computronics.gt_parts");
				proxy.registerEntities();
			}*/

			itemParts = new ItemMultipleComputronics(Mods.Computronics, new String[] { "part_tape_track" });
			itemParts.setCreativeTab(tab);
			registerItem(itemParts, "parts");
			itemParts.registerItemModels();
		}

		if(isEnabled("portableTapeDrive", true)) {
			portableTapeDrive = new ItemPortableTapeDrive();
			registerItem(portableTapeDrive, "portable_tape_drive");
			guiPortableTapeDrive = new GuiProviderPortableTapeDrive();
			gui.registerGuiProvider(Computronics.guiPortableTapeDrive);
		}

		/*if(Mods.isLoaded(Mods.Railcraft)) {
			railcraft = new IntegrationRailcraft();
			railcraft.preInit(config.config);
		}*/

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft = new IntegrationComputerCraft(this);
			peripheralRegistry = new MultiPeripheralRegistry();
			computercraft.preInit();
		}

		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers = new IntegrationOpenComputers(this);
			opencomputers.preInit();
		}

		if(Config.TTS_ENABLED) {
			boolean success = TextToSpeechLoader.INSTANCE.preInit();
			if(success) {
				tts = new TextToSpeech();
				if(!tts.preInit()) {
					tts = null;
				}
			}
			if(isEnabled("speechBox", true)) {
				speechBox = new BlockSpeechBox();
				registerBlockWithTileEntity(speechBox, TileSpeechBox.class, "speech_box");
			}
		}

		if(Mods.isLoaded(Mods.TIS3D)) {
			tis3D = new IntegrationTIS3D();
			tis3D.preInit();
		}

		charset = new IntegrationCharset();
		//charset.preInit(); TODO Charset Wires

		proxy.preInit();
	}

	public static TextToSpeech tts;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ChatHandler());

		if(tapeReader != null || portableTapeDrive != null) {
			storageEventHandler = new TapeStorageEventHandler();
			MinecraftForge.EVENT_BUS.register(storageEventHandler);
		}

		if(portableTapeDrive != null) {
			MinecraftForge.EVENT_BUS.register(PortableDriveManager.INSTANCE);
		}

		FMLInterModComms.sendMessage(Mods.Waila, "register", "pl.asie.computronics.integration.info.IntegrationWaila.register");
		FMLInterModComms.sendFunctionMessage(Mods.TheOneProbe, "getTheOneProbe", "pl.asie.computronics.integration.info.IntegrationTOP");

		FMLInterModComms.sendMessage(Mods.Waila, "register", "pl.asie.computronics.integration.waila.IntegrationWaila.register");

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft.init();
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers.init();
		}

		/*if(Mods.API.hasAPI(Mods.API.BuildCraftBlueprints)) {
			IntegrationBuildCraftBuilder.INSTANCE.init();
		}*/

		if(Mods.isLoaded(Mods.TIS3D) && tis3D != null) {
			tis3D.init(compat);
		}

		if(Mods.isLoaded(Mods.Conventional)) {
			IntegrationConventional.INSTANCE.init();
		}

		//achievements = new ComputronicsAchievements(); TODO Advancements?
		//achievements.initialize();

		Capabilities.INSTANCE.init();

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

		/*if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5) && Config.GREGTECH_RECIPES) {
			ModRecipes.instance = new GregTech5Recipes();
		} else if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech6) && Config.GREGTECH_RECIPES) {
			ModRecipes.instance = new GregTech6Recipes();
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
		/*if(itemTape != null && itemPartsGreg != null) {
			if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5)) {
				GregTech5Recipes.registerStandardGregTechRecipes();
			} else if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech6)) {
				GregTech6Recipes.registerStandardGregTechRecipes();
			}
		}*/

		if(Mods.isLoaded(Mods.OpenComputers)) {
			opencomputers.postInit();
		}

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft.postInit();
		}

		/*if(Mods.API.hasAPI(Mods.API.BuildCraftStatements)) {
			TriggerProvider.initialize();
			ActionProvider.initialize();
			StatementParameters.initialize();
		}*/

		if(Mods.isLoaded(Mods.TIS3D) && tis3D != null) {
			tis3D.postInit();
		}

		charset.postInit();
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			computercraft.serverStart();
		}
	}

	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		proxy.onServerStop();
		PortableDriveManager.INSTANCE.onServerStop();
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
