package pl.asie.computronics.integration;

import pl.asie.computronics.tile.inventory.TileTapeDrive;
import mcp.mobius.waila.api.IWailaRegistrar;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		reg.registerBodyProvider(new WailaTapeDrive(), TileTapeDrive.class);
	}
}
