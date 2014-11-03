package pl.asie.computronics.integration.cofh;

import cofh.api.energy.IEnergyHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.util.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverEnergyHandler {

	public static class CCDriver extends CCMultiPeripheral<IEnergyHandler> {

		public CCDriver() {
		}

		public CCDriver(IEnergyHandler tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_PoweredTile, world, x, y, z);
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IEnergyHandler) {
				return new CCDriver((IEnergyHandler) te, world, x, y, z);
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
			switch(method){
				case 0:{
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getEnergyStored(side) };
				}
				case 1:{
					final ForgeDirection side = arguments.length > 0 ? ForgeDirection.getOrientation((Integer) arguments[0]) : ForgeDirection.UNKNOWN;
					return new Object[] { tile.getMaxEnergyStored(side) };
				}
			}
			return null;
		}
	}
}
