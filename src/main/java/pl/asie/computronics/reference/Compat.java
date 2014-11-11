package pl.asie.computronics.reference;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Vexatos
 */
public class Compat {

	public static final String RedLogic_Lamps = "enableRedLogicLamps";
	public static final String Bundled_Redstone = "enableBundledRedstoneProviders";
	public static final String MFR_DSU = "enableDeepStorageUnit";
	public static final String FSP_Steam_Transporter = "enableFlaxbeardSteamTransporters";
	public static final String FZ_ChargePeripheral = "enableFactorizationChargePeripheral";
	public static final String Railcraft_Routing = "enableRailcraftRoutingComponents";
	public static final String AE2_SpatialIO = "enableAE2SpatialIOComponent";
	public static final String EnderIO = "enableEnderIOComponents";
	public static final String RedstoneFlux = "enableRedstoneFluxPeripheral";
	public static final String BetterStorage_Crates = "enableBetterStorageCrates";
	public static final String GregTech_Machines = "enableGregTechMachines";
	public static final String GregTech_DigitalChests = "enableGregTechDigitalChests";

	public static final String Compatibility = "modCompatibility";

	private final Configuration config;

	public Compat(Configuration config) {
		this.config = config;
	}

	public boolean isCompatEnabled(String key) {
		return config.get(Compatibility, key, true).getBoolean(true);
	}
}
