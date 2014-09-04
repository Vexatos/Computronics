package pl.asie.computronics.integration.betterstorage;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.mcft.copy.betterstorage.api.crate.ICrateStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

import java.util.ArrayList;
import java.util.List;

public class DriverCrateStorageNew extends DriverTileEntity {
	public class ManagedEnvironmentCrate extends ManagedEnvironmentOCTile<ICrateStorage> {
		public ManagedEnvironmentCrate(ICrateStorage tile, String name) {
			super(tile, name);
		}

		@Callback()
		public Object[] getContents(Context c, Arguments a) {
			List<ItemStack> l = new ArrayList<ItemStack>();
			for(ItemStack is: tile.getContents()) {
				l.add(is);
			}
			return new Object[]{l.toArray(new ItemStack[l.size()])};
		}
		
		@Callback(direct = true)
		public Object[] getCapacity(Context c, Arguments a) {
			return new Object[]{tile.getCapacity()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentCrate((ICrateStorage)world.getTileEntity(x, y, z), "crate");
	}

	@Override
	public Class<?> getTileEntityClass() {
		return ICrateStorage.class;
	}
}
