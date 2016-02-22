package pl.asie.computronics.integration.gregtech;

import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverDeviceInformation extends DriverTileEntity {

	public static class ManagedEnvironmentInfo extends ManagedEnvironmentOCTile<IGregTechDeviceInformation> {

		public ManagedEnvironmentInfo(IGregTechDeviceInformation tile,
			String name) {
			super(tile, name);
		}

		@Callback(doc = "function():table; Returns sensor information about this block", direct = true)
		public Object[] getSensorInformation(Context c, Arguments a) {
			return new Object[] { tile.getInfoData() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IGregTechDeviceInformation.class;
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		return tileEntity != null && tileEntity instanceof IGregTechDeviceInformation
			&& ((IGregTechDeviceInformation) tileEntity).isGivingInformation();
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentInfo((IGregTechDeviceInformation) world.getTileEntity(x, y, z), "gt_machine");
	}
}
