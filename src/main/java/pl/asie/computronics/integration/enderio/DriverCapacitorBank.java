package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverCapacitorBank {

	private static Object[] getRedstoneMode(TileCapBank tile, boolean input) {
		if(input) {
			return new Object[] { tile.getInputControlMode().name().toLowerCase(Locale.ENGLISH) };
		}
		return new Object[] { tile.getOutputControlMode().name().toLowerCase(Locale.ENGLISH) };
	}

	private static Object[] setRedstoneMode(TileCapBank tile, String mode, boolean input) {
		try {
			if(input) {
				tile.setInputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
			}
			tile.setOutputControlMode(RedstoneControlMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid Redstone mode given");
		}
		return new Object[] { };
	}

	private static Object[] modes() {
		LinkedHashMap<Integer, String> modes = new LinkedHashMap<Integer, String>();
		int i = 1;
		for(RedstoneControlMode mode : RedstoneControlMode.values()) {
			modes.put(i, mode.name().toLowerCase(Locale.ENGLISH));
			i++;
		}
		return new Object[] { modes };
	}

	public static class OCDriver extends DriverTileEntity {
		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileCapBank> {
			public InternalManagedEnvironment(TileCapBank tile) {
				super(tile, Names.EnderIO_CapacitorBank);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the average storage change per tick")
			public Object[] getAverageChangePerTick(Context c, Arguments a) {
				if(tile.getNetwork() != null) {
					return new Object[] { tile.getNetwork().getAverageChangePerTick() };
				}
				return new Object[] { 0 };
			}

			@Callback(doc = "function(max:number); Sets the max input of the capacitor bank")
			public Object[] setMaxInput(Context c, Arguments a) {
				tile.setMaxInput(a.checkInteger(0));
				return new Object[] { };
			}

			@Callback(doc = "function(max:number); Sets the max output of the capacitor bank")
			public Object[] setMaxOutput(Context c, Arguments a) {
				tile.setMaxOutput(a.checkInteger(0));
				return new Object[] { };
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

			@Callback(doc = "This is a table of every Redstone control mode available", getter = true)
			public Object[] redstone_modes(Context c, Arguments a) {
				return DriverCapacitorBank.modes();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileCapBank.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment(((TileCapBank) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileCapBank> {

		public CCDriver() {
		}

		public CCDriver(TileCapBank tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_CapacitorBank, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileCapBank) {
				return new CCDriver((TileCapBank) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getAverageChangePerTick", "setMaxInput", "setMaxOutput", "getInputMode", "getOutputMode", "setInputMode", "setOutputMode", "getRedstoneModeTable" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					if(tile.getNetwork() != null) {
						return new Object[] { tile.getNetwork().getAverageChangePerTick() };
					}
					return new Object[] { 0 };
				}
				case 1:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setMaxInput(((Double) arguments[0]).intValue());
					return new Object[] { };
				}
				case 2:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setMaxOutput(((Double) arguments[0]).intValue());
					return new Object[] { };
				}
				case 3:{
					return DriverCapacitorBank.getRedstoneMode(tile, true);
				}
				case 4:{
					return DriverCapacitorBank.getRedstoneMode(tile, false);
				}
				case 5:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverCapacitorBank.setRedstoneMode(tile, (String) arguments[0], true);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 6:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverCapacitorBank.setRedstoneMode(tile, (String) arguments[0], false);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 7:{
					return DriverCapacitorBank.modes();
				}
			}
			return null;
		}
	}
}
