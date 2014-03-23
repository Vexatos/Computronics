package pl.asie.computronics.tile.sorter;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.ISortingOutputHandler;

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
