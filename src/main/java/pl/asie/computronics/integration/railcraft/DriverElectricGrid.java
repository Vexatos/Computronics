package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.api.electricity.GridTools;
import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverElectricGrid {

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IElectricGrid> {

			public InternalManagedEnvironment(IElectricGrid tile) {
				super(tile, Names.Railcraft_ElectricGrid);
			}

			@Callback(doc = "function():number; Returns the current charge of the electric tile")
			public Object[] getCharge(Context c, Arguments a) {
				return new Object[] { tile.getChargeHandler().getCharge() };
			}

			@Callback(doc = "function():number; Returns the maximum capacity of the electric tile")
			public Object[] getCapacity(Context c, Arguments a) {
				return new Object[] { tile.getChargeHandler().getCapacity() };
			}

			@Callback(doc = "function():number; Returns the loss per tick of the electric tile.")
			public Object[] getLoss(Context c, Arguments a) {
				return new Object[] { tile.getChargeHandler().getLosses() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IElectricGrid.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z) {
			return GridTools.getGridObjectAt(world, x, y, z) != null;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment(GridTools.getGridObjectAt(world, x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IElectricGrid> {

		public CCDriver() {
			super();
		}

		public CCDriver(IElectricGrid tile, World world, int x, int y, int z) {
			super(tile, Names.Railcraft_ElectricGrid, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			if(GridTools.getGridObjectAt(world, x, y, z) != null) {
				return new CCDriver(GridTools.getGridObjectAt(world, x, y, z), world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getCharge", "getCapacity", "getLoss" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					return new Object[] { tile.getChargeHandler().getCharge() };
				}
				case 1:{
					return new Object[] { tile.getChargeHandler().getCapacity() };
				}
				case 2:{
					return new Object[] { tile.getChargeHandler().getLosses() };
				}
			}
			return null;
		}
	}
}
