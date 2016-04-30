package pl.asie.computronics.integration;

import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.ICombinedMultiPeripheral;

/**
 * @author Vexatos
 */
public abstract class CCMultiPeripheral<T> extends CCTilePeripheral<T>
	implements ICombinedMultiPeripheral {

	protected CCMultiPeripheral() {

	}

	protected CCMultiPeripheral(T tile, String name, World world, int x, int y, int z) {
		super(tile, name, world, x, y, z);
	}

	@Override
	public int peripheralPriority() {
		return 0;
	}
}
