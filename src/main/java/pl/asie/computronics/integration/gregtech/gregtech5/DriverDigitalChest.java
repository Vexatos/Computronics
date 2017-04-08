package pl.asie.computronics.integration.gregtech.gregtech5;

import gregtech.api.interfaces.tileentity.IDigitalChest;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverDigitalChest extends DriverSidedTileEntity {

	public static class ManagedEnvironmentDC extends ManagedEnvironmentOCTile<IDigitalChest> {

		public ManagedEnvironmentDC(IDigitalChest tile, String name) {
			super(tile, name);
		}

		@Callback(doc = "function():table; Returns a table of items stored in this block", direct = true)
		public Object[] getContents(Context c, Arguments a) {
			return new Object[] { tile.getStoredItemData() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IDigitalChest.class;
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		return tileEntity != null && tileEntity instanceof IDigitalChest
			&& ((IDigitalChest) tileEntity).isDigitalChest();
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
		return new ManagedEnvironmentDC((IDigitalChest) world.getTileEntity(x, y, z), "digital_chest");
	}
}
