package pl.asie.computronics.integration.gregtech.gregtech5;

import gregtech.api.interfaces.tileentity.IMachineProgress;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverMachine extends DriverSidedTileEntity {

	public static class ManagedEnvironmentMachine extends ManagedEnvironmentOCTile<IMachineProgress> {

		public ManagedEnvironmentMachine(IMachineProgress tile, String name) {
			super(tile, name);
		}

		@Override
		public int priority() {
			return 1;
		}

		@Callback(doc = "function():boolean; Returns true if the machine currently has work to do", direct = true)
		public Object[] hasWork(Context c, Arguments a) {
			return new Object[] { tile.hasThingsToDo() };
		}

		@Callback(doc = "function():number; Returns the current progress of this block", direct = true)
		public Object[] getWorkProgress(Context c, Arguments a) {
			return new Object[] { tile.getProgress() };
		}

		@Callback(doc = "function():number; Returns the max progress of this block", direct = true)
		public Object[] getWorkMaxProgress(Context c, Arguments a) {
			return new Object[] { tile.getMaxProgress() };
		}

		@Callback(doc = "function():boolean; Returns whether this block is currently allowed to work", direct = true)
		public Object[] isWorkAllowed(Context c, Arguments a) {
			return new Object[] { tile.isAllowedToWork() };
		}

		@Callback(doc = "function(work:boolean); Sets whether this block is currently allowed to work", direct = true)
		public Object[] setWorkAllowed(Context c, Arguments a) {
			if(a.count() == 1 && a.isBoolean(0)) {
				if(a.checkBoolean(0)) {
					tile.enableWorking();
				} else {
					tile.disableWorking();
				}
			}
			return null;
		}

		@Callback(doc = "function():boolean; Returns whether the machine is currently active", direct = true)
		public Object[] isMachineActive(Context c, Arguments a) {
			return new Object[] { tile.isActive() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IMachineProgress.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
		return new ManagedEnvironmentMachine((IMachineProgress) world.getTileEntity(x, y, z), "gt_machine");
	}
}
