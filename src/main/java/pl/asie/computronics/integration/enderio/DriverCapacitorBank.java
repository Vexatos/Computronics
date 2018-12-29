package pl.asie.computronics.integration.enderio;

import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
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

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverCapacitorBank {

	private static Object[] getAverageInputPerTick(TileCapBank tile) {
		if(tile.getNetwork() != null) {
			return new Object[] { tile.getNetwork().getAverageInputPerTick() };
		}
		return new Object[] { 0 };
	}

	private static Object[] getAverageOutputPerTick(TileCapBank tile) {
		if(tile.getNetwork() != null) {
			return new Object[] { tile.getNetwork().getAverageOutputPerTick() };
		}
		return new Object[] { 0 };
	}

	private static Object[] getAverageChangePerTick(TileCapBank tile) {
		if(tile.getNetwork() != null) {
			return new Object[] { tile.getNetwork().getAverageChangePerTick() };
		}
		return new Object[] { 0 };
	}

	private static Object[] setMaxInput(TileCapBank tile, int input) {
		if(tile.getNetwork() != null) {
			tile.getNetwork().setMaxInput(input);
		} else {
			tile.setMaxInput(input);
		}
		return new Object[] {};
	}

	private static Object[] setMaxOutput(TileCapBank tile, int output) {
		if(tile.getNetwork() != null) {
			tile.getNetwork().setMaxOutput(output);
		} else {
			tile.setMaxOutput(output);
		}
		return new Object[] {};
	}

	private static Object[] getRedstoneMode(TileCapBank tile, boolean input) {
		if(input) {
			return new Object[] { tile.getNetwork() != null ?
				tile.getNetwork().getInputControlMode().name().toLowerCase(Locale.ENGLISH) :
				tile.getInputControlMode().name().toLowerCase(Locale.ENGLISH) };
		}
		return new Object[] { tile.getNetwork() != null ?
			tile.getNetwork().getOutputControlMode().name().toLowerCase(Locale.ENGLISH) :
			tile.getOutputControlMode().name().toLowerCase(Locale.ENGLISH) };
	}

	private static Object[] setRedstoneMode(TileCapBank tile, String mode, boolean input) {
		try {
			if(input) {
				if(tile.getNetwork() != null) {
					tile.getNetwork().setInputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
				} else {
					tile.setInputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
				}
			} else {
				if(tile.getNetwork() != null) {
					tile.getNetwork().setOutputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
				} else {
					tile.setOutputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
				}
			}
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

	public static class OCDriver extends DriverSpecificTileEntity<TileCapBank> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TileCapBank> {

			public InternalManagedEnvironment(TileCapBank tile) {
				super(tile, Names.EnderIO_CapacitorBank);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the average storage input per tick")
			public Object[] getAverageInputPerTick(Context c, Arguments a) {
				return DriverCapacitorBank.getAverageInputPerTick(tile);
			}

			@Callback(doc = "function():number; Returns the average storage output per tick")
			public Object[] getAverageOutputPerTick(Context c, Arguments a) {
				return DriverCapacitorBank.getAverageOutputPerTick(tile);
			}

			@Callback(doc = "function():number; Returns the average storage change per tick")
			public Object[] getAverageChangePerTick(Context c, Arguments a) {
				return DriverCapacitorBank.getAverageChangePerTick(tile);
			}

			@Callback(doc = "function(max:number); Sets the max input of the capacitor bank")
			public Object[] setMaxInput(Context c, Arguments a) {
				return DriverCapacitorBank.setMaxInput(tile, a.checkInteger(0));
			}

			@Callback(doc = "function(max:number); Sets the max output of the capacitor bank")
			public Object[] setMaxOutput(Context c, Arguments a) {
				return DriverCapacitorBank.setMaxOutput(tile, a.checkInteger(0));
			}

			@Callback(doc = "function():string; Returns the current Redstone control mode for input")
			public Object[] getInputMode(Context c, Arguments a) {
				return DriverCapacitorBank.getRedstoneMode(tile, true);
			}

			@Callback(doc = "function():string; Returns the current Redstone control mode for output")
			public Object[] getOutputMode(Context c, Arguments a) {
				return DriverCapacitorBank.getRedstoneMode(tile, false);
			}

			@Callback(doc = "function(mode:string); Sets the Redstone control mode for input")
			public Object[] setInputMode(Context c, Arguments a) {
				return DriverCapacitorBank.setRedstoneMode(tile, a.checkString(0), true);
			}

			@Callback(doc = "function(mode:string); Sets the Redstone control mode for output")
			public Object[] setOutputMode(Context c, Arguments a) {
				return DriverCapacitorBank.setRedstoneMode(tile, a.checkString(0), false);
			}

			@Callback(doc = "This is a bidirectional table of every Redstone control mode available", getter = true)
			public Object[] redstone_modes(Context c, Arguments a) {
				return DriverCapacitorBank.modes();
			}
		}

		public OCDriver() {
			super(TileCapBank.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileCapBank tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileCapBank> {

		public CCDriver() {
		}

		public CCDriver(TileCapBank tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_CapacitorBank, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileCapBank) {
				return new CCDriver((TileCapBank) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getAverageInputPerTick", "getAverageOutputPerTick", "getAverageChangePerTick", "setMaxInput", "setMaxOutput", "getInputMode", "getOutputMode", "setInputMode",
				"setOutputMode", "getRedstoneModeTable" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverCapacitorBank.getAverageInputPerTick(tile);
				}
				case 1: {
					return DriverCapacitorBank.getAverageOutputPerTick(tile);
				}
				case 2: {
					return DriverCapacitorBank.getAverageChangePerTick(tile);
				}
				case 3: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverCapacitorBank.setMaxInput(tile, ((Double) arguments[0]).intValue());
				}
				case 4: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverCapacitorBank.setMaxOutput(tile, ((Double) arguments[0]).intValue());
				}
				case 5: {
					return DriverCapacitorBank.getRedstoneMode(tile, true);
				}
				case 6: {
					return DriverCapacitorBank.getRedstoneMode(tile, false);
				}
				case 7: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverCapacitorBank.setRedstoneMode(tile, (String) arguments[0], true);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 8: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverCapacitorBank.setRedstoneMode(tile, (String) arguments[0], false);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 9: {
					return DriverCapacitorBank.modes();
				}
			}
			return new Object[] {};
		}
	}
}
