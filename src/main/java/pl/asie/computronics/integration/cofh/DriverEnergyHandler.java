package pl.asie.computronics.integration.cofh;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverEnergyHandler {

	public static class CCDriver extends CCMultiPeripheral<IEnergyHandler> {

		public CCDriver() {
		}

		public CCDriver(IEnergyProvider tile, World world, BlockPos pos) {
			super(tile, Names.CoFH_PoweredTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IEnergyProvider && !(te instanceof IEnergyReceiver)) {
				return new CCDriver((IEnergyProvider) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getEnergyStored", "getMaxEnergyStored" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			if(arguments.length > 0 && !(arguments[0] instanceof Integer)) {
				throw new LuaException("first argument needs to be a number or nil");
			}
			switch(method) {
				case 0: {
					final EnumFacing side = arguments.length > 0 ? EnumFacing.getFront((Integer) arguments[0]) : null;
					return new Object[] { tile.getEnergyStored(side) };
				}
				case 1: {
					final EnumFacing side = arguments.length > 0 ? EnumFacing.getFront((Integer) arguments[0]) : null;
					return new Object[] { tile.getMaxEnergyStored(side) };
				}
			}
			return null;
		}
	}
}
