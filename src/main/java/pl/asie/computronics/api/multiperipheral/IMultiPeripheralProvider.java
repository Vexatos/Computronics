package pl.asie.computronics.api.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IMultiPeripheralProvider extends IPeripheralProvider {

	@Override
	public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side);

}
