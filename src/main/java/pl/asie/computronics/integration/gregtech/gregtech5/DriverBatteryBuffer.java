package pl.asie.computronics.integration.gregtech.gregtech5;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicBatteryBuffer;
import gregtech.api.util.GT_ModHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

/**
 * @author Vexatos
 */
public class DriverBatteryBuffer extends DriverSidedTileEntity {

	public static class ManagedEnvironmentBatteryBuffer extends ManagedEnvironmentOCTile<BaseMetaTileEntity> {

		public ManagedEnvironmentBatteryBuffer(BaseMetaTileEntity tile, String name) {
			super(tile, name);
		}

		@Override
		public int priority() {
			return 2;
		}

		@Callback(doc = "function(slot:number):number; Returns the amount of stored EU in the battery in the specified slot")
		public Object[] getBatteryCharge(Context c, Arguments a) {
			int slot = a.checkInteger(0);
			if(slot <= 0 || slot > ((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory.length) {
				return new Object[] { null, "slot does not exist" };
			}
			if(((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory[slot - 1] == null) {
				return new Object[] { null, "slot is empty" };
			}
			ItemStack stack = ((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory[slot - 1];
			if(GT_ModHandler.isElectricItem(stack)) {
				return new Object[] { ElectricItem.manager.getCharge(stack) };
			} else {
				return new Object[] { null, "item in slot is not electric" };
			}
		}

		@Callback(doc = "function(slot:number):number; Returns the max amount of stored EU in the battery in the specified slot")
		public Object[] getMaxBatteryCharge(Context c, Arguments a) {
			int slot = a.checkInteger(0);
			if(slot <= 0 || slot > ((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory.length) {
				return new Object[] { null, "slot does not exist" };
			}
			//System.out.println(((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory[slot - 1] == null);
			if(((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory[slot - 1] == null) {
				return new Object[] { null, "slot is empty" };
			}
			ItemStack stack = ((GT_MetaTileEntity_BasicBatteryBuffer) tile.getMetaTileEntity()).mInventory[slot - 1];
			if(GT_ModHandler.isElectricItem(stack)) {
				return new Object[] { ((IElectricItem) stack.getItem()).getMaxCharge(stack) };
			} else {
				return new Object[] { null, "item in slot is not electric" };
			}
		}

	}

	@Override
	public Class<?> getTileEntityClass() {
		return BaseMetaTileEntity.class;
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		return (tileEntity != null) && tileEntity instanceof BaseMetaTileEntity
			&& ((BaseMetaTileEntity) tileEntity).getMetaTileEntity() instanceof GT_MetaTileEntity_BasicBatteryBuffer;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
		return new ManagedEnvironmentBatteryBuffer(((BaseMetaTileEntity) world.getTileEntity(x, y, z)), "gt_batterybuffer");
	}
}
