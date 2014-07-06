package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.tile.sorter.TileSorter;
import pl.asie.lib.block.BlockBase;

public class BlockSorter extends BlockMachineSidedIcon {
	private IIcon mFrontOn, mFrontOff;
	
	public BlockSorter() {
		super();
		this.setBlockName("computronics.sorter");
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		((TileSorter)tile).update();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileSorter();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		switch(sideNumber) {
			case 2:
				return (metadata >= 8 ? mFrontOn : mFrontOff);
			default:
				return super.getAbsoluteSideIcon(sideNumber, metadata);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mFrontOff = r.registerIcon("computronics:sorter_front_off");
		mFrontOn = r.registerIcon("computronics:sorter_front_on");
		mTop = r.registerIcon("computronics:sorter_top");
	}
}
