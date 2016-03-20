package pl.asie.computronics.integration.enderio;

import com.enderio.core.api.common.util.IProgressTile;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverProgressTile {
	public static class OCDriver extends DriverSidedTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IProgressTile> {

			public InternalManagedEnvironment(IProgressTile tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 2;
			}

			@Callback(doc = "function():boolean; Returns the progress of the machine")
			public Object[] getProgress(Context c, Arguments a) {
				return new Object[] { tile.getProgress() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IProgressTile.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
			return new InternalManagedEnvironment(((IProgressTile) world.getTileEntity(pos)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IProgressTile> {

		public CCDriver() {
		}

		public CCDriver(IProgressTile tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_MachineTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 2;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IProgressTile) {
				return new CCDriver((IProgressTile) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getProgress" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getProgress() };
				}
			}
			return null;
		}
	}
}
