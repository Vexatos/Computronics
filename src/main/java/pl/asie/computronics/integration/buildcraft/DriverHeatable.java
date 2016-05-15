package pl.asie.computronics.integration.buildcraft;

import buildcraft.api.tiles.IHeatable;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverHeatable {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IHeatable> {

			public InternalManagedEnvironment(IHeatable tile) {
				super(tile, Names.BuildCraft_Heatable);
			}

			@Callback(doc = "function():number; Returns minimum heat value")
			public Object[] getMinHeatValue(Context c, Arguments a) {
				return new Object[] { tile.getMinHeatValue() };
			}

			@Callback(doc = "function():number; Returns ideal heat value")
			public Object[] getIdealHeatValue(Context c, Arguments a) {
				return new Object[] { tile.getIdealHeatValue() };
			}

			@Callback(doc = "function():number; Returns maximum heat value")
			public Object[] getMaxHeatValue(Context c, Arguments a) {
				return new Object[] { tile.getMaxHeatValue() };
			}

			@Callback(doc = "function():number; Returns current heat value")
			public Object[] getCurrentHeatValue(Context c, Arguments a) {
				return new Object[] { tile.getCurrentHeatValue() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IHeatable.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((IHeatable) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IHeatable> {

		public CCDriver() {
		}

		public CCDriver(IHeatable tile, World world, int x, int y, int z) {
			super(tile, Names.BuildCraft_Heatable, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IHeatable) {
				return new CCDriver((IHeatable) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getMinHeatValue", "getIdealHeatValue", "getMaxHeatValue", "getCurrentHeatValue" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getMinHeatValue() };
				}
				case 1: {
					return new Object[] { tile.getIdealHeatValue() };
				}
				case 2: {
					return new Object[] { tile.getMaxHeatValue() };
				}
				case 3: {
					return new Object[] { tile.getCurrentHeatValue() };
				}
			}
			return null;
		}
	}
}
