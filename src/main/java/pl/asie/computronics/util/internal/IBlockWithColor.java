package pl.asie.computronics.util.internal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Vexatos
 */
public interface IBlockWithColor {

	@SideOnly(Side.CLIENT)
	int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex);
}
