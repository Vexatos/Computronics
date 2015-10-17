package pl.asie.computronics.reference;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Vexatos
 */
public class Compat {

	public static final String
		RedLogic_Lamps = "enableRedLogicLamps",
		MFR_DSU = "enableDeepStorageUnit",
		FSP_Steam_Transporter = "enableFlaxbeardSteamTransporters",
		FZ_ChargePeripheral = "enableFactorizationChargePeripheral",
		Railcraft_Routing = "enableRailcraftRoutingComponents",
		AE2_SpatialIO = "enableAE2SpatialIOComponent",
		EnderIO = "enableEnderIOComponents",
		RedstoneFlux = "enableRedstoneFluxPeripheral",
		BetterStorage_Crates = "enableBetterStorageCrates",
		GregTech_Machines = "enableGregTechMachines",
		GregTech_DigitalChests = "enableGregTechDigitalChests",
		BuildCraft_Drivers = "enableBuildCraftDrivers",
		DraconicEvolution = "enableDraconicEvolutionEnergyStoragePeripheral",
		MekanismEnergy = "enableMekanismEnergyStoragePeripheral",
		StorageDrawers = "enableStorageDrawersDriver",
		Flamingo = "enableFlamingoDriver",
		AW_Mannequins = "enableAWMannequinDriver";

	public static final String Compatibility = "modCompatibility";

	private final Configuration config;

	public Compat(Configuration config) {
		this.config = config;
	}

	public boolean isCompatEnabled(String key) {
		return config.get(Compatibility, key, true).getBoolean(true);
	}
}
