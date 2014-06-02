package pl.asie.computronics.tile.sorter;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import pl.asie.computronics.api.ISortingOutputHandler;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

public class SortingHandlerBC implements ISortingOutputHandler {
	@Override
	public boolean isOutputtable(TileEntity entity) {
		if(entity instanceof IPipeTile && ((IPipeTile)entity).getPipeType() == PipeType.ITEM)
			return true;
		return false;
	}

	@Override
	public int output(TileEntity entity, ItemStack stack, boolean simulate) {
		IPipeTile pipe = ((IPipeTile)entity);
		return pipe.injectItem(stack, !simulate, ForgeDirection.DOWN);
	}
}
