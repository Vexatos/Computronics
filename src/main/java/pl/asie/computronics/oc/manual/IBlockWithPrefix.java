package pl.asie.computronics.oc.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithPrefix extends IBlockWithDocumentation {

	String getPrefix(World world, BlockPos pos);

	String getPrefix(ItemStack stack);
}
