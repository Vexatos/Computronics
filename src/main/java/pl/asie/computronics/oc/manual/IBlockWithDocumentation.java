package pl.asie.computronics.oc.manual;

import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public interface IBlockWithDocumentation {
	public String getDocumentationName(World world, int x, int y, int z);
}
