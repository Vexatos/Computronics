package pl.asie.computronics.oc.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithDocumentation {

	public String getDocumentationName(World world, BlockPos pos);

	public String getDocumentationName(ItemStack stack);
}
