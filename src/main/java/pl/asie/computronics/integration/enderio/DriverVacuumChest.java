package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machines.machine.vacuum.chest.TileVacuumChest;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverVacuumChest {

	public static class OCDriver extends DriverSpecificTileEntity<TileVacuumChest> {

		public class InternalManagedEnvironment extends NamedManagedEnvironment<TileVacuumChest> {

			public InternalManagedEnvironment(TileVacuumChest tile) {
				super(tile, Names.EnderIO_VacuumChest);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the current range of the vacuum chest", direct = true)
			public Object[] getRange(Context c, Arguments a) {
				return new Object[] { tile.getRange() };
			}

			@Callback(doc = "function(range:number); Sets the range of the vacuum chest")
			public Object[] setRange(Context c, Arguments a) {
				tile.setRange(a.checkInteger(0));
				return new Object[] {};
			}
		}

		public OCDriver() {
			super(TileVacuumChest.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileVacuumChest tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileVacuumChest> {

		public CCDriver() {
		}

		public CCDriver(TileVacuumChest tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_VacuumChest, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileVacuumChest) {
				return new CCDriver((TileVacuumChest) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getRange", "setRange" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getRange() };
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setRange(((Double) arguments[0]).intValue());
					return new Object[] {};
				}
			}
			return new Object[] {};
		}
	}
}
