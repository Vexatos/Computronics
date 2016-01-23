package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		this.setUnlocalizedName("computronics.camera");
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
	public boolean hasComparatorInputOverride() {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(null);
		}
		return super.getComparatorInputOverride(world, pos);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCamera.class;
	}
}
