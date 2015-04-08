package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Mods;

public class BlockSorter extends BlockMachineSidedIcon {
	private IIcon mFrontOn, mFrontOff;
	
	public BlockSorter() {
		super();
		this.setBlockName("computronics.sorter");
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		//((TileSorter)tile).updateOutput();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return null;
		//return new TileSorter();
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

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return null;
		//return TileSorter.class
	}
}
