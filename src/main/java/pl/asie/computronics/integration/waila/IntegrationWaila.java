package pl.asie.computronics.integration.waila;

import cpw.mods.fml.common.Loader;
import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileLocomotiveRelay;
import pl.asie.computronics.tile.TileTapeDrive;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		reg.registerBodyProvider(new WailaTapeDrive(), TileTapeDrive.class);
		reg.registerSyncedNBTKey("*", TileTapeDrive.class);

		if(Loader.isModLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(new WailaLocomotiveRelay(), TileLocomotiveRelay.class);
			reg.registerSyncedNBTKey("*", TileLocomotiveRelay.class);
		}
	}
}
