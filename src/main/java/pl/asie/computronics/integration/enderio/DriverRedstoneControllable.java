package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
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

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverRedstoneControllable {

	private static Object[] getRedstoneMode(IRedstoneModeControlable tile) {
		return new Object[] { tile.getRedstoneControlMode().name().toLowerCase(Locale.ENGLISH) };
	}

	private static Object[] setRedstoneMode(IRedstoneModeControlable tile, String mode) {
		try {
			tile.setRedstoneControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid Redstone mode given");
		}
		return new Object[] {};
	}

	private static Object[] modes() {
		LinkedHashMap<Integer, String> modes = new LinkedHashMap<Integer, String>();
		int i = 1;
		for(RedstoneControlMode mode : RedstoneControlMode.values()) {
			modes.put(i++, mode.name().toLowerCase(Locale.ENGLISH));
		}
		return new Object[] { modes };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IRedstoneModeControlable> {

			public InternalManagedEnvironment(IRedstoneModeControlable tile) {
				super(tile, Names.EnderIO_RedstoneTile);
			}

			@Override
			public int priority() {
				return 1;
			}

			@Callback(doc = "function():string; Returns the current Redstone control mode")
			public Object[] getRedstoneMode(Context c, Arguments a) {
				return DriverRedstoneControllable.getRedstoneMode(tile);
			}

			@Callback(doc = "function(mode:string); Sets the Redstone control mode")
			public Object[] setRedstoneMode(Context c, Arguments a) {
				return DriverRedstoneControllable.setRedstoneMode(tile, a.checkString(0));
			}

			@Callback(doc = "This is a table of every Redstone control mode available", getter = true)
			public Object[] redstone_modes(Context c, Arguments a) {
				return DriverRedstoneControllable.modes();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IRedstoneModeControlable.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((IRedstoneModeControlable) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IRedstoneModeControlable> {

		public CCDriver() {
		}

		public CCDriver(IRedstoneModeControlable tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_RedstoneTile, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IRedstoneModeControlable) {
				return new CCDriver((IRedstoneModeControlable) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getRedstoneMode", "setRedstoneMode", "getRedstoneModeTable" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverRedstoneControllable.getRedstoneMode(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverRedstoneControllable.setRedstoneMode(tile, (String) arguments[0]);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 2: {
					return DriverRedstoneControllable.modes();
				}
			}
			return null;
		}
	}
}
