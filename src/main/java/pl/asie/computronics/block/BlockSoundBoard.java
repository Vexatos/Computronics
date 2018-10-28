package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.tile.TileSoundBoard;

/**
 * @author Vexatos
 */
public class BlockSoundBoard extends BlockPeripheral implements IBlockWithPrefix {

	public BlockSoundBoard() {
		super("sound_board", Rotation.SIX);
		this.setTranslationKey("computronics.sound_board");
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	@Deprecated
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, meta, placer, hand);
		return state.withProperty(rotation.FACING, side.getOpposite());
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	// From ComputerCraft.
	private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[] {
		new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875), // Down
		new AxisAlignedBB(0.125, 0.8125, 0.125, 0.875, 1.0, 0.875), // Up
		new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.1875), // North
		new AxisAlignedBB(0.125, 0.125, 0.8125, 0.875, 0.875, 1.0), // South
		new AxisAlignedBB(0.0, 0.125, 0.125, 0.1875, 0.875, 0.875), // West
		new AxisAlignedBB(0.8125, 0.125, 0.125, 1.0, 0.875, 0.875), // East
	};

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		int direction = state.getValue(rotation.FACING).ordinal();
		return direction >= 0 && direction < BOXES.length ? BOXES[direction] : Block.FULL_BLOCK_AABB;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return state.isOpaqueCube();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileSoundBoard();
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSoundBoard.class;
	}

	private static final String prefix = "computercraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}
