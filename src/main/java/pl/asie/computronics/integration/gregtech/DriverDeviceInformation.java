package pl.asie.computronics.integration.gregtech;

import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

public class DriverDeviceInformation extends DriverTileEntity {
	public class ManagedEnvironmentInfo extends li.cil.oc.api.prefab.ManagedEnvironment {
		private IGregTechDeviceInformation info;
		
		public ManagedEnvironmentInfo(IGregTechDeviceInformation info) {
			this.info = info;
			node = Network.newNode(this, Visibility.Network).withComponent("gtDeviceInformation", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getSensorInformation(Context c, Arguments a) {
			return new Object[]{info.getInfoData()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		IGregTechDeviceInformation dc = (IGregTechDeviceInformation)world.getTileEntity(x, y, z);
		if(dc != null && dc.isGivingInformation()) return new ManagedEnvironmentInfo((IGregTechDeviceInformation)world.getTileEntity(x, y, z));
		else return null;
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IGregTechDeviceInformation.class;
	}
}
