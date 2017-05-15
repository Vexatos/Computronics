package pl.asie.computronics.reference;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import pl.asie.computronics.Computronics;
import pl.asie.lib.util.EnergyConverter;

/**
 * @author Vexatos
 */
public class Config {

	public final Configuration config;

	public static int CHATBOX_DISTANCE = 40;
	public static boolean CHATBOX_MAGIC = false;
	public static int CAMERA_DISTANCE = 32;
	public static int TAPEDRIVE_DISTANCE = 24;
	public static int TAPEDRIVE_BUFFER_MS = 750;
	public static int PORTABLE_TAPEDRIVE_DISTANCE = 8;
	public static int RADAR_RANGE = 8;
	public static int FX_RANGE = 256;
	public static boolean RADAR_ONLY_DISTANCE = true;
	public static boolean CIPHER_CAN_LOCK = true;
	public static double CIPHER_ENERGY_STORAGE = 1600.0;
	public static double CIPHER_KEY_CONSUMPTION = 1600.0;
	public static double CIPHER_WORK_CONSUMPTION = 16.0;
	public static double RADAR_ENERGY_COST_OC = 5.0;
	public static double RADAR_CC_TIME = 0.5;
	public static double FX_ENERGY_COST = 0.2;
	public static double BEEP_ENERGY_COST = 1.0;
	public static double SOUND_CARD_ENERGY_COST = 1.0;
	public static double SPOOFING_ENERGY_COST = 0.2;
	public static double COLORFUL_UPGRADE_COLOR_CHANGE_COST = 0.2;
	public static double LIGHT_BOARD_COLOR_CHANGE_COST = 0.2;
	public static double LIGHT_BOARD_COLOR_MAINTENANCE_COST = 0.02;
	public static double BOOM_BOARD_MAINTENANCE_COST = 0.02;
	public static double RACK_CAPACITOR_CAPACITY = 7500;
	public static double SWITCH_BOARD_MAINTENANCE_COST = 0.02;
	public static String CHATBOX_PREFIX = "ChatBox";
	public static double LOCOMOTIVE_RELAY_RANGE = 128.0;
	public static double LOCOMOTIVE_RELAY_BASE_POWER = 20.0;
	public static boolean LOCOMOTIVE_RELAY_CONSUME_CHARGE = true;
	public static boolean TICKET_MACHINE_CONSUME_RF = true;
	public static boolean GREGTECH_RECIPES = false;
	public static boolean NON_OC_RECIPES = false;
	public static boolean FORESTRY_BEES = true;
	public static boolean BUILDCRAFT_STATION = true;

	public static int SOUND_SAMPLE_RATE = 44100;
	public static byte SOUND_VOLUME = 32;
	public static int SOUND_RADIUS = 24;
	public static int SOUND_CARD_MAX_DELAY = 5000; // TODO
	public static int SOUND_CARD_QUEUE_SIZE = 1024; // TODO
	public static int SOUND_CARD_CHANNEL_COUNT = 8; // TODO

	public static boolean TTS_ENABLED;
	public static int TTS_MAX_LENGTH = 300; // TODO

	public static boolean OC_UPGRADE_CAMERA;
	public static boolean OC_UPGRADE_CHATBOX;
	public static boolean OC_UPGRADE_RADAR;
	public static boolean OC_CARD_FX;
	public static boolean OC_CARD_SPOOF;
	public static boolean OC_CARD_BEEP;
	public static boolean OC_CARD_BOOM;
	public static boolean OC_UPGRADE_COLORFUL;
	public static boolean OC_CARD_NOISE;
	public static boolean OC_CARD_SOUND;
	public static boolean OC_BOARD_LIGHT;
	public static boolean OC_BOARD_BOOM;
	public static boolean OC_BOARD_CAPACITOR;
	public static boolean OC_BOARD_SWITCH;
	public static boolean OC_UPGRADE_SPEECH;

	public static boolean OC_MAGICAL_MEMORY;

	public static boolean CC_OPEN_MULTI_PERIPHERAL = true;
	public static boolean CC_ALL_MULTI_PERIPHERALS = true;
	public static boolean CC_ALWAYS_FIRST = true;

	public static boolean TIS3D_MODULE_COLORFUL = true;
	public static boolean TIS3D_MODULE_TAPE_READER = true;
	public static boolean TIS3D_MODULE_BOOM = true;

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
		CHATBOX_MAGIC = config.getBoolean("makeMagical", "chatbox", false, "Make the normal Chat Box have no range limit and work interdimensionally.");
		CHATBOX_PREFIX = config.getString("prefix", "chatbox", "ChatBox", "The Chat Box's default prefix.");

		// Cipher Block
		CIPHER_CAN_LOCK = config.getBoolean("canLock", "cipherblock", true, "Decides whether Cipher Blocks can or cannot be locked.");

		SOUND_SAMPLE_RATE = config.getInt("soundSampleRate", "sound.client", 44100, 0, Integer.MAX_VALUE, "The sample rate used for generating sounds. Modify at your own risk.");
		SOUND_VOLUME = (byte) config.getInt("soundVolume", "sound.client", 127, 0, Byte.MAX_VALUE, "The base volume of generated sounds.");
		SOUND_RADIUS = config.getInt("soundRadius", "sound.client", 24, 0, 64, "The radius in which generated sounds can be heard.");

		TTS_ENABLED = config.getBoolean("enableTextToSpeech", "tts", true, "Enable Text To Speech. To use it, install MaryTTS, a language and a corresponding voice into the marytts directory of your minecraft instance. For installation instructions, see http://wiki.vex.tty.sh/wiki:computronics:mary");
		TTS_MAX_LENGTH = config.getInt("maxPhraseLength", "tts", 300, 0, 100000, "The maximum number of text bytes the speech box can process at a time.");

		if(Mods.isLoaded(Mods.OpenComputers)) {
			//Advanced Cipher Block
			CIPHER_ENERGY_STORAGE = convertRFtoOC(
				config.getFloat("cipherEnergyStorage", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Chipher Block can store"));
			CIPHER_KEY_CONSUMPTION = convertRFtoOC(
				config.getFloat("cipherKeyConsumption", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Cipher Block should consume for creating a key set"));
			CIPHER_WORK_CONSUMPTION = convertRFtoOC(
				config.getFloat("cipherWorkConsumption", "power", 160.0f, 0.0f, 100000.0f, "How much base energy the Advanced Cipher Block should consume per encryption/decryption task. It will consume this value + 2*(number of characters in message)"));

			OC_UPGRADE_CAMERA = config.get("enable.opencomputers", "cameraUpgrade", true).getBoolean(true);
			OC_UPGRADE_CHATBOX = config.get("enable.opencomputers", "chatboxUpgrade", true).getBoolean(true);
			OC_UPGRADE_RADAR = config.get("enable.opencomputers", "radarUpgrade", true).getBoolean(true);
			OC_CARD_FX = config.get("enable.opencomputers", "particleCard", true).getBoolean(true);
			OC_CARD_SPOOF = config.get("enable.opencomputers", "spoofingCard", true).getBoolean(true);
			OC_CARD_BEEP = config.get("enable.opencomputers", "beepCard", true).getBoolean(true);
			OC_CARD_BOOM = config.get("enable.opencomputers", "boomCard", true).getBoolean(true);
			OC_UPGRADE_COLORFUL = config.get("enable.opencomputers", "colorfulUpgrade", true).getBoolean(true);
			OC_CARD_NOISE = config.get("enable.opencomputers", "noiseCard", true).getBoolean(true);
			OC_CARD_SOUND = config.get("enable.opencomputers", "soundCard", true).getBoolean(true);
			OC_BOARD_LIGHT = config.get("enable.opencomputers", "lightBoard", true).getBoolean(true);
			OC_BOARD_BOOM = config.get("enable.opencomputers", "boomBoard", true).getBoolean(true);
			OC_BOARD_CAPACITOR = config.get("enable.opencomputers", "rackCapacitor", true).getBoolean(true);
			OC_BOARD_SWITCH = config.get("enable.opencomputers", "switchBoard", true).getBoolean(true);
			OC_UPGRADE_SPEECH = config.get("enable.opencomputers", "speechUpgrade", true).getBoolean(true);

			OC_MAGICAL_MEMORY = config.get("enable.opencomputers", "magicalMemory", true).getBoolean(true);

			if(OC_CARD_SOUND) {
				SOUND_CARD_MAX_DELAY = config.getInt("ocSoundCardMaxDelay", "sound", SOUND_CARD_MAX_DELAY, 0, Integer.MAX_VALUE, "Maximum delay allowed in a sound card's instruction queue, in milliseconds");
				SOUND_CARD_QUEUE_SIZE = config.getInt("ocSoundCardQueueSize", "sound", SOUND_CARD_QUEUE_SIZE, 0, Integer.MAX_VALUE, "Maximum  number of instructons allowed in a sound cards instruction queue. This directly affects the maximum size of the packets sent to the client.");
				SOUND_CARD_CHANNEL_COUNT = config.getInt("ocSoundCardChannelCount", "sound", SOUND_CARD_CHANNEL_COUNT, 1, 65536, "The number of audio channels each sound card has.");
			}

			// Particle Card
			FX_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocParticleCardCostPerParticle", "power", 2.0f, 0.0f, 10000.0f, "How much energy 1 particle emission should take. Multiplied by the distance to the target."));
			//Spoofing Card
			SPOOFING_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocSpoofingCardCostPerMessage", "power", 2.0f, 0.0f, 10000.0f, "How much energy sending one spoofed message should take"));
			// Beep Card
			BEEP_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocBeepCardCostPerSound", "power", 10.0f, 0.0f, 10000.0f, "How much energy a single beep will cost for 1 second"));
			// Sound Card
			SOUND_CARD_ENERGY_COST = convertRFtoOC(
				config.getFloat("ocSoundCardCostPerSecond", "power", 10.0f, 0.0f, 10000.0f, "How much energy the sound card will consume per second of processed sound."));
			// Colorful Upgrade
			COLORFUL_UPGRADE_COLOR_CHANGE_COST = convertRFtoOC(
				config.getFloat("ocColorfulUpgradeColorChangeCost", "power", 2.0f, 0.0f, 10000.0f, "How much energy changing the color of the Colorful Upgrade will cost"));

			// Rack Mountables
			LIGHT_BOARD_COLOR_CHANGE_COST = convertRFtoOC(
				config.getFloat("ocLightBoardColorChangeCost", "power", 2.0f, 0.0f, 10000.0f, "How much energy changing the color or state of a Light Board's light will cost"));
			LIGHT_BOARD_COLOR_MAINTENANCE_COST = convertRFtoOC(
				config.getFloat("ocLightBoardColorMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Light Board's light running. Note that this value is consumed for each active light on the board."));
			BOOM_BOARD_MAINTENANCE_COST = convertRFtoOC(
				config.getFloat("ocBoomBoardMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Server Self-Destructor active."));
			RACK_CAPACITOR_CAPACITY = convertRFtoOC(
				config.getFloat("ocRackCapacitorCapacity", "power", 7500f, 0.0f, 10000.0f, "How much energy a Rack Capacitor can store."));
			SWITCH_BOARD_MAINTENANCE_COST = convertRFtoOC(
				config.getFloat("ocSwitchBoardMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Switch Board's switch active. Note that this value is consumed for each active switch on the board."));

			if(Mods.isLoaded(Mods.Railcraft)) {
				LOCOMOTIVE_RELAY_BASE_POWER = convertRFtoOC(
					config.getFloat("locomotiveRelayBasePower", "power.railcraft", 20.0f, 0.0f, 10000.0f, "How much base energy the Locomotive Relay consumes per operation"));
			}

			NON_OC_RECIPES = config.getBoolean("easyRecipeMode", "recipes", false, "Set this to true to make some recipes not require OpenComputers blocks and items");

			if(Mods.hasVersion(Mods.Forestry, Mods.Versions.Forestry)) {
				FORESTRY_BEES = config.getBoolean("opencomputersBees", "enable.forestry", true, "Set this to false to disable Forestry bee species for OpenComputers");
			}
			if(Mods.isLoaded(Mods.BuildCraftTransport) && Mods.isLoaded(Mods.BuildCraftCore)) {
				BUILDCRAFT_STATION = config.getBoolean("droneDockingStation", "enable.buildcraft", true, "Set this to false to disable the Drone Docking Station for OpenComputers");
			}
		}

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			if(Mods.isLoaded(Mods.OpenPeripheral)) {
				CC_OPEN_MULTI_PERIPHERAL = config.getBoolean("openMultiPeripheral", "computercraft.multiperipheral", true, "Set this to false to disable MultiPeripheral compatibility with OpenPeripheral peripherals");
			}
			CC_ALL_MULTI_PERIPHERALS = config.getBoolean("allMultiPeripherals", "computercraft.multiperipheral", true, "Set this to true to fix multiple mods adding peripherals to the same block not working");
			CC_ALWAYS_FIRST = config.getBoolean("alwaysFirstPeripheral", "computercraft.multiperipheral", true, "If this is true, the Computronics MultiPeripheral system will almost always be the one recognized by ComputerCraft");
			config.setCategoryComment("computercraft.multiperipheral", "If all of these options are set to true, Computronics will fix almost every conflict with multiple mods adding peripherals to the same block");
			if(CC_OPEN_MULTI_PERIPHERAL && CC_ALL_MULTI_PERIPHERALS && CC_ALWAYS_FIRST) {
				Computronics.log.info("Multiperipheral system for ComputerCraft engaged. Hooray!");
				Computronics.log.info("Multiple mods registering peripherals for the same block now won't be a problem anymore.");
			}
		}

		if(Mods.isLoaded(Mods.TIS3D)) {
			TIS3D_MODULE_COLORFUL = config.get("enable.tis3d", "colorfulModule", true).getBoolean(true);
			TIS3D_MODULE_TAPE_READER = config.get("enable.tis3d", "tapeReaderModule", true).getBoolean(true);
			TIS3D_MODULE_BOOM = config.get("enable.tis3d", "boomModule", true).getBoolean(true);
		}

		// Radar
		RADAR_RANGE = config.getInt("maxRange", "radar", 8, 0, 256, "The maximum range of the Radar.");
		RADAR_ONLY_DISTANCE = config.getBoolean("onlyOutputDistance", "radar", true, "Stop Radars from outputting X/Y/Z coordinates and instead only output the distance from an entity.");

		// Particles
		FX_RANGE = config.getInt("particleRange", "particles", FX_RANGE, -1,65536, "The maximum range of particle-emitting devices. Set to -1 to make it work over any distance.");

		// Tape Drive
		TAPEDRIVE_BUFFER_MS = config.getInt("audioPreloadMs", "tapedrive", 750, 500, 10000, "The amount of time (in milliseconds) used for pre-buffering the tape for audio playback. If you get audio playback glitches in SMP/your TPS is under 20, RAISE THIS VALUE!");
		TAPEDRIVE_DISTANCE = config.getInt("hearingDistance", "tapedrive", 24, 0, 64, "The distance up to which Tape Drives can be heard.");
		TAPE_LENGTHS = config.getString("tapeLengths", "tapedrive", "4,8,16,32,64,2,6,16,128,128", "The lengths of the computronics tapes. Should be 10 numbers separated by commas");

		PORTABLE_TAPEDRIVE_DISTANCE= config.getInt("hearingDistance", "tapedrive.portable", 8, 0, 64, "The distance up to which Portable Tape Drives can be heard.");

		// General
		REDSTONE_REFRESH = config.getBoolean("enableTickingRedstoneSupport", "general", true, "Set whether some machines should stop being tickless in exchange for redstone output support.");

		// Power
		RADAR_ENERGY_COST_OC = convertRFtoOC(
			config.getFloat("radarCostPerBlock", "power", 50.0f, 0.0f, 10000.0f, "How much energy each 1-block distance takes by OpenComputers radars."));

		// Railcraft integration
		if(Mods.isLoaded(Mods.Railcraft)) {
			LOCOMOTIVE_RELAY_RANGE = (double) config.getInt("locomotiveRelayRange", "railcraft", 128, 0, 512, "The range of Locomotive Relays in Blocks.");
			LOCOMOTIVE_RELAY_CONSUME_CHARGE = config.getBoolean("locomotiveRelayConsumeCharge", "railcraft", true, "If true, the Locomotive Relay will consume"
				+ "a little bit of Railcraft charge in the locomotive everytime it is accessing the locomotive");
			TICKET_MACHINE_CONSUME_RF = config.getBoolean("ticketMachineConsumeCharge", "railcraft", true, "If true, the Ticket Machine will"
				+ "require a little bit of RF to print tickets");
		}

		// GregTech recipe mode
		if(Mods.isLoaded(Mods.GregTech)) {
			GREGTECH_RECIPES = config.getBoolean("gtRecipeMode", "recipes", true, "Set this to true to enable GregTech-style recipes");
		}

		config.setCategoryComment("power", "Every value related to energy in this section uses RF as the base power unit.");
		config.setCategoryComment("sound", "Configs for sounds generated by devices like the Beep Card.");
		if(Mods.isLoaded(Mods.OpenComputers) || Mods.isLoaded(Mods.ComputerCraft)) {
			config.setCategoryComment(Compat.Compatibility, "Set anything here to false to prevent Computronics from adding the respective Peripherals and Drivers");
		}
	}

	public void save() {
		config.save();
	}
}
