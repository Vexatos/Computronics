package pl.asie.computronics.reference;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.lib.util.EnergyConverter;

/**
 * @author Vexatos
 */
public class Config {

	public final Configuration config;

	public static int CHATBOX_DISTANCE = 40;
	public static int CAMERA_DISTANCE = 32;
	public static int TAPEDRIVE_DISTANCE = 24;
	public static int TAPEDRIVE_BUFFER_MS = 750;
	public static int RADAR_RANGE = 8;
	public static boolean RADAR_ONLY_DISTANCE = false;
	public static boolean CIPHER_CAN_LOCK = true;
	public static double CIPHER_ENERGY_STORAGE = 1600.0;
	public static double CIPHER_KEY_CONSUMPTION = 1600.0;
	public static double CIPHER_WORK_CONSUMPTION = 16.0;
	public static double RADAR_ENERGY_COST_OC = 5.0;
	public static double RADAR_CC_TIME = 0.5;
	public static double FX_ENERGY_COST = 0.2;
	public static double SOUND_ENERGY_COST = 1.0;
	public static double SPOOFING_ENERGY_COST = 0.2;
	public static String CHATBOX_PREFIX = "ChatBox";
	public static double LOCOMOTIVE_RELAY_RANGE = 128.0;
	public static boolean GREGTECH_RECIPES = false;
	public static boolean NON_OC_RECIPES = false;
	public static boolean FORESTRY_BEES = true;
	public static boolean BUILDCRAFT_STATION = true;

	public static boolean OC_ROBOT_UPGRADES;
	public static boolean OC_CARD_FX;
	public static boolean OC_CARD_SPOOF;
	public static boolean OC_CARD_SOUND;
	public static boolean OC_CARD_BOOM;

	public static boolean CC_OPEN_MULTI_PERIPHERAL = true;
	public static boolean CC_ALL_MULTI_PERIPHERALS = true;
	public static boolean CC_ALWAYS_FIRST = true;

	public static String TAPE_LENGTHS;
	public static boolean REDSTONE_REFRESH, CHATBOX_CREATIVE;

	public static boolean MUST_UPDATE_TILE_ENTITIES = false;

	public Config(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
	}

	public boolean isEnabled(String name, boolean def) {
		return config.get("enable", name, def).getBoolean(def);
	}

	private double convertRFtoOC(double v) {
		return EnergyConverter.convertEnergy(v, "RF", "OC");
	}

	public void preInit() {
		// Configs

		// Camera
		CAMERA_DISTANCE = config.getInt("maxDistance", "camera", 32, 16, 256, "The maximum camera distance, in blocks.");

		// Chat Box
		CHATBOX_CREATIVE = config.getBoolean("enableCreative", "chatbox", true, "Enable Creative Chat Boxes.");
		CHATBOX_DISTANCE = config.getInt("maxDistance", "chatbox", 40, 4, 32767, "The maximum chat box distance, in blocks.");
		CHATBOX_PREFIX = config.getString("prefix", "chatbox", "ChatBox", "The Chat Box's default prefix.");

		// Cipher Block
		CIPHER_CAN_LOCK = config.getBoolean("canLock", "cipherblock", true, "Decides whether Cipher Blocks can or cannot be locked.");

		if(Loader.isModLoaded(Mods.OpenComputers)) {
			//Advanced Cipher Block
			CIPHER_ENERGY_STORAGE = convertRFtoOC(
				config.getFloat("cipherEnergyStorage", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Chipher Block can store"));
			CIPHER_KEY_CONSUMPTION = convertRFtoOC(
				config.getFloat("cipherKeyConsumption", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Cipher Block should consume for creating a key set"));
			CIPHER_WORK_CONSUMPTION = convertRFtoOC(
				config.getFloat("cipherWorkConsumption", "power", 160.0f, 0.0f, 100000.0f, "How much base energy the Advanced Cipher Block should consume per encryption/decryption task. It will consume this value + 2*(number of characters in message)"));

			OC_ROBOT_UPGRADES = config.get("enable.opencomputers", "robotUpgrades", true).getBoolean(true);
			OC_CARD_FX = config.get("enable.opencomputers", "particleCard", true).getBoolean(true);
			OC_CARD_SPOOF = config.get("enable.opencomputers", "spoofingCard", true).getBoolean(true);
			OC_CARD_SOUND = config.get("enable.opencomputers", "soundCard", true).getBoolean(true);
			OC_CARD_BOOM = config.get("enable.opencomputers", "boomCard", true).getBoolean(true);

			// Particle Card
			FX_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocParticleCardCostPerParticle", "power", 2.0f, 0.0f, 10000.0f, "How much energy 1 particle emission should take."));
			//Spoofing Card
			SPOOFING_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocSpoofingCardCostPerMessage", "power", 2.0f, 0.0f, 10000.0f, "How much energy sending one spoofed message should take"));
			// Beep Card
			SOUND_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocBeepCardCostPerSound", "power", 10.0f, 0.0f, 10000.0f, "How much energy a single beep will cost for 1 second"));

			NON_OC_RECIPES = config.getBoolean("easyRecipeMode", "recipes", false, "Set this to true to make some recipes not require OpenComputers blocks and items");

			if(Loader.isModLoaded(Mods.Forestry)) {
				FORESTRY_BEES = config.getBoolean("opencomputersBees", "enable.forestry", true, "Set this to false to disable Forestry bee species for OpenComputers");
			}
			if(Loader.isModLoaded(Mods.BuildCraftTransport) && Loader.isModLoaded(Mods.BuildCraftCore)) {
				BUILDCRAFT_STATION = config.getBoolean("droneDockingStation", "enable.buildcraft", true, "Set this to false to disable the Drone Docking Station for OpenComputers");
			}
		}

		if(Loader.isModLoaded(Mods.ComputerCraft)) {
			if(Loader.isModLoaded(Mods.OpenPeripheral)) {
				CC_OPEN_MULTI_PERIPHERAL = config.getBoolean("openMultiPeripheral", "computercraft.multiperipheral", true, "Set this to false to disable MultiPeripheral compatibility with OpenPeripheral peripherals");
			}
			CC_ALL_MULTI_PERIPHERALS = config.getBoolean("allMultiPeripherals", "computercraft.multiperipheral", true, "Set this to true to fix multiple mods adding peripherals to the same block not working");
			CC_ALWAYS_FIRST = config.getBoolean("alwaysFirstPeripheral", "computercraft.multiperipheral", true, "If this is true, the Computronics MultiPeripheral system will almost always be the one recognized by ComputerCraft");
			config.setCategoryComment("computercraft.multiperipheral", "If all of these options are set to true, Computronics will fix almost every conflict with multiple mods adding peripherals to the same block");
			if(CC_OPEN_MULTI_PERIPHERAL && CC_ALL_MULTI_PERIPHERALS && CC_ALWAYS_FIRST) {
				Logger cpx = LogManager.getLogger(Mods.Computronics_NAME);
				Logger cc = LogManager.getLogger(Mods.ComputerCraft);
				cpx.info("Hey, ComputerCraft! Guess what!");
				cc.info("What?");
				cpx.info("I fixed your peripheral system!");
				cc.info("You did WHAT?!");
				cpx.info("Now peripherals are being properly handled in case multiple mods register peripherals for the same block, isn't that amazing?");
				cc.info("Are you serious?");
				cpx.info("Yes I am. Now be quiet and let Minecraft continue to load.");
				cc.info("...");
			}
		}

		// Radar
		RADAR_RANGE = config.getInt("maxRange", "radar", 8, 0, 256, "The maximum range of the Radar.");
		RADAR_ONLY_DISTANCE = config.getBoolean("onlyOutputDistance", "radar", false, "Stop Radars from outputting X/Y/Z coordinates and instead only output the distance from an entity.");

		// Tape Drive
		TAPEDRIVE_BUFFER_MS = config.getInt("audioPreloadMs", "tapedrive", 750, 500, 10000, "The amount of time (in milliseconds) used for pre-buffering the tape for audio playback. If you get audio playback glitches in SMP/your TPS is under 20, RAISE THIS VALUE!");
		TAPEDRIVE_DISTANCE = config.getInt("hearingDistance", "tapedrive", 24, 0, 64, "The distance up to which Tape Drives can be heard.");
		TAPE_LENGTHS = config.getString("tapeLengths", "tapedrive", "4,8,16,32,64,2,6,16,128,128", "The lengths of the computronics tapes. Should be 10 numbers separated by commas");

		// General
		REDSTONE_REFRESH = config.getBoolean("enableTickingRedstoneSupport", "general", true, "Set whether some machines should stop being tickless in exchange for redstone output support.");

		// Power
		RADAR_ENERGY_COST_OC = convertRFtoOC(
			config.getFloat("radarCostPerBlock", "power", 50.0f, 0.0f, 10000.0f, "How much energy each 1-block distance takes by OpenComputers radars."));

		// Railcraft integration
		if(Loader.isModLoaded(Mods.Railcraft)) {
			LOCOMOTIVE_RELAY_RANGE = (double) config.getInt("locomotiveRelayRange", "railcraft", 128, 0, 512, "The range of Locomotive Relays in Blocks.");
		}

		// GregTech recipe mode
		if(Loader.isModLoaded(Mods.GregTech)) {
			GREGTECH_RECIPES = config.getBoolean("gtRecipeMode", "recipes", true, "Set this to true to enable GregTech-style recipes");
		}
	}

	public void setCategoryComment(String category, String comment) {
		config.setCategoryComment(category, comment);
	}

	public void save() {
		config.save();
	}
}
