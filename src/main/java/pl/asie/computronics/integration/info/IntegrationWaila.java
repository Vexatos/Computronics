package pl.asie.computronics.integration.info;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockPeripheral;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		/*if(Mods.isLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerNBTProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerBodyProvider(provider, BlockDigitalControllerBox.class);
			reg.registerNBTProvider(provider, BlockDigitalControllerBox.class);
		}*/

		ConfigValues.registerConfigs(reg);
	}

}
