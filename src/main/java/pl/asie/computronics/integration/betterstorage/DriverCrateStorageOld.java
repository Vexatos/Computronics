package pl.asie.computronics.integration.betterstorage;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.mcft.copy.betterstorage.api.ICrateStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

import java.util.List;

public class DriverCrateStorageOld extends DriverSidedTileEntity {

	public static class ManagedEnvironmentCrate extends ManagedEnvironmentOCTile<ICrateStorage> {

		public ManagedEnvironmentCrate(ICrateStorage tile, String name) {
			super(tile, name);
		}

		@Callback(doc = "function():table; Returns a table of the crate's contents")
		public Object[] getContents(Context c, Arguments a) {
			List<ItemStack> l = tile.getContents(ForgeDirection.UNKNOWN);
			return new Object[] { l.toArray(new ItemStack[l.size()]) };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return ICrateStorage.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
		return new ManagedEnvironmentCrate((ICrateStorage) world.getTileEntity(x, y, z), "crate");
	}
}
