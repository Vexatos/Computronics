package pl.asie.computronics.integration.charset;

import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharset {

	public static CCBundledRedstoneIntegration bundledRedstone;

	public void preInit() {
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			bundledRedstone = new CCBundledRedstoneIntegration();
		}
	}

}
