package pl.asie.computronics.integration.enderio;

import crazypants.enderio.base.power.IPowerStorage;
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
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverPowerStorage {

	public static class OCDriver extends DriverSpecificTileEntity<IPowerStorage> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<IPowerStorage> {

			public InternalManagedEnvironment(IPowerStorage tile) {
				super(tile, Names.EnderIO_PowerStorage);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function():number; Returns the maximum input of the storage device")
			public Object[] getMaxInput(Context c, Arguments a) {
				return new Object[] { tile.getMaxInput() };
			}

			@Callback(doc = "function():number; Returns the maximum output of the storage device")
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

		public OCDriver() {
			super(IPowerStorage.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, IPowerStorage tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IPowerStorage> {

		public CCDriver() {
		}

		public CCDriver(IPowerStorage tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_PowerStorage, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IPowerStorage) {
				return new CCDriver((IPowerStorage) te, world, pos);
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
			return new Object[] {};
		}
	}
}
