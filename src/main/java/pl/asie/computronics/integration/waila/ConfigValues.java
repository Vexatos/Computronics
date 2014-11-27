package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public enum ConfigValues {

	Address(Mods.Computronics + ".enableAddress"),
	Tape(Mods.Computronics + ".enableTape"),
	TapeName(Mods.Computronics + ".enableTapeName"),
	DriveState(Mods.Computronics + ".enableDriveState"),
	RelayBound(Mods.Computronics + ".enableRelayBound"),
	LampColor(Mods.Computronics + ".enableLampColor");

	private String key;
	private boolean defvalue;

	private ConfigValues(String key) {
		this(key, true);
	}

	private ConfigValues(String key, boolean defvalue) {
		this.key = key;
		this.defvalue = defvalue;
	}

	private void registerConfigRemote(IWailaRegistrar reg) {
		reg.addConfigRemote(Mods.Computronics_NAME, key, defvalue);
	}

	static void registerConfigs(IWailaRegistrar reg) {
		for(ConfigValues value : ConfigValues.values()) {
			value.registerConfigRemote(reg);
		}
	}

	public boolean getValue(IWailaConfigHandler config) {
		return config.getConfig(key, defvalue);
	}
}
