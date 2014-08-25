package pl.asie.computronics.integration.gregtech;

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
	public class ManagedEnvironmentMachine extends li.cil.oc.api.prefab.ManagedEnvironment {
		private BaseMetaTileEntity te;
		
		public ManagedEnvironmentMachine(BaseMetaTileEntity dc) {
			this.te = dc;
			node = Network.newNode(this, Visibility.Network).withComponent("gtMachine", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getEUStored(Context c, Arguments a) {
			return new Object[]{te.getStoredEU()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamStored(Context c, Arguments a) {
			return new Object[]{te.getStoredSteam()};
		}
		
		@Callback(direct = true)
		public Object[] getEUMaxStored(Context c, Arguments a) {
			return new Object[]{te.getEUCapacity()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamMaxStored(Context c, Arguments a) {
			return new Object[]{te.getSteamCapacity()};
		}
		
		@Callback(direct = true)
		public Object[] getEUInputAverage(Context c, Arguments a) {
			return new Object[]{te.getAverageElectricInput()};
		}
		
		@Callback(direct = true)
		public Object[] getEUOutputAverage(Context c, Arguments a) {
			return new Object[]{te.getAverageElectricOutput()};
		}
		
		@Callback(direct = true)
		public Object[] getOwnerName(Context c, Arguments a) {
			return new Object[]{te.getOwnerName()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentMachine((BaseMetaTileEntity)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return BaseMetaTileEntity.class;
	}
}
