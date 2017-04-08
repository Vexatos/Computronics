package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.vacuum.TileVacuumChest;
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
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverVacuumChest {
	public static class OCDriver extends DriverSidedTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileVacuumChest> {
			public InternalManagedEnvironment(TileVacuumChest tile) {
				super(tile, Names.EnderIO_VacuumChest);
			}

			@Override
			public int priority() {
				return 3;
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

		@Override
		public Class<?> getTileEntityClass() {
			return TileVacuumChest.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((TileVacuumChest) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileVacuumChest> {

		public CCDriver() {
		}

		public CCDriver(TileVacuumChest tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_VacuumChest, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileVacuumChest) {
				return new CCDriver((TileVacuumChest) te, world, x, y, z);
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
			return null;
		}
	}
}
