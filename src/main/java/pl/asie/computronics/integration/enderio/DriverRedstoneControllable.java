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
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
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

	private static LinkedHashMap<Object, Object> modes;

	private static Object[] modes() {
		if(modes == null) {
			modes = new LinkedHashMap<Object, Object>();
			int i = 1;
			for(RedstoneControlMode mode : RedstoneControlMode.values()) {
				final String name = mode.name().toLowerCase(Locale.ENGLISH);
				modes.put(name, i);
				modes.put(i++, name);
			}
		}
		return new Object[] { modes };
	}

	public static class OCDriver extends DriverTileEntity {

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

			@Callback(doc = "This is a bidirectional table of every Redstone control mode available", getter = true)
			public Object[] redstone_modes(Context c, Arguments a) {
				return DriverRedstoneControllable.modes();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IRedstoneModeControlable.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
			return new InternalManagedEnvironment(((IRedstoneModeControlable) world.getTileEntity(pos)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IRedstoneModeControlable> {

		public CCDriver() {
		}

		public CCDriver(IRedstoneModeControlable tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_RedstoneTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IRedstoneModeControlable) {
				return new CCDriver((IRedstoneModeControlable) te, world, pos);
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
