package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.block.BlockBase;

public class BlockTapeReader extends BlockMachineSidedIcon {
	private Icon mFront;
	
	public BlockTapeReader(int id) {
		super(id);
		this.setUnlocalizedName("computronics.tapeDrive");
		this.setGuiID(0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileTapeDrive();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		super.registerIcons(r);
		mFront = r.registerIcon("computronics:tape_drive_front");
	}
	
	@Override
	public boolean receivesRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
}
