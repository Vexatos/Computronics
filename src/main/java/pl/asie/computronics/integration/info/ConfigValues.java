package pl.asie.computronics.integration.info;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public enum ConfigValues {

	OCAddress(Mods.Computronics + ".enableOCAddress", Mods.OpenComputers),
	Tape(Mods.Computronics + ".enableTape"),
	TapeName(Mods.Computronics + ".enableTapeName"),
	DriveState(Mods.Computronics + ".enableDriveState"),
	RelayBound(Mods.Computronics + ".enableRelayBound", Mods.Railcraft),
	LampColor(Mods.Computronics + ".enableLampColor");

	private String key;
	private boolean defvalue;
	private String modID;

	ConfigValues(String key) {
		this(key, true);
	}

	ConfigValues(String key, boolean defvalue) {
		this.key = key;
		this.defvalue = defvalue;
	}

	ConfigValues(String key, String modID) {
		this(key, modID, true);
	}

	ConfigValues(String key, String modID, boolean defvalue) {
		this.key = key;
		this.defvalue = defvalue;
		this.modID = modID;
	}

	@Optional.Method(modid = Mods.Waila)
	private void registerConfigRemote(IWailaRegistrar reg) {
		reg.addConfigRemote(Mods.Computronics_NAME, key, defvalue);
	}

	@Optional.Method(modid = Mods.Waila)
	static void registerConfigs(IWailaRegistrar reg) {
		for(ConfigValues value : ConfigValues.values()) {
			if(value.modID == null || Mods.isLoaded(value.modID) || Mods.API.hasAPI(value.modID)) {
				value.registerConfigRemote(reg);
			}
		}
	}

	@Optional.Method(modid = Mods.Waila)
	public boolean getValue(IWailaConfigHandler config) {
		return (this.modID == null || Mods.isLoaded(this.modID) || Mods.API.hasAPI(this.modID))
			&& config.getConfig(key, defvalue);
	}
}
