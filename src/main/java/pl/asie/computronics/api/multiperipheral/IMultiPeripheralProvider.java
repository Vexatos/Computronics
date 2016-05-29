package pl.asie.computronics.api.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public interface IMultiPeripheralProvider extends IPeripheralProvider {

	@Nullable
	@Override
	IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side);

}
