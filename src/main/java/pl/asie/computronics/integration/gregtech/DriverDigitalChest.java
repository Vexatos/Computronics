package pl.asie.computronics.integration.gregtech;

import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import gregtech.api.interfaces.tileentity.IDigitalChest;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

public class DriverDigitalChest extends DriverTileEntity {
	public class ManagedEnvironmentDC extends ManagedEnvironmentOCTile<IDigitalChest> {
		public ManagedEnvironmentDC(IDigitalChest tile, String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] getContents(Context c, Arguments a) {
			return new Object[]{tile.getStoredItemData()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		IDigitalChest dc = (IDigitalChest)world.getTileEntity(x, y, z);
		if(dc != null && dc.isDigitalChest()) return new ManagedEnvironmentDC((IDigitalChest)world.getTileEntity(x, y, z), "digital_chest");
		else return null;
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IDigitalChest.class;
	}
}
