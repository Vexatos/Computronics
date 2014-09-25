package pl.asie.computronics.integration.factorization;

import factorization.api.IChargeConductor;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverChargeConductor extends DriverTileEntity {
	public class ManagedEnvironmentCC extends ManagedEnvironmentOCTile<IChargeConductor> {
		public ManagedEnvironmentCC(IChargeConductor tile, String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] getCharge(Context c, Arguments a) {
			if(tile.getCharge() != null) {
				return new Object[] { tile.getCharge().getValue() };
			} else {
				return new Object[] { 0 };
			}
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IChargeConductor.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentCC((IChargeConductor) world.getTileEntity(x, y, z), "charge_conductor");
	}
}
