package pl.asie.computronics.integration.mfr;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class DriverDeepStorageUnit extends DriverTileEntity {
	public class ManagedEnvironmentDSU extends ManagedEnvironmentOCTile<IDeepStorageUnit> {
		public ManagedEnvironmentDSU(IDeepStorageUnit tile, String name) {
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
		return new ManagedEnvironmentDSU((IDeepStorageUnit) world.getTileEntity(x, y, z), "deep_storage_unit");
	}
}
