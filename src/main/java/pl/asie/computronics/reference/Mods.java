package pl.asie.computronics.reference;

import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;

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
		Waila = "Waila";

	//APIs
	public static class API {
		public static final String
			BuildCraftStatements = "BuildCraftAPI|statements",
			BuildCraftTiles = "BuildCraftAPI|tiles",
			CoFHAPI_Energy = "CoFHAPI|energy",
			DraconicEvolution = "DraconicEvolution|API";

		private static HashMap<String, ArtifactVersion> apiList;

		public static ArtifactVersion getVersion(String name) {
			if(apiList == null) {
				apiList = new HashMap<String, ArtifactVersion>();
				Iterable<? extends ModContainer> apis = ModAPIManager.INSTANCE.getAPIList();

				for(ModContainer api : apis) {
					apiList.put(api.getModId(), api.getProcessedVersion());
				}
			}

			if(apiList.containsKey(name)) {
				return apiList.get(name);
			}
			throw new IllegalArgumentException("API '" + name + "' does not exist!");
		}

		public static boolean hasVersion(String name, String version) {
			if(ModAPIManager.INSTANCE.hasAPI(name)) {
				ArtifactVersion v1 = VersionParser.parseVersionReference(name + "@" + version);
				ArtifactVersion v2 = getVersion(name);
				return v1.containsVersion(v2);
			}
			return false;
		}
	}
}
