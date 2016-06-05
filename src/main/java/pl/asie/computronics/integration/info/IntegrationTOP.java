package pl.asie.computronics.integration.info;

import mcjty.theoneprobe.api.ITheOneProbe;

/**
 * @author Vexatos
 */
public class IntegrationTOP {

	public static void register(ITheOneProbe probe) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		probe.registerProvider(provider);
	}
}
