package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.AbstractMachineEntity;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverAbstractMachine {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<AbstractMachineEntity> {

			public InternalManagedEnvironment(AbstractMachineEntity tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function():boolean; Returns whether the machine is currently active")
			public Object[] isActive(Context c, Arguments a) {
				return new Object[] { tile.isActive() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return AbstractMachineEntity.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
			return new InternalManagedEnvironment(((AbstractMachineEntity) world.getTileEntity(pos)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<AbstractMachineEntity> {

		public CCDriver() {
		}

		public CCDriver(AbstractMachineEntity tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_MachineTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof AbstractMachineEntity) {
				return new CCDriver((AbstractMachineEntity) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isActive" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.isActive() };
				}
			}
			return null;
		}
	}
}
