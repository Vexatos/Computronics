package pl.asie.computronics.block;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileCamera;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCamera extends BlockMachineSidedIcon {
	private Icon mFront;
	
	public BlockCamera(int id) {
		super(id);
		this.setUnlocalizedName("computronics.camera");
		this.setRotation(Rotation.SIX);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileCamera();
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
		mFront = r.registerIcon("computronics:camera_front");
	}
	
	@Override
	public boolean emitsRedstone(IBlockAccess world, int x, int y, int z) {
		return Computronics.CAMERA_REDSTONE_REFRESH;
	}
}
