package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.power.TileCapacitorBank;
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
public class DriverCapacitorBank {

	public static class OCDriver extends DriverTileEntity {
		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileCapacitorBank> {
			public InternalManagedEnvironment(TileCapacitorBank tile) {
				super(tile, Names.EnderIO_CapacitorBank);
			}

			@Override
			public int priority() {
				return 5;
			}

			@Callback(doc = "function():number; Returns the maximum input of the capacitor bank")
			public Object[] getMaxInput(Context c, Arguments a) {
				return new Object[] { tile.getEnergyStored() };
			}

			@Callback(doc = "function():number; Returns the maximum output of the capacitor bank")
			public Object[] getMaxOutput(Context c, Arguments a) {
				return new Object[] { tile.getMaxEnergyStored() };
			}

			@Callback(doc = "function(max:number); Sets the max input of the capacitor bank")
			public Object[] setMaxInput(Context c, Arguments a) {
				tile.setMaxInput(a.checkInteger(0));
				return new Object[] { };
			}

			@Callback(doc = "function(max:number); Sets the max output of the capacitor bank")
			public Object[] setMaxOutput(Context c, Arguments a) {
				tile.setMaxOutput(a.checkInteger(0));
				return new Object[] { };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileCapacitorBank.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment(((TileCapacitorBank) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileCapacitorBank> {

		public CCDriver() {
		}

		public CCDriver(TileCapacitorBank tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_CapacitorBank, world, x, y, z);
		}

		@Override
		public int priority() {
			return 5;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileCapacitorBank) {
				return new CCDriver((TileCapacitorBank) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getMaxInput", "getMaxOutput", "setMaxInput", "setMaxOutput" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					return new Object[] { tile.getMaxInput() };
				}
				case 1:{
					return new Object[] { tile.getMaxOutput() };
				}
				case 2:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setMaxInput(((Double) arguments[0]).intValue());
					return new Object[] { };
				}
				case 3:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setMaxOutput(((Double) arguments[0]).intValue());
					return new Object[] { };
				}
			}
			return null;
		}
	}
}
