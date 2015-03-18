package pl.asie.computronics.integration.cofh;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverEnergyHandler {

	public static class CCDriver_Receiver extends CCMultiPeripheral<IEnergyReceiver> {
		public CCDriver_Receiver() {
		}

		public CCDriver_Receiver(IEnergyReceiver tile, World world, int x, int y, int z) {
			super(tile, Names.CoFH_PoweredTile, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IEnergyReceiver) {
				return new CCDriver_Receiver((IEnergyReceiver) te, world, x, y, z);
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
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getEnergyStored(side) };
				}
				case 1: {
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getMaxEnergyStored(side) };
				}
			}
			return null;
		}
	}

	public static class CCDriver_Provider extends CCMultiPeripheral<IEnergyProvider> {

		public CCDriver_Provider() {
		}

		public CCDriver_Provider(IEnergyProvider tile, World world, int x, int y, int z) {
			super(tile, Names.CoFH_PoweredTile, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IEnergyProvider && !(te instanceof IEnergyReceiver)) {
				return new CCDriver_Provider((IEnergyProvider) te, world, x, y, z);
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
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getEnergyStored(side) };
				}
				case 1: {
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getMaxEnergyStored(side) };
				}
			}
			return null;
		}
	}
}
