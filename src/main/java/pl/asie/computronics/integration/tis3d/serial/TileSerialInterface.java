package pl.asie.computronics.integration.tis3d.serial;

import li.cil.tis3d.api.serial.SerialInterface;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Vexatos
 */
public abstract class TileSerialInterface<T extends TileEntity> implements SerialInterface {

	protected final T tile;

	public TileSerialInterface(final T tile) {
		this.tile = tile;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public void skip() {

	}

	@Override
	public void reset() {
	}

	public boolean isTileEqual(TileEntity tile) {
		return tile != null && this.tile != null && this.tile.equals(tile);
	}
}
