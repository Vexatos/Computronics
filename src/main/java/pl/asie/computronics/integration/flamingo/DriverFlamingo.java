package pl.asie.computronics.integration.flamingo;

import com.reddit.user.koppeh.flamingo.TileEntityFlamingo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

//import dan200.computercraft.api.lua.ILuaContext;
//import dan200.computercraft.api.lua.LuaException;
//import dan200.computercraft.api.peripheral.IComputerAccess;
//import pl.asie.computronics.integration.CCMultiPeripheral;

/**
 * @author Vexatos
 */
public class DriverFlamingo {

	public static class OCDriver extends DriverTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileEntityFlamingo> {

			public InternalManagedEnvironment(TileEntityFlamingo tile) {
				super(tile, Names.Flamingo_Flamingo);
			}

			@Override
			public int priority() {
				return 100;
			}

			@Callback(doc = "function(); Makes the Flamingo wiggle.")
			public Object[] wiggle(Context c, Arguments a) {
				tile.getWorld().addBlockEvent(tile.getPos(), tile.getBlockType(), 0, 0);
				return new Object[] {};
			}

			@Callback(doc = "function():number; Returns the Flamingo's wiggle strength.")
			public Object[] getWiggleStrength(Context c, Arguments a) {
				return new Object[] { tile.wiggleStrength };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileEntityFlamingo.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
			return new InternalManagedEnvironment(((TileEntityFlamingo) world.getTileEntity(pos)));
		}
	}

	/*public static class CCDriver extends CCMultiPeripheral<TileEntityFlamingo> {

		public CCDriver() {
		}

		public CCDriver(TileEntityFlamingo tile, World world, int x, int y, int z) {
			super(tile, Names.Flamingo_Flamingo, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 100;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityFlamingo) {
				return new CCDriver((TileEntityFlamingo) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "wiggle", "getWiggleStrength" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					tile.getWorldObj().addBlockEvent(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType(), 0, 0);
				}
				case 1: {
					return new Object[] { tile.wiggleStrength };
				}
			}
			return null;
		}
	}*/
}
