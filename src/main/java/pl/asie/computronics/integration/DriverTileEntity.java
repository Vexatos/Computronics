package pl.asie.computronics.integration;

import li.cil.oc.api.driver.SidedBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Because {@link li.cil.oc.api.prefab.DriverTileEntity} uses a deprecated interface.
 * @author Sangar, Vexatos
 */
public abstract class DriverTileEntity implements SidedBlock {

	public abstract Class<?> getTileEntityClass();

	@Override
	public boolean worksWith(final World world, final int x, final int y, final int z, ForgeDirection side) {
		final Class<?> filter = getTileEntityClass();
		if(filter == null) {
			// This can happen if filter classes are deduced by reflection and
			// the class in question is not present.
			return false;
		}
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		return tileEntity != null && filter.isAssignableFrom(tileEntity.getClass());
	}
}
