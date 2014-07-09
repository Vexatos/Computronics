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
import pl.asie.computronics.tile.TileEEPROMReader;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.block.BlockBase;

public class BlockEEPROMReader extends BlockPeripheral {
	private IIcon mTopOff, mTopOn, mSide, mBottom;
	
	public BlockEEPROMReader() {
		super();
		this.setBlockName("computronics.eepromReader");
		this.setGuiID(2);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEEPROMReader();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int sideNumber, int metadata) {
		switch(sideNumber) {
		case 0: return mBottom;
		case 1: return (metadata > 0 ? mTopOn : mTopOff);
		default: return mSide;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mTopOff = r.registerIcon("computronics:eepromreader_top_nochip");
		mTopOn = r.registerIcon("computronics:eepromreader_top_chip");
		mSide = r.registerIcon("computronics:machine_side");
		mBottom = r.registerIcon("computronics:machine_bottom");
	}
}
