package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.tile.TileTapeDrive;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		//reg.registerBodyProvider(new WailaPeripheralBase(), TileEntityPeripheralBase.class);
		//registerKeys(reg, TileEntityPeripheralBase.class, "oc:node");

		reg.registerBodyProvider(new WailaTapeDrive(), TileTapeDrive.class);
		//registerKeys(reg, TileTapeDrive.class, "*");

		/*if(Loader.isModLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(new WailaLocomotiveRelay(), TileLocomotiveRelay.class);
			registerKeys(reg, TileLocomotiveRelay.class, "*");
		}*/

		ConfigValues.registerConfigs(reg);
	}

	private static void registerKeys(IWailaRegistrar reg, Class<?> clazz, String... names) {
		for(String name : names) {
			reg.registerSyncedNBTKey(name, clazz);
		}
		reg.registerSyncedNBTKey("x", clazz);
		reg.registerSyncedNBTKey("y", clazz);
		reg.registerSyncedNBTKey("z", clazz);
	}
}
