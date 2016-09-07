package pl.asie.computronics.integration.gregtech.gregtech5;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

/*
 * GREEEEEEEEEEEEEEEEEEEEEEEEG
 */
public class DriverBaseMetaTileEntity extends DriverSidedTileEntity {

	public static class ManagedEnvironmentMachine extends ManagedEnvironmentOCTile<BaseMetaTileEntity> {

		public ManagedEnvironmentMachine(BaseMetaTileEntity tile, String name) {
			super(tile, name);
		}

		@Callback(doc = "function():number; Returns the EU stored in this block", direct = true)
		public Object[] getEUStored(Context c, Arguments a) {
			return new Object[] { tile.getStoredEU() };
		}

		@Callback(doc = "function():number; Returns the steam stored in this block", direct = true)
		public Object[] getSteamStored(Context c, Arguments a) {
			return new Object[] { tile.getStoredSteam() };
		}

		@Callback(doc = "function():number; Returns the max EU that can be stored in this block", direct = true)
		public Object[] getEUMaxStored(Context c, Arguments a) {
			return new Object[] { tile.getEUCapacity() };
		}

		@Callback(doc = "function():number; Returns the max steam that can be stored in this block", direct = true)
		public Object[] getSteamMaxStored(Context c, Arguments a) {
			return new Object[] { tile.getSteamCapacity() };
		}

		@Callback(doc = "function():number; Returns the average EU input of this block", direct = true)
		public Object[] getEUInputAverage(Context c, Arguments a) {
			return new Object[] { tile.getAverageElectricInput() };
		}

		@Callback(doc = "function():number; Returns the average EU output of this block", direct = true)
		public Object[] getEUOutputAverage(Context c, Arguments a) {
			return new Object[] { tile.getAverageElectricOutput() };
		}

		@Callback(doc = "function():string; Returns the name of this block's owner", direct = true)
		public Object[] getOwnerName(Context c, Arguments a) {
			return new Object[] { tile.getOwnerName() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return BaseMetaTileEntity.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
		return new ManagedEnvironmentMachine((BaseMetaTileEntity) world.getTileEntity(x, y, z), "gt_machine");
	}
}
