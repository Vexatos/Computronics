package pl.asie.computronics.integration.enderio;

import crazypants.enderio.conduits.conduit.power.NetworkPowerManager;
import crazypants.enderio.powertools.machine.monitor.TilePowerMonitor;
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
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverPowerMonitor {

	private static NetworkPowerManager checkPowerManager(TilePowerMonitor tile) {
		NetworkPowerManager man = tile.getPowerManager();
		if(man == null) {
			throw new IllegalStateException("no conduit network detected");
		}
		return man;
	}

	public static class OCDriver extends DriverSpecificTileEntity<TilePowerMonitor> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TilePowerMonitor> {

			public InternalManagedEnvironment(TilePowerMonitor tile) {
				super(tile, Names.EnderIO_PowerMonitor);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the energy currently in the conduit network")
			public Object[] getPowerInConduits(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getPowerInConduits() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in the conduit network")
			public Object[] getMaxPowerInConduits(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getMaxPowerInConduits() };
			}

			@Callback(doc = "function():number; Returns the energy currently in connected Capacitor Banks")
			public Object[] getPowerInCapacitorBanks(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getPowerInCapacitorBanks() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in connected Capacitor Banks")
			public Object[] getMaxPowerInCapacitorBanks(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getMaxPowerInCapacitorBanks() };
			}

			@Callback(doc = "function():number; Returns the energy currently in connected Machines")
			public Object[] getPowerInReceptors(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getPowerInReceptors() };
			}

			@Callback(doc = "function():number; Returns the max energy that can be in connected Machines")
			public Object[] getMaxPowerInReceptors(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getMaxPowerInReceptors() };
			}

			@Callback(doc = "function():number; Returns the average energy sent")
			public Object[] getAverageEnergySent(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getNetworkPowerTracker().getAverageRfTickSent() };
			}

			@Callback(doc = "function():number; Returns the average energy received")
			public Object[] getAverageEnergyReceived(Context c, Arguments a) {
				return new Object[] { checkPowerManager(tile).getNetworkPowerTracker().getAverageRfTickRecieved() };
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

		public OCDriver() {
			super(TilePowerMonitor.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TilePowerMonitor tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TilePowerMonitor> {

		public CCDriver() {
		}

		public CCDriver(TilePowerMonitor tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_PowerStorage, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TilePowerMonitor) {
				return new CCDriver((TilePowerMonitor) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getPowerInConduits", "getMaxPowerInConduits", "getPowerInCapacitorBanks", "getMaxPowerInCapacitorBanks",
				"getPowerInReceptors", "getMaxPowerInReceptors", "getAverageEnergySent", "getAverageEnergyReceived",
				"isEngineControlEnabled", "setEngineControlEnabled", "getStartLevel", "setStartLevel", "getStopLevel", "setStopLevel" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { checkPowerManager(tile).getPowerInConduits() };
				}
				case 1: {
					return new Object[] { checkPowerManager(tile).getMaxPowerInConduits() };
				}
				case 2: {
					return new Object[] { checkPowerManager(tile).getPowerInCapacitorBanks() };
				}
				case 3: {
					return new Object[] { checkPowerManager(tile).getMaxPowerInCapacitorBanks() };
				}
				case 4: {
					return new Object[] { checkPowerManager(tile).getPowerInReceptors() };
				}
				case 5: {
					return new Object[] { checkPowerManager(tile).getMaxPowerInReceptors() };
				}
				case 6: {
					return new Object[] { checkPowerManager(tile).getNetworkPowerTracker().getAverageRfTickSent() };
				}
				case 7: {
					return new Object[] { checkPowerManager(tile).getNetworkPowerTracker().getAverageRfTickRecieved() };
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
			return new Object[] {};
		}
	}
}
