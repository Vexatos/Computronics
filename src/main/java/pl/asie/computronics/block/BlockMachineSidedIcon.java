package pl.asie.computronics.block;

import cpw.mods.fml.common.Loader;
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
	protected IIcon mSide, mSideBI, mSideBO, mTop, mBottom;
	private String sidingType;
	
	public BlockMachineSidedIcon(String sidingType) {
		super();
		this.sidingType = sidingType;
		if(sidingType.equals("bundled") && !Loader.isModLoaded("RedLogic") && !Loader.isModLoaded("ProjRed|Core"))
			this.sidingType = "";
		this.setRotation(Rotation.FOUR);
	}

	public BlockMachineSidedIcon() {
		this("");
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
		if(this.sidingType.equals("bundled")) {
			if(sideNumber == 4) return mSideBI;
			else if(sideNumber == 5) return mSideBO;
			else return mSide;
		} else return mSide;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		mSide = r.registerIcon("computronics:machine_side");
		mTop = r.registerIcon("computronics:machine_top");
		mBottom = r.registerIcon("computronics:machine_bottom");
		if(this.sidingType.equals("bundled")) {
			mSideBI = r.registerIcon("computronics:machine_side_bundled_input");
			mSideBO = r.registerIcon("computronics:machine_side_bundled_output");
		}
	}
}
