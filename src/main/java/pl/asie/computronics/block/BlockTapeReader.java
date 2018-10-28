package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.computronics.tile.TileTapeDrive;

public class BlockTapeReader extends BlockPeripheral {

	public BlockTapeReader() {
		super("tape_drive", Rotation.FOUR);
		this.setTranslationKey("computronics.tapeDrive");
		this.setGuiProvider(Computronics.guiTapeDrive);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileTapeDrive();
	}

	@Override
	public boolean receivesRedstone(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileTapeDrive)) {
			return 0;
		}
		return ((TileTapeDrive) tile).getEnumState() == TapeDriveState.State.PLAYING ? 15 : 0;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileTapeDrive.class;
	}
}
