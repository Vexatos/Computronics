package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.driver.SidedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
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
	private static ChargeNetwork.ChargeNode getNode(int dimension, BlockPos pos) {
		WorldServer world = DimensionManager.getWorld(dimension);
		return world == null ? null : ChargeManager.getNetwork(world).getNode(pos);
	}

	public static class OCDriver implements SidedBlock {

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
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getBattery() != null ? node.getBattery().getCharge() : 0 };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the maximum capacity of the charge conductor.")
			public Object[] getCapacity(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { /* TODO */ };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the loss per tick of the charge conductor.")
			public Object[] getLoss(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getChargeDef().getCost() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the current charge of the charge network.")
			public Object[] getNetworkCharge(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getChargeGraph().getCharge() };
				}
				return new Object[] { null, "no node found" };
			}

			// TODO Network capacity

			@Callback(doc = "function():number; Returns the loss per tick of the charge network.")
			public Object[] getNetworkLoss(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getChargeGraph().getMaintenanceCost() };
				}
				return new Object[] { null, "no node found" };
			}

			@Callback(doc = "function():number; Returns the draw per tick of the charge network.")
			public Object[] getNetworkDraw(Context c, Arguments a) {
				ChargeNetwork.ChargeNode node = getNode(dimension, tile);
				if(node != null) {
					return new Object[] { node.getChargeGraph().getAverageUsagePerTick() };
				}
				return new Object[] { null, "no node found" };
			}
		}

		@Override
		public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
			return !ChargeManager.getNetwork(world).getNode(pos).isNull();
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
			if(!ChargeManager.getNetwork(world).getNode(pos).isNull()) {
				return new CCDriver(world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getCharge", "getCapacity", "getLoss", "getDraw",
				"getNetworkCharge", "getNetworkCapacity", "getNetworkLoss", "getNetworkDraw" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getBattery() != null ? node.getBattery().getCharge() : 0 };
					}
					return new Object[] { null, "no node found" };
				}
				case 1: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { /* TODO */ };
					}
					return new Object[] { null, "no node found" };
				}
				case 2: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getChargeDef().getCost() };
					}
					return new Object[] { null, "no node found" };
				}
				case 3: {
					return new Object[] { /* TODO */ };
				}
				case 4: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getChargeGraph().getCharge() };
					}
					return new Object[] { null, "no node found" };
				}
				case 5: {
					return new Object[] { /* TODO */ };
				}
				case 6: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getChargeGraph().getMaintenanceCost() };
					}
					return new Object[] { null, "no node found" };
				}
				case 7: {
					ChargeNetwork.ChargeNode node = getNode(dimension, tile);
					if(node != null) {
						return new Object[] { node.getChargeGraph().getAverageUsagePerTick() };
					}
					return new Object[] { null, "no node found" };
				}
			}
			return new Object[] {};
		}
	}
}
