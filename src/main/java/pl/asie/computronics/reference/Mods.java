package pl.asie.computronics.reference;

import cpw.mods.fml.common.versioning.ArtifactVersion;

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
		OpenComputers = "OpenComputers",
		ComputerCraft = "ComputerCraft",
		NedoComputers = "nedocomputers";

	//Other mods
	public static final String
		AE2 = "appliedenergistics2",
		BetterStorage = "betterstorage",
		BuildCraftCore = "BuildCraft|Core",
		BuildCraftTransport = "BuildCraft|Transport",
		EnderIO = "EnderIO",
		Factorization = "factorization",
		Forestry = "Forestry",
		FSP = "Steamcraft",
		GregTech = "gregtech",
		JABBA = "JABBA",
		MFR = "MineFactoryReloaded",
		OpenBlocks = "OpenBlocks",
		OpenPeripheral = "OpenPeripheralCore",
		RedLogic = "RedLogic",
		ProjectRed = "ProjRed|Core",
		Railcraft = "Railcraft",
		StorageDrawers = "StorageDrawers",
		Waila = "Waila";

	//APIs
	public static class API {

		public static final String
			BuildCraftBlueprints = "BuildCraftAPI|blueprints",
			BuildCraftStatements = "BuildCraftAPI|statements",
			BuildCraftTiles = "BuildCraftAPI|tiles",
			CoFHAPI_Energy = "CoFHAPI|energy",
			DraconicEvolution = "DraconicEvolution|API",
			Gendustry = "gendustryAPI",
			Mekanism_Energy = "MekanismAPI|energy";

		private static HashMap<String, ArtifactVersion> apiList;

		public static ArtifactVersion getVersion(String name) {
			return pl.asie.lib.reference.Mods.API.getVersion(name);
		}

		public static boolean hasVersion(String name, String version) {
			return pl.asie.lib.reference.Mods.API.hasVersion(name, version);
		}

		public static boolean hasAPI(String name) {
			return pl.asie.lib.reference.Mods.API.hasAPI(name);
		}
	}

	public static boolean isLoaded(String name) {
		return pl.asie.lib.reference.Mods.isLoaded(name);
	}

	public static boolean hasEnergyMod() {
		return pl.asie.lib.reference.Mods.hasEnergyMod();
	}
}
