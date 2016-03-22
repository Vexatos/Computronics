package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.monitor.TilePowerMonitor;
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
public class DriverPowerMonitor {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TilePowerMonitor> {

			public InternalManagedEnvironment(TilePowerMonitor tile) {
				super(tile, Names.EnderIO_PowerMonitor);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the energy currently in the conduit network")
			public Object[] getPowerInConduits(Context c, Arguments a) {
				return new Object[] { tile.getPowerInConduits() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in the conduit network")
			public Object[] getMaxPowerInConduits(Context c, Arguments a) {
				return new Object[] { tile.getMaxPowerInConduits() };
			}

			@Callback(doc = "function():number; Returns the energy currently in connected Capacitor Banks")
			public Object[] getPowerInCapBanks(Context c, Arguments a) {
				return new Object[] { tile.getPowerInCapBanks() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in connected Capacitor Banks")
			public Object[] getMaxPowerInCapBanks(Context c, Arguments a) {
				return new Object[] { tile.getMaxPowerInCapBanks() };
			}

			@Callback(doc = "function():number; Returns the energy currently in connected Machines")
			public Object[] getPowerInMachines(Context c, Arguments a) {
				return new Object[] { tile.getPowerInMachines() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in connected Machines")
			public Object[] getMaxPowerInMachines(Context c, Arguments a) {
				return new Object[] { tile.getMaxPowerInMachines() };
			}

			@Callback(doc = "function():number; Returns the average energy sent")
			public Object[] getAverageEnergySent(Context c, Arguments a) {
				return new Object[] { tile.getAveRfSent() };
			}

			@Callback(doc = "function():number; Returns the average energy received")
			public Object[] getAverageEnergyReceived(Context c, Arguments a) {
				return new Object[] { tile.getAveRfReceived() };
			}

			@Callback(doc = "function():boolean; Returns whether Engine Control is enabled")
			public Object[] isEngineControlEnabled(Context c, Arguments a) {
				return new Object[] { tile.isEngineControlEnabled() };
			}

			@Callback(doc = "function(control:boolean); Sets whether Engine Control is enabled")
			public Object[] setEngineControlEnabled(Context c, Arguments a) {
				tile.setEngineControlEnabled(a.checkBoolean(0));
				return new Object[] {};
			}

			@Callback(doc = "function():number; Returns the level at which the monitor should start emitting redstone")
			public Object[] getStartLevel(Context c, Arguments a) {
				return new Object[] { tile.getStartLevel() };
			}

			@Callback(doc = "function(level:number); Sets the level at which the monitor should start emitting redstone")
			public Object[] setStartLevel(Context c, Arguments a) {
				tile.setStartLevel((float) a.checkDouble(0));
				return new Object[] {};
			}

			@Callback(doc = "function():number; Returns the level at which the monitor should stop emitting redstone")
			public Object[] getStopLevel(Context c, Arguments a) {
				return new Object[] { tile.getStopLevel() };
			}

			@Callback(doc = "function(level:number); Sets the level at which the monitor should stop emitting redstone")
			public Object[] setStopLevel(Context c, Arguments a) {
				tile.setStopLevel((float) a.checkDouble(0));
				return new Object[] {};
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TilePowerMonitor.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((TilePowerMonitor) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TilePowerMonitor> {

		public CCDriver() {
		}

		public CCDriver(TilePowerMonitor tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_PowerStorage, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TilePowerMonitor) {
				return new CCDriver((TilePowerMonitor) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getPowerInConduits", "getMaxPowerInConduits", "getPowerInCapBanks", "getMaxPowerInCapBanks",
				"getPowerInMachines", "getMaxPowerInMachines", "getAverageEnergySent", "getAverageEnergyReceived",
				"isEngineControlEnabled", "setEngineControlEnabled", "getStartLevel", "setStartLevel", "getStopLevel", "setStopLevel" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getPowerInConduits() };
				}
				case 1: {
					return new Object[] { tile.getMaxPowerInConduits() };
				}
				case 2: {
					return new Object[] { tile.getPowerInCapBanks() };
				}
				case 3: {
					return new Object[] { tile.getMaxPowerInCapBanks() };
				}
				case 4: {
					return new Object[] { tile.getPowerInMachines() };
				}
				case 5: {
					return new Object[] { tile.getMaxPowerInMachines() };
				}
				case 6: {
					return new Object[] { tile.getAveRfSent() };
				}
				case 7: {
					return new Object[] { tile.getAveRfReceived() };
				}
				case 8: {
					return new Object[] { tile.isEngineControlEnabled() };
				}
				case 9: {
					if(arguments.length < 1 || !(arguments[0] instanceof Boolean)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setEngineControlEnabled((Boolean) arguments[0]);
					return new Object[] {};
				}
				case 10: {
					return new Object[] { tile.getStartLevel() };
				}
				case 11: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setStartLevel(((Number) arguments[0]).floatValue());
					return new Object[] {};
				}
				case 12: {
					return new Object[] { tile.getStopLevel() };
				}
				case 13: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setStopLevel(((Number) arguments[0]).floatValue());
					return new Object[] {};
				}
			}
			return null;
		}
	}
}
