package pl.asie.computronics.integration.gregtech;

import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverDeviceInformation extends DriverTileEntity {
	public class ManagedEnvironmentInfo extends ManagedEnvironmentOCTile<IGregTechDeviceInformation> {
		public ManagedEnvironmentInfo(IGregTechDeviceInformation tile,
				String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] getSensorInformation(Context c, Arguments a) {
			return new Object[]{tile.getInfoData()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		IGregTechDeviceInformation dc = (IGregTechDeviceInformation)world.getTileEntity(x, y, z);
		if(dc != null && dc.isGivingInformation()) return new ManagedEnvironmentInfo((IGregTechDeviceInformation)world.getTileEntity(x, y, z), "gt_machine");
		else return null;
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IGregTechDeviceInformation.class;
	}
}
