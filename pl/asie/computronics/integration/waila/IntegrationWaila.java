package pl.asie.computronics.integration.waila;

import pl.asie.computronics.tile.TileTapeDrive;
import mcp.mobius.waila.api.IWailaRegistrar;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		reg.registerBodyProvider(new WailaTapeDrive(), TileTapeDrive.class);
	}
}
