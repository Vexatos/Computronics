package pl.asie.computronics.oc.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithPrefix extends IBlockWithDocumentation {

	public String getPrefix(World world, BlockPos pos);

	public String getPrefix(ItemStack stack);
}
