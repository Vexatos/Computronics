package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.tile.TileTapeDrive;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		reg.registerBodyProvider(new WailaTapeDrive(), TileTapeDrive.class);
	}
}
