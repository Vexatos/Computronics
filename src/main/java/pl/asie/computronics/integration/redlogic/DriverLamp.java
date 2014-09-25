package pl.asie.computronics.integration.redlogic;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverBlock;
import mods.immibis.redlogic.api.misc.ILampBlock;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DriverLamp extends DriverBlock {
	public class ManagedEnvironmentLamp extends li.cil.oc.api.prefab.ManagedEnvironment {
		private ILampBlock block;
		private IBlockAccess w;
		private int x, y, z;
		
		public ManagedEnvironmentLamp(ILampBlock block, IBlockAccess w, int x, int y, int z) {
			this.block = block;
			this.w = w;
			this.x = x;
			this.y = y;
			this.z = z;
			node = Network.newNode(this, Visibility.Network).withComponent("lamp", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getLampType(Context c, Arguments a) {
			return new Object[]{block.getType().name()};
		}
		
		@Callback(direct = true)
		public Object[] getLampColor(Context c, Arguments a) {
			return new Object[]{block.getColourRGB(w, x, y, z)};
		}
		
		@Callback(direct = true)
		public Object[] isLampPowered(Context c, Arguments a) {
			return new Object[]{block.isPowered()};
		}
	}

	@Override
	public boolean worksWith(final World world, final int x, final int y, final int z) {
		return world.getBlock(x, y, z) instanceof ILampBlock;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if(block instanceof ILampBlock) return new ManagedEnvironmentLamp((ILampBlock)block, world, x, y, z);
		else return null;
	}
}
