package pl.asie.computronics.integration.flamingo;

import com.reddit.user.koppeh.flamingo.TileEntityFlamingo;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.tis3d.api.serial.SerialInterface;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.tis3d.serial.TileInterfaceProvider;
import pl.asie.computronics.integration.tis3d.serial.TileSerialInterface;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverFlamingo {

	private static void wiggle(TileEntityFlamingo tile) {
		tile.getWorldObj().addBlockEvent(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType(), 0, 0);
	}

	public static class OCDriver extends DriverSidedTileEntity {

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
				DriverFlamingo.wiggle(tile);
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
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((TileEntityFlamingo) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileEntityFlamingo> {

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
					DriverFlamingo.wiggle(tile);
				}
				case 1: {
					return new Object[] { tile.wiggleStrength };
				}
			}
			return null;
		}
	}

	public static class TISInterfaceProvider extends TileInterfaceProvider {

		public TISInterfaceProvider() {
			super(TileEntityFlamingo.class, "flamingo", "flamingo.md");
		}

		public static class InternalSerialInterface extends TileSerialInterface<TileEntityFlamingo> {

			public InternalSerialInterface(TileEntityFlamingo tile) {
				super(tile);
			}

			@Override
			public boolean canWrite() {
				return true;
			}

			@Override
			public void write(short value) {
				DriverFlamingo.wiggle(tile);
			}

			@Override
			public boolean canRead() {
				return false;
			}

			@Override
			public short peek() {
				return 0;
			}
		}

		@Override
		public SerialInterface interfaceFor(World world, int x, int y, int z, EnumFacing side) {
			return new InternalSerialInterface((TileEntityFlamingo) world.getTileEntity(x, y, z));
		}

		@Override
		protected boolean isStillValid(World world, int x, int y, int z, EnumFacing side, SerialInterface serialInterface, TileEntity tile) {
			return tile instanceof TileEntityFlamingo
				&& serialInterface instanceof InternalSerialInterface
				&& ((InternalSerialInterface) serialInterface).isTileEqual(tile);
		}
	}
}
