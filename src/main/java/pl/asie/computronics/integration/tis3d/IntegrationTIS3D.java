package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.API;

/**
 * @author Vexatos
 */
public class IntegrationTIS3D {

	public void init() {
		API.addProvider(new ModuleProviderColorful());
		API.addProvider(new ModuleProviderTapeReader());
	}
}
