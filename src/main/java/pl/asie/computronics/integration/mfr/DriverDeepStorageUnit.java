package pl.asie.computronics.integration.mfr;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class DriverDeepStorageUnit extends DriverTileEntity {
	public class ManagedEnvironmentDSU extends li.cil.oc.api.prefab.ManagedEnvironment {
		private IDeepStorageUnit dsu;
		
		public ManagedEnvironmentDSU(IDeepStorageUnit dsu) {
			this.dsu = dsu;
			node = Network.newNode(this, Visibility.Network).withComponent("deep_storage_unit", Visibility.Network).create();
		}
		
		@Callback()
		public Object[] getStoredItem(Context c, Arguments a) {
			return new Object[]{dsu.getStoredItemType()};
		}
		
		@Callback(direct = true)
		public Object[] isLocked(Context c, Arguments a) {
			ItemStack is = dsu.getStoredItemType();
			return new Object[]{(is != null && is.stackSize == 0)};
		}
		
		@Callback(direct = true)
		public Object[] getMaxItemCount(Context c, Arguments a) {
			return new Object[]{dsu.getMaxStoredCount()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentDSU((IDeepStorageUnit)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IDeepStorageUnit.class;
	}
}
