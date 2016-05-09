package pl.asie.computronics.oc.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithDocumentation {

	String getDocumentationName(World world, BlockPos pos);

	String getDocumentationName(ItemStack stack);
}
