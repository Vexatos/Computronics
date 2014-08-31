package pl.asie.computronics.integration.betterstorage;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.mcft.copy.betterstorage.api.ICrateStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverCrateStorage extends DriverTileEntity {
	public class ManagedEnvironmentCrate extends ManagedEnvironmentOCTile<ICrateStorage> {
		public ManagedEnvironmentCrate(ICrateStorage tile, String name) {
			super(tile, name);
		}

		@Callback()
		public Object[] getContents(Context c, Arguments a) {
			List<ItemStack> l = tile.getContents(ForgeDirection.UNKNOWN);
			return new Object[]{l.toArray(new ItemStack[l.size()])};
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
