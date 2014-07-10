package pl.asie.computronics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface ISortingOutputHandler {
	/**
	 * Check if this TileEntity is one we want to react to.
	 * Returns true if this is the valid OutputHandler for this TileEntity.
	 */
	public boolean isOutputtable(TileEntity entity);
	
	/**
	 * Try to output an ItemStack.
	 * @param entity A TileEntity (guaranteed to be only the ones which returned true on isOutputtable).
	 * @param stack The ItemStack we want to output.
	 * @param color The color (0-15) that the stack should be colored in.
	 * @param simulate Should the output be simulated or actually happen?
	 * @return The amount of items actually output.
	 */
	public int output(TileEntity entity, ItemStack stack, int color, boolean simulate);
}
