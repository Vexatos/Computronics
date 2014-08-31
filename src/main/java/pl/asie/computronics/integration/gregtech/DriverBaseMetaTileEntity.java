package pl.asie.computronics.integration.gregtech;

import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

/*
 * GREEEEEEEEEEEEEEEEEEEEEEEEG
 */
public class DriverBaseMetaTileEntity extends DriverTileEntity {
	public class ManagedEnvironmentMachine extends ManagedEnvironmentOCTile<BaseMetaTileEntity> {
		public ManagedEnvironmentMachine(BaseMetaTileEntity tile, String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] getEUStored(Context c, Arguments a) {
			return new Object[]{tile.getStoredEU()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamStored(Context c, Arguments a) {
			return new Object[]{tile.getStoredSteam()};
		}
		
		@Callback(direct = true)
		public Object[] getEUMaxStored(Context c, Arguments a) {
			return new Object[]{tile.getEUCapacity()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamMaxStored(Context c, Arguments a) {
			return new Object[]{tile.getSteamCapacity()};
		}
		
		@Callback(direct = true)
		public Object[] getEUInputAverage(Context c, Arguments a) {
			return new Object[]{tile.getAverageElectricInput()};
		}
		
		@Callback(direct = true)
		public Object[] getEUOutputAverage(Context c, Arguments a) {
			return new Object[]{tile.getAverageElectricOutput()};
		}
		
		@Callback(direct = true)
		public Object[] getOwnerName(Context c, Arguments a) {
			return new Object[]{tile.getOwnerName()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentMachine((BaseMetaTileEntity)world.getTileEntity(x, y, z), "gt_machine");
	}

	@Override
	public Class<?> getTileEntityClass() {
		return BaseMetaTileEntity.class;
	}
}
