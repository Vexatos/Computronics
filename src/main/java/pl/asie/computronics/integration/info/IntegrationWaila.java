package pl.asie.computronics.integration.info;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalSignalBox;
import pl.asie.computronics.reference.Mods;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		if(Mods.isLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalSignalBox.class);
			reg.registerNBTProvider(provider, BlockDigitalSignalBox.class);
		}

		ConfigValues.registerConfigs(reg);
	}

}
