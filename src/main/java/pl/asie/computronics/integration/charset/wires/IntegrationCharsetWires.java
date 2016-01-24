package pl.asie.computronics.integration.charset.wires;

import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharsetWires {

	public static CCBundledRedstoneIntegration bundledRedstone;

	public void preInit() {
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			bundledRedstone = new CCBundledRedstoneIntegration();
		}
	}

}
