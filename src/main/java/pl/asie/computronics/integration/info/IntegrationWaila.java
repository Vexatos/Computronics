package pl.asie.computronics.integration.info;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalBoxBase;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalReceiverBox;
import pl.asie.computronics.reference.Mods;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		if(Mods.isLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerNBTProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerBodyProvider(provider, BlockDigitalControllerBox.class);
			reg.registerNBTProvider(provider, BlockDigitalControllerBox.class);
			reg.registerBodyProvider(provider, BlockDigitalBoxBase.class);
			reg.registerNBTProvider(provider, BlockDigitalBoxBase.class);
		}

		ConfigValues.registerConfigs(reg);
	}

}
