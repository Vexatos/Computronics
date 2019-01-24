package pl.asie.computronics.integration.charset.wires;

import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharsetWires {

	public static CCBundledRedstoneIntegration bundledRedstoneCC;
	public static ComputronicsBundledRedstoneIntegration bundledRedstone;

	static {
		bundledRedstone = new ComputronicsBundledRedstoneIntegration();

		if(Mods.isLoaded(Mods.ComputerCraft)) {
			bundledRedstoneCC = new CCBundledRedstoneIntegration();
		}
	}

}
