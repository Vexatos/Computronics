package pl.asie.computronics.integration.tis3d.serial;

import li.cil.tis3d.api.serial.SerialInterface;
import li.cil.tis3d.api.serial.SerialInterfaceProvider;
import li.cil.tis3d.api.serial.SerialProtocolDocumentationReference;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Sangar, Vexatos
 */
public abstract class TileInterfaceProvider<T> implements SerialInterfaceProvider {

	protected final Class<T> tileClass;
	protected final String name, link;

	public TileInterfaceProvider(Class<T> tileClass, String name, String link) {
		this.tileClass = tileClass;
		this.name = name;
		this.link = link;
	}

	@Override
	public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
		final TileEntity tile = world.getTileEntity(pos);
		return tile != null && tileClass.isInstance(tile);
	}

	@Override
	public SerialProtocolDocumentationReference getDocumentationReference() {
		return new SerialProtocolDocumentationReference("tooltip.computronics.manual.tis3d.port." + name, "protocols/computronics/" + link);
	}

	protected abstract boolean isStillValid(World world, BlockPos pos, EnumFacing side, SerialInterface serialInterface, TileEntity tile);

	@Override
	public boolean isValid(World world, BlockPos pos, EnumFacing side, SerialInterface serialInterface) {
		final TileEntity tile = world.getTileEntity(pos);
		return tile != null && isStillValid(world, pos, side, serialInterface, tile);
	}

	@Override
	public final SerialInterface interfaceFor(World world, BlockPos pos, EnumFacing side) {
		final TileEntity tile = world.getTileEntity(pos);
		return tile != null && tileClass.isInstance(tile) ? interfaceFor(world, pos, side, tileClass.cast(tile)) : null;
	}

	protected abstract SerialInterface interfaceFor(World world, BlockPos pos, EnumFacing side, T tile);
}
