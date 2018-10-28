package pl.asie.computronics.integration.railcraft.driver.track;

import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.api.tracks.ITrackKitInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.NamedManagedEnvironment;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public abstract class DriverTrack<T extends ITrackKitInstance> implements DriverBlock {

	protected final Class<T> tileClass;

	public DriverTrack(Class<T> tileClass) {
		this.tileClass = tileClass;
	}

	@Override
	public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
		TileEntity tileEntity = world.getTileEntity(pos);
		return (tileEntity != null) && tileEntity instanceof IOutfittedTrackTile
			&& tileClass.isInstance(((IOutfittedTrackTile) tileEntity).getTrackKitInstance());
	}

	@Nullable
	@Override
	public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
		IOutfittedTrackTile tile = ((IOutfittedTrackTile) world.getTileEntity(pos));
		return tile != null ? createEnvironment(world, pos, side, tileClass.cast(tile.getTrackKitInstance())) : null;
	}

	@Nullable
	protected abstract NamedManagedEnvironment<T> createEnvironment(World world, BlockPos pos, EnumFacing side, T tile);
}
