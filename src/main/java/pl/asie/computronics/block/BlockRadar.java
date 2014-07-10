package pl.asie.computronics.block;

import java.util.logging.Level;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileRadar;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockRadar extends BlockMachineSidedIcon {
	public BlockRadar() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:radar");
		this.setBlockName("computronics.radar");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileRadar();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mSide = r.registerIcon("computronics:radar_side");
	}
}
