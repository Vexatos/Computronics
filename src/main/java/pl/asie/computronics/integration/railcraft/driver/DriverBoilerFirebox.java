package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFirebox;
import net.minecraft.tileentity.TileEntity;
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

		public CCDriver(TileBoilerFirebox tile, World world, int x, int y, int z) {
			super(tile, Names.Railcraft_BoilerFirebox, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileBoilerFirebox) {
				return new CCDriver((TileBoilerFirebox) te, world, x, y, z);
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
