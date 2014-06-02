package pl.asie.computronics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface ISortingOutputHandler {
	/**
	 * Check if this TileEntity can serve as an output.
	 */
	public boolean isOutputtable(TileEntity entity);
	
	/**
	 * Try to output an ItemStack.
	 * @param entity A TileEntity verified by isOutputtable.
	 * @param stack The ItemStack we want to output.
	 * @param simulate Should the output be simulated or actually happen?
	 * @return The amount of items output.
	 */
	public int output(TileEntity entity, ItemStack stack, boolean simulate);
}
