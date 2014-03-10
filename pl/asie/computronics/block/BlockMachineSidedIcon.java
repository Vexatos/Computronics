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
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.MiscUtils;

public abstract class BlockMachineSidedIcon extends BlockBase {
	private Icon mSide, mTop, mBottom;
	
	public BlockMachineSidedIcon(int id) {
		super(id, Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setRotateFrontSide(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess w, int x, int y, int z, int side) {
		return getIconInternal(side, w.getBlockMetadata(x, y, z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		return getIconInternal(side, 2);
	}
	
	@SideOnly(Side.CLIENT)
	private Icon getIconInternal(int side, int metadata) {
		switch(side) {
			case 0: return mBottom;
			case 1: return mTop;
			default: return getAbsoluteSideIcon(MiscUtils.getAbsoluteSide(side, ((metadata & 3) + 2)), metadata);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return mSide;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		mSide = r.registerIcon("computronics:machine_side");
		mTop = r.registerIcon("computronics:machine_top");
		mBottom = r.registerIcon("computronics:machine_bottom");
	}
}
