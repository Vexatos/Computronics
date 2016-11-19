package pl.asie.computronics.integration.waila;

import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalReceiverBox;
import pl.asie.computronics.integration.railcraft.block.BlockLocomotiveRelay;
import pl.asie.computronics.integration.waila.providers.IComputronicsWailaProvider;
import pl.asie.computronics.integration.waila.providers.WailaColorfulLamp;
import pl.asie.computronics.integration.waila.providers.WailaLocomotiveRelay;
import pl.asie.computronics.integration.waila.providers.WailaPeripheral;
import pl.asie.computronics.integration.waila.providers.WailaTapeDrive;
import pl.asie.computronics.reference.Mods;

import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class WailaProviders {

	public static final ArrayList<WailaProviders> VALUES = new ArrayList<WailaProviders>();
	private IComputronicsWailaProvider provider;
	private Class<?> block;

	static void initialize() {
		newProvider(new WailaPeripheral(), BlockPeripheral.class);
		if(Mods.isLoaded(Mods.Railcraft)) {
			newProvider(new WailaPeripheral(), BlockDigitalReceiverBox.class);
			newProvider(new WailaPeripheral(), BlockDigitalControllerBox.class);
			newProvider(new WailaLocomotiveRelay(), BlockLocomotiveRelay.class);
		}
		newProvider(new WailaTapeDrive(), BlockTapeReader.class);
		newProvider(new WailaColorfulLamp(), BlockColorfulLamp.class);
	}

	private static void newProvider(IComputronicsWailaProvider provider, Class<?> block) {
		new WailaProviders(provider, block);
	}

	private WailaProviders(IComputronicsWailaProvider provider, Class<?> block) {
		this.provider = provider;
		this.block = block;
		VALUES.add(this);
	}

	public IComputronicsWailaProvider getProvider() {
		return this.provider;
	}

	public boolean isInstance(Object obj) {
		return obj != null && block.isInstance(obj);
	}
}
