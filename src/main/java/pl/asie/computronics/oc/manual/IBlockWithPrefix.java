package pl.asie.computronics.oc.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithPrefix extends IBlockWithDocumentation {

	public String getPrefix(World world, int x, int y, int z);

	public String getPrefix(ItemStack stack);
}
