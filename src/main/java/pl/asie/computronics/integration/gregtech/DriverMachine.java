package pl.asie.computronics.integration.gregtech;

import gregtech.api.interfaces.tileentity.IMachineProgress;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

public class DriverMachine extends DriverTileEntity {
	public class ManagedEnvironmentMachine extends li.cil.oc.api.prefab.ManagedEnvironment {
		private IMachineProgress mp;
		
		public ManagedEnvironmentMachine(IMachineProgress dc) {
			this.mp = dc;
			node = Network.newNode(this, Visibility.Network).withComponent("gtMachine", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] hasWork(Context c, Arguments a) {
			return new Object[]{mp.hasThingsToDo()};
		}
		
		@Callback(direct = true)
		public Object[] getWorkProgress(Context c, Arguments a) {
			return new Object[]{mp.getProgress()};
		}
		
		@Callback(direct = true)
		public Object[] getWorkMaxProgress(Context c, Arguments a) {
			return new Object[]{mp.getMaxProgress()};
		}
		
		@Callback(direct = true)
		public Object[] isWorkAllowed(Context c, Arguments a) {
			return new Object[]{mp.isAllowedToWork()};
		}
		
		@Callback(direct = true)
		public Object[] setWorkAllowed(Context c, Arguments a) {
			if(a.count() == 1 && a.isBoolean(0)) {
				if(a.checkBoolean(0)) mp.enableWorking();
				else mp.disableWorking();
			}
			return null;
		}
		
		@Callback(direct = true)
		public Object[] isMachineActive(Context c, Arguments a) {
			return new Object[]{mp.isActive()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentMachine((IMachineProgress)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IMachineProgress.class;
	}
}
