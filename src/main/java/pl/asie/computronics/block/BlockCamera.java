package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.lib.tile.TileEntityBase;

public class BlockCamera extends BlockPeripheral {

	public BlockCamera() {
		super("camera", Rotation.SIX);
		this.setTranslationKey("computronics.camera");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCamera();
	}

	@Override
	public boolean emitsRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(IBlockState state) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(null);
		}
		return super.getComparatorInputOverride(state, world, pos);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCamera.class;
	}
}
