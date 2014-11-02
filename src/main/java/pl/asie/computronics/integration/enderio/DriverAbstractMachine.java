package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.AbstractMachineEntity;
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
public class DriverAbstractMachine {

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<AbstractMachineEntity> {
			public InternalManagedEnvironment(AbstractMachineEntity tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 5;
			}

			@Callback(doc = "function():boolean; Returns whether the machine is currently active")
			public Object[] isActive(Context c, Arguments a) {
				return new Object[] { tile.isActive() };
			}

			@Callback(doc = "function():number; Returns the power usage/production per tick")
			public Object[] getPowerPerTick(Context c, Arguments a) {
				return new Object[] { tile.getPowerUsePerTick() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return AbstractMachineEntity.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment(((AbstractMachineEntity) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<AbstractMachineEntity> {

		public CCDriver() {
		}

		public CCDriver(AbstractMachineEntity tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_MachineTile, world, x, y, z);
		}

		@Override
		public int priority() {
			return 5;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof AbstractMachineEntity) {
				return new CCDriver((AbstractMachineEntity) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isActive", "getPowerPerTick" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					return new Object[] { tile.isActive() };
				}
				case 1:{
					return new Object[] { tile.getPowerUsePerTick() };
				}
			}
			return null;
		}
	}
}
