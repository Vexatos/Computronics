package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockPeripheral;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		WailaProviders.initialize();

		WailaComputronics provider = new WailaComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		/*if(Mods.isLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerNBTProvider(provider, BlockDigitalReceiverBox.class);
		}*/

		ConfigValues.registerConfigs(reg);
	}

}
