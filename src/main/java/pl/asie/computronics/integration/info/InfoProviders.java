package pl.asie.computronics.integration.info;

import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.block.BlockTapeReader;
import pl.asie.computronics.integration.info.providers.IComputronicsInfoProvider;
import pl.asie.computronics.integration.info.providers.InfoColorfulLamp;
import pl.asie.computronics.integration.info.providers.InfoLocomotiveRelay;
import pl.asie.computronics.integration.info.providers.InfoPeripheral;
import pl.asie.computronics.integration.info.providers.InfoTapeDrive;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalSignalBox;
import pl.asie.computronics.integration.railcraft.block.BlockLocomotiveRelay;
import pl.asie.computronics.reference.Mods;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class InfoProviders {

	public static final ArrayList<InfoProviders> VALUES = new ArrayList<InfoProviders>();
	private IComputronicsInfoProvider provider;
	private Class<?> block;

	private static boolean init = false;

	static void initialize() {
		if(init) {
			return;
		}
		init = true;
		newProvider(new InfoPeripheral(), BlockPeripheral.class);
		if(Mods.isLoaded(Mods.Railcraft)) {
			newProvider(new InfoPeripheral(), BlockDigitalSignalBox.class);
			newProvider(new InfoLocomotiveRelay(), BlockLocomotiveRelay.class);
		}
		newProvider(new InfoTapeDrive(), BlockTapeReader.class);
		newProvider(new InfoColorfulLamp(), BlockColorfulLamp.class);
	}

	private static void newProvider(IComputronicsInfoProvider provider, Class<?> block) {
		new InfoProviders(provider, block);
	}

	private InfoProviders(IComputronicsInfoProvider provider, Class<?> block) {
		this.provider = provider;
		this.block = block;
		VALUES.add(this);
	}

	public IComputronicsInfoProvider getProvider() {
		return this.provider;
	}

	public boolean isInstance(@Nullable Object obj) {
		return obj != null && block.isInstance(obj);
	}
}
