package pl.asie.computronics.cc;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;

public class CCPeripheralProvider implements IMultiPeripheralProvider {

	@Override
	public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity t = world.getTileEntity(pos);
		if(t != null && t instanceof IComputronicsPeripheral && t instanceof IMultiPeripheral) {
			if(t instanceof ISidedPeripheral) {
				return ((ISidedPeripheral) t).canConnectPeripheralOnSide(side) ? ((IMultiPeripheral) t) : null;
			}
			return ((IMultiPeripheral) t);
		} else {
			return null;
		}
	}
}
