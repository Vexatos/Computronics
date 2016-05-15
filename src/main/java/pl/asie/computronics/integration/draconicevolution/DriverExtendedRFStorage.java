package pl.asie.computronics.integration.draconicevolution;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
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
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverExtendedRFStorage {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IExtendedRFStorage> {

			public InternalManagedEnvironment(IExtendedRFStorage tile) {
				super(tile, Names.DraconicEvolution_Storage);
			}

			@Override
			public int priority() {
				return 1;
			}

			@Callback(doc = "function():number;  Returns the total amount of stored energy.")
			public Object[] getEnergyStored(Context c, Arguments a) {
				return new Object[] { tile.getEnergyStored() };
			}

			@Callback(doc = "function():number;  Returns the maximum amount of stored energy.")
			public Object[] getMaxEnergyStored(Context c, Arguments a) {
				return new Object[] { tile.getMaxEnergyStored() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IExtendedRFStorage.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((IExtendedRFStorage) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IExtendedRFStorage> {

		public CCDriver() {
		}

		public CCDriver(IExtendedRFStorage tile, World world, int x, int y, int z) {
			super(tile, Names.DraconicEvolution_Storage, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IExtendedRFStorage) {
				return new CCDriver((IExtendedRFStorage) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getEnergyStored", "getMaxEnergyStored" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getEnergyStored() };
				}
				case 1: {
					return new Object[] { tile.getMaxEnergyStored() };
				}
			}
			return null;
		}
	}
}
