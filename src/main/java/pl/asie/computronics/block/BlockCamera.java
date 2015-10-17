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
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCamera;
import pl.asie.lib.block.TileEntityBase;

public class BlockCamera extends BlockMachineSidedIcon {
	private IIcon mFront;
	
	public BlockCamera() {
		super("camera");
		this.setBlockName("computronics.camera");
		this.setRotation(Rotation.SIX);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileCamera();
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
		mFront = r.registerIcon("computronics:camera_front");
	}
	
	@Override
	public boolean emitsRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(side);
		}
		return super.getComparatorInputOverride(world, x, y, z, side);
	}

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCamera.class;
	}
}
