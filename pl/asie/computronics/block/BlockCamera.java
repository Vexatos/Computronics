package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.lib.block.BlockBase;

public class BlockCamera extends BlockMachineSidedIcon {
	private Icon mFront;
	
	public BlockCamera(int id) {
		super(id);
		this.setUnlocalizedName("computronics.camera");
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
}
