package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
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
public class DriverIOConfigurable {

	private static Object[] getIOMode(IIoConfigurable tile, int side) {
		return new Object[] { tile.getIoMode(EnumFacing.getFront(side - 1)).name().toLowerCase(Locale.ENGLISH) };
	}

	private static Object[] setIOMode(IIoConfigurable tile, int side, String mode) {
		try {
			tile.setIoMode(EnumFacing.getFront(side - 1), IoMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid IO mode given");
		}
		return new Object[] {};
	}

	private static Object[] modes() {
		LinkedHashMap<Integer, String> modes = new LinkedHashMap<Integer, String>();
		int i = 1;
		for(IoMode mode : IoMode.values()) {
			modes.put(i++, mode.name().toLowerCase(Locale.ENGLISH));
		}
		return new Object[] { modes };
	}

	public static class OCDriver extends DriverTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IIoConfigurable> {

			public InternalManagedEnvironment(IIoConfigurable tile) {
				super(tile, Names.EnderIO_IOConfigurable);
			}

			@Override
			public int priority() {
				return 2;
			}

			@Callback(doc = "function(side:number):string; Returns the current IO mode on the given side")
			public Object[] getIOMode(Context c, Arguments a) {
				int side = a.checkInteger(0);
				if(side <= 0 || side > IoMode.values().length) {
					throw new IllegalArgumentException("side needs to be between 1 and 6");
				}
				return DriverIOConfigurable.getIOMode(tile, side);
			}

			@Callback(doc = "function(side:number,mode:string); Sets the IO mode on the given side")
			public Object[] setIOMode(Context c, Arguments a) {
				return DriverIOConfigurable.setIOMode(tile, a.checkInteger(0), a.checkString(1));
			}

			@Callback(doc = "This is a bidirectional table of every IO mode available", getter = true)
			public Object[] io_modes(Context c, Arguments a) {
				return DriverIOConfigurable.modes();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IIoConfigurable.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
			return new InternalManagedEnvironment(((IIoConfigurable) world.getTileEntity(pos)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IIoConfigurable> {

		public CCDriver() {
		}

		public CCDriver(IIoConfigurable tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_IOConfigurable, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 2;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IIoConfigurable) {
				return new CCDriver((IIoConfigurable) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getIOMode", "setIOMode", "getIOModeTable" };
		}

		private int checkSide(Object[] arguments) throws LuaException {
			if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
				throw new LuaException("first argument needs to be a number");
			}
			int side = ((Double) arguments[0]).intValue() - 1;
			if(side <= 0 || side > IoMode.values().length) {
				throw new LuaException("side needs to be between 1 and 6");
			}
			return side;
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					int side = this.checkSide(arguments);
					return DriverIOConfigurable.getIOMode(tile, side);
				}
				case 1: {
					int side = this.checkSide(arguments);
					if(arguments.length < 2 || !(arguments[1] instanceof String)) {
						throw new LuaException("second argument needs to be a string");
					}
					try {
						return DriverIOConfigurable.setIOMode(tile, side, (String) arguments[1]);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 2: {
					return DriverIOConfigurable.modes();
				}
			}
			return null;
		}
	}
}
