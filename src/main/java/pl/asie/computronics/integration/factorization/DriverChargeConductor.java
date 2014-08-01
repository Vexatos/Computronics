package pl.asie.computronics.integration.factorization;

import java.util.List;

import factorization.api.IChargeConductor;
import factorization.crafting.TileEntityCompressionCrafter;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverBlock;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

public class DriverChargeConductor extends DriverTileEntity {
	public class ManagedEnvironmentCC extends li.cil.oc.api.prefab.ManagedEnvironment {
		private IChargeConductor cc;
		
		public ManagedEnvironmentCC(IChargeConductor cc) {
			this.cc = cc;
			node = Network.newNode(this, Visibility.Network).withComponent("chargeConductor", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getCharge(Context c, Arguments a) {
			if(cc.getCharge() != null) {
				return new Object[]{cc.getCharge().getValue()};
			} else return new Object[]{0};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentCC((IChargeConductor)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IChargeConductor.class;
	}
}
