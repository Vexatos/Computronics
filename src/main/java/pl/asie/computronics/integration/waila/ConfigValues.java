package pl.asie.computronics.integration.waila;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public enum ConfigValues {

	OCAddress(Mods.Computronics + ".enableOCAddress", Mods.OpenComputers),
	NCAddress(Mods.Computronics + ".enableNCAddress", Mods.NedoComputers),
	Tape(Mods.Computronics + ".enableTape"),
	TapeName(Mods.Computronics + ".enableTapeName"),
	DriveState(Mods.Computronics + ".enableDriveState"),
	RelayBound(Mods.Computronics + ".enableRelayBound"),
	LampColor(Mods.Computronics + ".enableLampColor");

	private String key;
	private boolean defvalue;
	private String modID;

	private ConfigValues(String key) {
		this(key, true);
	}

	private ConfigValues(String key, boolean defvalue) {
		this.key = key;
		this.defvalue = defvalue;
	}

	private ConfigValues(String key, String modID) {
		this(key, modID, true);
	}

	private ConfigValues(String key, String modID, boolean defvalue) {
		this.key = key;
		this.defvalue = defvalue;
		this.modID = modID;
	}

	private void registerConfigRemote(IWailaRegistrar reg) {
		reg.addConfigRemote(Mods.Computronics_NAME, key, defvalue);
	}

	static void registerConfigs(IWailaRegistrar reg) {
		for(ConfigValues value : ConfigValues.values()) {
			if(value.modID == null || Loader.isModLoaded(value.modID) || ModAPIManager.INSTANCE.hasAPI(value.modID)) {
				value.registerConfigRemote(reg);
			}
		}
	}

	public boolean getValue(IWailaConfigHandler config) {
		return (this.modID == null || Loader.isModLoaded(this.modID) || ModAPIManager.INSTANCE.hasAPI(this.modID))
			&& config.getConfig(key, defvalue);
	}
}
