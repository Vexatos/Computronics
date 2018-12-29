package pl.asie.lib.reference;

import com.google.common.collect.Iterables;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class Mods {

	//The mod itself
	public static final String
		AsieLib = "asielib",
		AsieLib_NAME = "AsieLib";

	//Other mods
	public static final String
		AE2 = "appliedenergistics2",
		IC2 = "IC2",
		IC2Classic = "IC2-Classic",
	//GregTech = "gregtech",
	Mekanism = "Mekanism",
		ProjectRed = "ProjRed|Core",
		RedLogic = "RedLogic",
		RedstoneFlux = "redstoneflux";

	//Other APIs
	public static class API {

		public static final String
			BuildCraftTools = "BuildCraftAPI|tools",
			CharsetWires = "CharsetAPI|Wires",
			CoFHBlocks = "CoFHAPI|block",
			CoFHItems = "CoFHAPI|item",
			CoFHTileEntities = "CoFHAPI|tileentity",
			EiraIRC = "EiraIRC|API",
			EnderIOTools = "enderioapi|tools",
			OpenComputersInternal = "opencomputersapi|internal";

		public static boolean hasAPI(String name) {
			return ModAPIManager.INSTANCE.hasAPI(name);
		}
	}

	public static boolean isLoaded(String name) {
		return Loader.isModLoaded(name);
	}

	// Mod versions

	private static HashMap<String, ArtifactVersion> modVersionList;

	public static ArtifactVersion getVersion(String name) {
		if(modVersionList == null) {
			modVersionList = new HashMap<String, ArtifactVersion>();

			for(ModContainer api : Iterables.concat(Loader.instance().getActiveModList(), ModAPIManager.INSTANCE.getAPIList())) {
				modVersionList.put(api.getModId(), api.getProcessedVersion());
			}
		}

		if(modVersionList.containsKey(name)) {
			return modVersionList.get(name);
		}
		throw new IllegalArgumentException("Mod/API '" + name + "' does not exist!");
	}

	public static boolean hasVersion(String name, String version) {
		if(isLoaded(name) || API.hasAPI(name)) {
			ArtifactVersion v1 = VersionParser.parseVersionReference(name + "@" + version);
			ArtifactVersion v2 = getVersion(name);
			return v1.containsVersion(v2);
		}
		return false;
	}

	// Energy related

	private static boolean checkedEnergyMods = false;
	private static boolean hasEnergyMod = false;

	public static boolean hasEnergyMod() {
		if(!checkedEnergyMods) {
			hasEnergyMod = API.hasAPI(Mods.RedstoneFlux)
				|| isLoaded(IC2)
				|| isLoaded(IC2Classic);
			checkedEnergyMods = true;
		}
		return hasEnergyMod;
	}

	private static boolean checkedBundledMods = false;
	private static boolean hasBundledMod = false;

	public static boolean hasBundledRedstoneMod() {
		if(!checkedBundledMods) {
			hasBundledMod = API.hasAPI(API.CharsetWires);
			checkedBundledMods = true;
		}
		return hasBundledMod;
	}
}
