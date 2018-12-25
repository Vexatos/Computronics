package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBattery;
import mods.railcraft.common.util.charge.BatteryBlock;
import mods.railcraft.common.util.charge.ChargeManager;
import mods.railcraft.common.util.charge.ChargeNetwork;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class DriverElectricGrid {

	@Nullable
	private static Charge.IAccess getAccess(int dimension, BlockPos pos) {
		WorldServer world = DimensionManager.getWorld(dimension);
		return world == null ? null : ChargeManager.DISTRIBUTION.network(world).access(pos);
	}

	@Nullable
	private static ChargeNetwork.ChargeNode getNode(int dimension, BlockPos pos) {
		WorldServer world = DimensionManager.getWorld(dimension);
		if(world != null) {
			Charge.IAccess access = ChargeManager.DISTRIBUTION.network(world).access(pos);
			if(access instanceof ChargeNetwork.ChargeNode) {
				return (ChargeNetwork.ChargeNode) access;
			}
		}
		return null;
	}

	public static class OCDriver implements DriverBlock {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<BlockPos> {

			private final int dimension;

			public InternalManagedEnvironment(int dimension, BlockPos pos) {
				super(pos, Names.Railcraft_ChargeBlock);
				this.dimension = dimension;
			}

			@Override
			public int priority() {
				return -1;
			}

			@Callback(doc = "function():number; Returns the current charge of the charge conductor.")
			public Object[] getCharge(Context c, Arguments a) {
				Charge.IAccess node = getAccess(dimension, tile);
				if(node != null) {
					return new Object[] { node.getBattery().map(IBattery::getCharge).orElse(0D) };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the maximum capacity of the charge conductor.")
			public Object[] getCapacity(Context c, Arguments a) {
				Charge.IAccess node = getAccess(dimension, tile);
				if(node != null) {
					return new Object[] { node.getBattery().map(IBattery::getCapacity).orElse(0D)};
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the loss per tick of the charge conductor.")
			public Object[] getLoss(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getChargeSpec().getLosses() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the maximum draw per tick of the charge conductor.")
			public Object[] getMaxDraw(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getBattery().map(BatteryBlock::getMaxDraw).orElse(0D) };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the efficiency of the charge conductor.")
			public Object[] getEfficiency(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getBattery().map(BatteryBlock::getEfficiency).orElse(0D) };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the current charge of the charge network.")
			public Object[] getNetworkCharge(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getCharge() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the maximum capacity of the charge network.")
			public Object[] getNetworkCapacity(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getCapacity() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the loss per tick of the charge network.")
			public Object[] getNetworkLoss(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getLosses() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the draw per tick of the charge network.")
			public Object[] getNetworkDraw(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getAverageUsagePerTick() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the maximum draw per tick of the charge network.")
			public Object[] getMaxNetworkDraw(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getPotentialDraw() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the efficiency of the charge network.")
			public Object[] getNetworkEfficiency(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().getEfficiency() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the size of the charge network.")
			public Object[] getNetworkSize(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getGrid().size() };
				}
				return new Object[] { null, "no node found" };
			}
		}

		@Override
		public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
			Charge.IAccess access = ChargeManager.DISTRIBUTION.network(world).access(pos);
			return access instanceof ChargeNetwork.ChargeNode && ((ChargeNetwork.ChargeNode) access).isValid();
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
			return new InternalManagedEnvironment(world.provider.getDimension(), pos);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<BlockPos> {

		private final int dimension;

		public CCDriver() {
			super();
			this.dimension = 0;
		}

		public CCDriver(World world, BlockPos pos) {
			super(pos, Names.Railcraft_ChargeBlock, world, pos);
			this.dimension = world.provider.getDimension();
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			Charge.IAccess access = ChargeManager.DISTRIBUTION.network(world).access(pos);
			if(access instanceof ChargeNetwork.ChargeNode && ((ChargeNetwork.ChargeNode) access).isValid()) {
				return new CCDriver(world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getCharge", "getCapacity", "getLoss", "getMaxDraw", "getEfficiency",
				"getNetworkCharge", "getNetworkCapacity", "getNetworkLoss", "getNetworkDraw", "getMaxNetworkDraw", "getNetworkEfficiency", "getNetworkSize" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					Charge.IAccess node = getAccess(dimension, tile);
					if(node != null) {
						return new Object[] { node.getBattery().map(IBattery::getCharge).orElse(0D) };
					}
					return new Object[] { null, "no node found" };
				}
				case 1: {
					Charge.IAccess node = getAccess(dimension, tile);
					if(node != null) {
						return new Object[] { node.getBattery().map(IBattery::getCapacity).orElse(0D) };
					}
					return new Object[] { null, "no node found" };
				}
				case 2: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getChargeSpec().getLosses() };
					}
					return new Object[] { null, "no node found" };
				}
				case 3: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getBattery().map(BatteryBlock::getMaxDraw).orElse(0D) };
					}
					return new Object[] { null, "no node found" };
				}
				case 4: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getBattery().map(BatteryBlock::getEfficiency).orElse(0D) };
					}
					return new Object[] { null, "no node found" };
				}
				case 5: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getCharge() };
					}
					return new Object[] { null, "no node found" };
				}
				case 6: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getCapacity() };
					}
					return new Object[] { null, "no node found" };
				}
				case 7: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getLosses() };
					}
					return new Object[] { null, "no node found" };
				}
				case 8: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getAverageUsagePerTick() };
					}
					return new Object[] { null, "no node found" };
				}
				case 9: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getPotentialDraw() };
					}
					return new Object[] { null, "no node found" };
				}
				case 10: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().getEfficiency() };
					}
					return new Object[] { null, "no node found" };
				}
				case 11: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getGrid().size() };
					}
					return new Object[] { null, "no node found" };
				}
			}
			return new Object[] {};
		}
	}
}
