package pl.asie.computronics.integration.waila;

import cpw.mods.fml.common.Loader;
import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockDigitalReceiverBox;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.reference.Mods;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		reg.registerBodyProvider(new WailaComputronics(), BlockPeripheral.class);
		registerKeys(reg, BlockPeripheral.class);

		if(Loader.isModLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(new WailaComputronics(), BlockDigitalReceiverBox.class);
			registerKeys(reg, BlockDigitalReceiverBox.class);
		}

		ConfigValues.registerConfigs(reg);
	}

	private static void registerKeys(IWailaRegistrar reg, Class<?> clazz) {
		reg.registerSyncedNBTKey("*", clazz);
	}
}
