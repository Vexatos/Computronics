package pl.asie.computronics.integration.waila;

import cpw.mods.fml.common.Loader;
import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockDigitalReceiverBox;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.reference.Mods;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		WailaProviders.initialize();

		WailaComputronics provider = new WailaComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		if(Loader.isModLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalReceiverBox.class);
			reg.registerNBTProvider(provider, BlockDigitalReceiverBox.class);
		}

		ConfigValues.registerConfigs(reg);
	}

}
