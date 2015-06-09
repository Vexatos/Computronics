package pl.asie.computronics.integration.railcraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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

	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack) {
		return super.func_150936_a(world, x, y, z, side, player, stack)
			&& (!SignalTypes.Digital.needsSupport() || world.isSideSolid(x, y - 1, z, ForgeDirection.UP));
	}
}
