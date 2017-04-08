package pl.asie.computronics.integration.railcraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack) {
		Block oldBlock = world.getBlock(x, y, z);
		if(oldBlock == Blocks.snow_layer) {
			side = 1;
		} else if(oldBlock != Blocks.vine && oldBlock != Blocks.tallgrass && oldBlock != Blocks.deadbush && !oldBlock.isReplaceable(world, x, y, z)) {
			if(side == 0) {
				--y;
			}

			if(side == 1) {
				++y;
			}

			if(side == 2) {
				--z;
			}

			if(side == 3) {
				++z;
			}

			if(side == 4) {
				--x;
			}

			if(side == 5) {
				++x;
			}
		}

		return world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, null, stack) && (!SignalTypes.DigitalReceiver.needsSupport() || world.isSideSolid(x, y - 1, z, ForgeDirection.UP));
	}
}
