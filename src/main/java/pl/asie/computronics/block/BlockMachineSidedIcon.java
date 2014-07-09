package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.MiscUtils;

public abstract class BlockMachineSidedIcon extends BlockPeripheral {
	protected IIcon mSide, mTop, mBottom;
	
	public BlockMachineSidedIcon() {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setRotation(Rotation.FOUR);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int side, int metadata) {
		switch(side) {
			case 0: return mBottom;
			case 1: return mTop;
			default: return getAbsoluteSideIcon(side, metadata);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return mSide;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		mSide = r.registerIcon("computronics:machine_side");
		mTop = r.registerIcon("computronics:machine_top");
		mBottom = r.registerIcon("computronics:machine_bottom");
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return true;
	}
}
