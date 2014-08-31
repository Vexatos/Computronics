package pl.asie.computronics.integration.gregtech;

import gregtech.api.interfaces.tileentity.IMachineProgress;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverMachine extends DriverTileEntity {
	public class ManagedEnvironmentMachine extends ManagedEnvironmentOCTile<IMachineProgress> {
		public ManagedEnvironmentMachine(IMachineProgress tile, String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] hasWork(Context c, Arguments a) {
			return new Object[]{tile.hasThingsToDo()};
		}
		
		@Callback(direct = true)
		public Object[] getWorkProgress(Context c, Arguments a) {
			return new Object[]{tile.getProgress()};
		}
		
		@Callback(direct = true)
		public Object[] getWorkMaxProgress(Context c, Arguments a) {
			return new Object[]{tile.getMaxProgress()};
		}
		
		@Callback(direct = true)
		public Object[] isWorkAllowed(Context c, Arguments a) {
			return new Object[]{tile.isAllowedToWork()};
		}
		
		@Callback(direct = true)
		public Object[] setWorkAllowed(Context c, Arguments a) {
			if(a.count() == 1 && a.isBoolean(0)) {
				if(a.checkBoolean(0)) tile.enableWorking();
				else tile.disableWorking();
			}
			return null;
		}
		
		@Callback(direct = true)
		public Object[] isMachineActive(Context c, Arguments a) {
			return new Object[]{tile.isActive()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentMachine((IMachineProgress)world.getTileEntity(x, y, z), "gt_machine");
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IMachineProgress.class;
	}
}
