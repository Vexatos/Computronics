package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.common.blocks.multi.TileBoilerFirebox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverBoilerFirebox {

	public static class CCDriver extends CCMultiPeripheral<TileBoilerFirebox> {

		public CCDriver() {
			super();
		}

		public CCDriver(TileBoilerFirebox tile, World world, BlockPos pos) {
			super(tile, Names.Railcraft_BoilerFirebox, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileBoilerFirebox) {
				return new CCDriver((TileBoilerFirebox) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isBurning", "getTemperature", "getMaxHeat" };
		}

		//Yes, this is mostly stolen from Sangar's Steam Turbine Driver.
		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.isBurning() };
				}
				case 1: {
					return new Object[] { tile.getTemperature() };
				}
				case 2: {
					return new Object[] { tile.getMaxHeat() };
				}
			}
			return null;
		}
	}
}
