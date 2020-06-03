package pl.asie.computronics.reference;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.util.HashMap;

/**
 * List of used mod IDs
 * @author Vexatos
 */
public class Mods {

	//The mod itself
	public static final String
		Computronics = "computronics",
		Computronics_NAME = "Computronics";

	//Computer mods
	public static final String
		OpenComputers = "opencomputers",
		ComputerCraft = "computercraft",
		TIS3D = "tis3d";

	//Other mods
	public static final String
		AE2 = "appliedenergistics2",
		Albedo = "albedo",
		ArmourersWorkshop = "armourersWorkshop",
		BetterStorage = "betterstorage",
		BuildCraftCore = "BuildCraft|Core",
		BuildCraftTransport = "BuildCraft|Transport",
		CharsetAudio = "CharsetAudio",
		CharsetLib = "CharsetLib",
		Conventional = "conventional",
		EnderIO = "enderio",
		EnderIO_PowerTools = "enderiopowertools",
		EnderIO_Machines = "enderiomachines",
		Factorization = "factorization",
		Flamingo = "flamingo",
		Forestry = "forestry",
		FSP = "Steamcraft",
		GregTech = "gregtech",
		Mirage = "mirage",
		MFR = "MineFactoryReloaded",
		OpenBlocks = "OpenBlocks",
		OpenPeripheral = "OpenPeripheralCore",
		RedLogic = "RedLogic",
		ProjectRed = "ProjRed|Core",
		Railcraft = "railcraft",
		StorageDrawers = "storagedrawers",
		TheOneProbe = "theoneprobe",
		Waila = "waila";

	//APIs
	public static class API {

		public static final String
			BuildCraftBlueprints = "BuildCraftAPI|blueprints",
			BuildCraftStatements = "BuildCraftAPI|statements",
			BuildCraftTiles = "BuildCraftAPI|tiles",
			CharsetAudio = "CharsetAPI|Audio",
			CharsetWires = "CharsetAPI|Wires",
			DraconicEvolution = "DraconicEvolution|API",
			Gendustry = "gendustryAPI",
			Mekanism_Energy = "MekanismAPI|energy",
			NoteBetter = "notebetter|api";

		public static boolean hasAPI(String name) {
			return pl.asie.lib.reference.Mods.API.hasAPI(name);
		}
	}

	// Mod Versions
	public static class Versions {

		public static final String
			BuildCraftTiles = "[1.1,)",
			Gendustry = "[2.0.0,)",
			GregTech5 = "[MC1710]",
			GregTech6 = "[GT6-MC1710]";
	}

	public static boolean isLoaded(String name) {
		return pl.asie.lib.reference.Mods.isLoaded(name);
	}

	public static ArtifactVersion getVersion(String name) {
		return pl.asie.lib.reference.Mods.getVersion(name);
	}

	public static boolean hasVersion(String name, String version) {
		return pl.asie.lib.reference.Mods.hasVersion(name, version);
	}

	public static boolean hasEnergyMod() {
		return pl.asie.lib.reference.Mods.hasEnergyMod();
	}

	public static boolean hasBundledRedstoneMod() {
		return pl.asie.lib.reference.Mods.hasBundledRedstoneMod();
	}

	private static final HashMap<String, Boolean> loadedClasses = new HashMap<String, Boolean>();

	public static boolean isClassLoaded(String className) {
		if(!loadedClasses.containsKey(className)) {
			try {
				loadedClasses.put(className, Class.forName(className) != null);
			} catch(ClassNotFoundException e) {
				loadedClasses.put(className, false);
			}
		}
		return loadedClasses.get(className);
	}
}
