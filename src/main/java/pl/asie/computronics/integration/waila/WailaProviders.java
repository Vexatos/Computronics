package pl.asie.computronics.integration.waila;

import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.integration.waila.providers.IComputronicsWailaProvider;
import pl.asie.computronics.integration.waila.providers.WailaTapeDrive;

/**
 * @author Vexatos
 */
public enum WailaProviders {
	//Base(new WailaPeripheral(), BlockPeripheral.class),
	//DigitalBox(new WailaPeripheral(), BlockDigitalReceiverBox.class),
	TapeDrive(new WailaTapeDrive(), BlockTapeReader.class);
	//LocoRelay(new WailaLocomotiveRelay(), BlockLocomotiveRelay.class);

	public static final WailaProviders[] VALUES = values();
	private IComputronicsWailaProvider provider;
	private Class<?> block;

	private WailaProviders(IComputronicsWailaProvider provider, Class<?> block) {
		this.provider = provider;
		this.block = block;
	}

	public IComputronicsWailaProvider getProvider() {
		return this.provider;
	}

	public boolean isInstance(Object obj) {
		return obj != null && block.isInstance(obj);
	}
}
