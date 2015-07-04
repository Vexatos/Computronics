package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.computronics.tile.TileTapeDrive;

public class BlockTapeReader extends BlockMachineSidedIcon {

	private IIcon mFront;

	public BlockTapeReader() {
		super("tape_drive");
		this.setBlockName("computronics.tapeDrive");
		this.setGuiProvider(Computronics.guiTapeDrive);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileTapeDrive();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mFront = r.registerIcon("computronics:tape_drive_front");
	}

	@Override
	public boolean receivesRedstone(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
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
