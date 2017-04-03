package pl.asie.computronics.integration;

import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public abstract class DriverSpecificTileEntity<T> implements DriverBlock {

	protected final Class<T> tileClass;

	public DriverSpecificTileEntity(Class<T> tileClass) {
		this.tileClass = tileClass;
	}

	@Override
	public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
		final TileEntity tile = world.getTileEntity(pos);
		return tileClass.isInstance(tile);
	}

	@Nullable
	@Override
	public final ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
		final TileEntity tile = world.getTileEntity(pos);
		return tileClass.isInstance(tile) ? createEnvironment(world, pos, side, tileClass.cast(tile)) : null;
	}

	@Nullable
	protected abstract NamedManagedEnvironment<T> createEnvironment(World world, BlockPos pos, EnumFacing side, T tile);
}
