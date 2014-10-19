package pl.asie.computronics.integration.mfr;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class DriverDeepStorageUnit {
	public static class OCDriver extends DriverTileEntity {
		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IDeepStorageUnit> {
			public InternalManagedEnvironment(IDeepStorageUnit tile, String name) {
				super(tile, name);
			}

			@Callback()
			public Object[] getStoredItem(Context c, Arguments a) {
				return new Object[] { tile.getStoredItemType() };
			}

			@Callback(direct = true)
			public Object[] isLocked(Context c, Arguments a) {
				ItemStack is = tile.getStoredItemType();
				return new Object[] { (is != null && is.stackSize == 0) };
			}

			@Callback(direct = true)
			public Object[] getMaxItemCount(Context c, Arguments a) {
				return new Object[] { tile.getMaxStoredCount() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IDeepStorageUnit.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((IDeepStorageUnit) world.getTileEntity(x, y, z), Names.MFR_DSU);
		}
	}

	public static class CCDriver extends CCTilePeripheral<IDeepStorageUnit> {

		public CCDriver() {
		}

		public CCDriver(IDeepStorageUnit dsu, World world, int x, int y, int z) {
			super(dsu, Names.MFR_DSU, world, x, y, z);
		}

		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IDeepStorageUnit) {
				return new CCDriver((IDeepStorageUnit) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getItemName", "getItemDamage", "getItemCount", "isLocked", "getMaxItemCount" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			ItemStack is = tile.getStoredItemType();
			if(method < 4 && (is == null)) {
				if(method == 3) {
					return new Object[] { false };
				} else if(method > 0) {
					return new Object[] { 0 };
				} else {
					return new Object[] { null };
				}
			}
			switch(method){
				case 0:
					return new Object[] { is.getUnlocalizedName() };
				case 1:
					return new Object[] { is.getItemDamage() };
				case 2:
					return new Object[] { is.stackSize };
				case 3:
					return new Object[] { is.stackSize == 0 };
				case 4:
					return new Object[] { tile.getMaxStoredCount() };
			}
			return null;
		}
	}
}
