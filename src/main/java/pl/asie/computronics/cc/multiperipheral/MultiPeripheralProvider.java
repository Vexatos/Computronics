package pl.asie.computronics.cc.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;

import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class MultiPeripheralProvider implements IPeripheralProvider {
	ArrayList<IMultiPeripheralProvider> peripheralProviders = new ArrayList<IMultiPeripheralProvider>();

	public MultiPeripheralProvider(ArrayList<IMultiPeripheralProvider> peripheralProviders) {
		this.peripheralProviders = peripheralProviders;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		ArrayList<IMultiPeripheral> periphs = new ArrayList<IMultiPeripheral>();
		for(IMultiPeripheralProvider peripheralProvider : this.peripheralProviders) {
			IMultiPeripheral p = peripheralProvider.getPeripheral(world, x, y, z, side);
			if(p != null) {
				periphs.add(p);
			}
		}
		if(!periphs.isEmpty()) {
			return new MultiPeripheral(periphs, world, x, y, z);
		}
		return null;
	}
}
