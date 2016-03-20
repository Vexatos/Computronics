package pl.asie.computronics.integration.enderio;

import crazypants.enderio.power.IPowerStorage;
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
public class DriverPowerStorage {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IPowerStorage> {

			public InternalManagedEnvironment(IPowerStorage tile) {
				super(tile, Names.EnderIO_CapacitorBank);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function():number; Returns the maximum input of the capacitor bank")
			public Object[] getMaxInput(Context c, Arguments a) {
				return new Object[] { tile.getMaxInput() };
			}

			@Callback(doc = "function():number; Returns the maximum output of the capacitor bank")
			public Object[] getMaxOutput(Context c, Arguments a) {
				return new Object[] { tile.getMaxOutput() };
			}

			@Callback(doc = "function():number;  Returns the total amount of stored energy.")
			public Object[] getEnergyStored(final Context context, final Arguments args) {
				return new Object[] { tile.getEnergyStoredL() };
			}

			@Callback(doc = "function():number;  Returns the maximum amount of stored energy.")
			public Object[] getMaxEnergyStored(final Context context, final Arguments args) {
				return new Object[] { tile.getMaxEnergyStoredL() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IPowerStorage.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((IPowerStorage) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IPowerStorage> {

		public CCDriver() {
		}

		public CCDriver(IPowerStorage tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_PowerStorage, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IPowerStorage) {
				return new CCDriver((IPowerStorage) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getMaxInput", "getMaxOutput", "getEnergyStored", "getMaxEnergyStored" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getMaxInput() };
				}
				case 1: {
					return new Object[] { tile.getMaxOutput() };
				}
				case 2: {
					return new Object[] { tile.getEnergyStoredL() };
				}
				case 3: {
					return new Object[] { tile.getMaxEnergyStoredL() };
				}
			}
			return null;
		}
	}
}
