package pl.asie.computronics.integration.enderio;

import crazypants.enderio.machine.AbstractPoweredMachineEntity;
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
public class DriverAbstractPoweredMachine {

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<AbstractPoweredMachineEntity> {

			public InternalManagedEnvironment(AbstractPoweredMachineEntity tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function():number; Returns the power use/production per tick")
			public Object[] getPowerPerTick(Context c, Arguments a) {
				return new Object[] { tile.getPowerUsePerTick() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return AbstractPoweredMachineEntity.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((AbstractPoweredMachineEntity) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<AbstractPoweredMachineEntity> {

		public CCDriver() {
		}

		public CCDriver(AbstractPoweredMachineEntity tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_MachineTile, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof AbstractPoweredMachineEntity) {
				return new CCDriver((AbstractPoweredMachineEntity) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getPowerPerTick" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getPowerUsePerTick() };
				}
			}
			return null;
		}
	}

}

