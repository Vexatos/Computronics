package pl.asie.computronics.integration.enderio;

import crazypants.enderio.power.IInternalPowerReceptor;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.util.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverPowerReceptor {

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IInternalPowerReceptor> {

			public InternalManagedEnvironment(IInternalPowerReceptor tile) {
				super(tile, Names.EnderIO_PoweredTile);
			}

			@Override
			public int priority() {
				return 1;
			}

			@Callback(doc = "function():number; Returns the current energy stored in the tile")
			public Object[] getEnergyStored(Context c, Arguments a) {
				return new Object[] { tile.getEnergyStored() };
			}

			@Callback(doc = "function():number; Returns the maximum energy the tile can store")
			public Object[] getMaxEnergyStored(Context c, Arguments a) {
				return new Object[] { tile.getMaxEnergyStored() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IInternalPowerReceptor.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment(((IInternalPowerReceptor) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IInternalPowerReceptor> {

		public CCDriver() {
		}

		public CCDriver(IInternalPowerReceptor tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_PoweredTile, world, x, y, z);
		}

		@Override
		public int priority() {
			return 1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IInternalPowerReceptor) {
				return new CCDriver((IInternalPowerReceptor) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getEnergyStored", "getMaxEnergyStored" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					return new Object[] { tile.getEnergyStored() };
				}
				case 1:{
					return new Object[] { tile.getMaxEnergyStored() };
				}
			}
			return null;
		}
	}
}
