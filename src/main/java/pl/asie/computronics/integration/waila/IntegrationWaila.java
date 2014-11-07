package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import pl.asie.computronics.block.BlockTapeReader;

public class IntegrationWaila {
	public static void register(IWailaRegistrar reg) {
		//reg.registerBodyProvider(new WailaComputronics(), BlockPeripheral.class);
		//registerKeys(reg, BlockPeripheral.class, "oc:node");
		reg.registerBodyProvider(new WailaComputronics(), BlockTapeReader.class);
		/*if(Loader.isModLoaded(Mods.Railcraft)) {
			registerKeys(reg, TileLocomotiveRelay.class, "bound");
			reg.registerBodyProvider(new WailaComputronics(), BlockDigitalReceiverBox.class);
			registerKeys(reg, TileDigitalReceiverBox.class, "oc:node");
		}*/

		ConfigValues.registerConfigs(reg);
	}

	private static void registerKeys(IWailaRegistrar reg, Class<?> clazz, String... names) {
		for(String name : names) {
			reg.registerSyncedNBTKey(name, clazz);
		}
		reg.registerSyncedNBTKey("x", clazz);
		reg.registerSyncedNBTKey("y", clazz);
		reg.registerSyncedNBTKey("z", clazz);
	}
}
