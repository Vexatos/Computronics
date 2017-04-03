package pl.asie.computronics.integration.charset.wires;

/**
 * @author Vexatos
 */
public class IntegrationCharsetWires {

	//public static CCBundledRedstoneIntegration bundledRedstoneCC;
	public static ComputronicsBundledRedstoneIntegration bundledRedstone;

	static {
		bundledRedstone = new ComputronicsBundledRedstoneIntegration();

		/*if(Mods.isLoaded(Mods.ComputerCraft)) { TODO ComputerCraft
			bundledRedstoneCC = new CCBundledRedstoneIntegration();
		}*/
	}

}
