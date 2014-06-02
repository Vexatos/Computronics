package pl.asie.computronics.block;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pl.asie.computronics.tile.sorter.TileSorter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSorter extends BlockMachineSidedIcon {
	private Icon mFrontOn, mFrontOff;
	
	public BlockSorter(int id) {
		super(id);
		this.setUnlocalizedName("computronics.sorter");
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int block) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		((TileSorter)tile).update();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileSorter();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getAbsoluteSideIcon(int sideNumber, int metadata) {
		switch(sideNumber) {
			case 2:
				return (metadata >= 8 ? mFrontOn : mFrontOff);
			default:
				return super.getAbsoluteSideIcon(sideNumber, metadata);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		super.registerIcons(r);
		mFrontOff = r.registerIcon("computronics:sorter_front_off");
		mFrontOn = r.registerIcon("computronics:sorter_front_on");
		mTop = r.registerIcon("computronics:sorter_top");
	}
}
