package pl.asie.computronics.integration.railcraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.railcraft.SignalTypes;

/**
 * @author Vexatos
 */
public class ItemBlockSignalBox extends ItemBlock {

	public ItemBlockSignalBox(Block block) {
		super(block);
	}

	// Taken from Railcraft code
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		Block block = world.getBlockState(pos).getBlock();
		if(block == Blocks.SNOW_LAYER && block.isReplaceable(world, pos)) {
			side = EnumFacing.UP;
		} else if(!block.isReplaceable(world, pos)) {
			pos = pos.offset(side);
		}

		return world.canBlockBePlaced(this.getBlock(), pos, false, side, (Entity) null, stack) && (!SignalTypes.DigitalReceiver.needsSupport() || world.isSideSolid(pos.down(), EnumFacing.UP));
	}
}
