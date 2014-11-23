package pl.asie.computronics.cc.multiperipheral;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.api.ApiAccess;
import openperipheral.api.IAdapterFactory;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.reference.Mods;

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
		if(Loader.isModLoaded(Mods.OpenPeripheral)) {
			IMultiPeripheral peripheral = getOpenPeripheral(world, x, y, z);
			if(peripheral != null) {
				periphs.add(peripheral);
			}
		}
		if(!periphs.isEmpty()) {
			return new MultiPeripheral(periphs, world, x, y, z);
		}
		return null;
	}

	@Optional.Method(modid = Mods.OpenPeripheral)
	private IMultiPeripheral getOpenPeripheral(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null) {
			IPeripheral peripheral = ApiAccess.getApi(IAdapterFactory.class).createPeripheral(tile);
			if(peripheral != null) {
				return new OpenMultiPeripheral(peripheral);
			}
		}
		return null;
	}
}
