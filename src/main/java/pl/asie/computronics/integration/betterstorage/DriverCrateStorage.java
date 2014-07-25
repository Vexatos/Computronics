package pl.asie.computronics.integration.betterstorage;

import java.util.List;

import mods.immibis.redlogic.api.misc.ILampBlock;
import net.mcft.copy.betterstorage.api.ICrateStorage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverBlock;
import li.cil.oc.api.prefab.DriverTileEntity;

public class DriverCrateStorage extends DriverTileEntity {
	public class ManagedEnvironmentCrate extends li.cil.oc.api.prefab.ManagedEnvironment {
		private ICrateStorage crate;
		
		public ManagedEnvironmentCrate(ICrateStorage crate) {
			this.crate = crate;
			node = Network.newNode(this, Visibility.Network).withComponent("crate", Visibility.Network).create();
		}
		
		@Callback()
		public Object[] getContents(Context c, Arguments a) {
			List<ItemStack> l = crate.getContents(ForgeDirection.UNKNOWN);
			return new Object[]{l.toArray(new ItemStack[l.size()])};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentCrate((ICrateStorage)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return ICrateStorage.class;
	}
}
