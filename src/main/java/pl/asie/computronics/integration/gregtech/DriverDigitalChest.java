package pl.asie.computronics.integration.gregtech;

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
	public class ManagedEnvironmentDC extends li.cil.oc.api.prefab.ManagedEnvironment {
		private IDigitalChest dc;
		
		public ManagedEnvironmentDC(IDigitalChest dc) {
			this.dc = dc;
			node = Network.newNode(this, Visibility.Network).withComponent("digitalChest", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getContents(Context c, Arguments a) {
			return new Object[]{dc.getStoredItemData()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		IDigitalChest dc = (IDigitalChest)world.getTileEntity(x, y, z);
		if(dc != null && dc.isDigitalChest()) return new ManagedEnvironmentDC((IDigitalChest)world.getTileEntity(x, y, z));
		else return null;
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IDigitalChest.class;
	}
}
