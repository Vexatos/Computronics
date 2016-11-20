package pl.asie.computronics.integration.info;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockPeripheral;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		/*if(Mods.isLoaded(Mods.Railcraft)) { TODO Railcraft
			reg.registerBodyProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerNBTProvider(provider, BlockDigitalReceiverBox.class);
		}*/

		ConfigValues.registerConfigs(reg);
	}

}
