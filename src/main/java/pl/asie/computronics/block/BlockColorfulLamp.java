package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.tile.TileColorfulLamp;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockColorfulLamp extends BlockPeripheral {
	public IIcon m0, m1;
	
	public BlockColorfulLamp() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setBlockName("computronics.colorfulLamp");
	}
	
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileColorfulLamp();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		m0 = r.registerIcon("computronics:lamp_layer_0");
		m1 = r.registerIcon("computronics:lamp_layer_1");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getLightValue() { return 15; } 
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return LampRender.id();
	}
	
	private int renderingPass = 0;
	public void setRenderingPass(int i) { renderingPass = i&1; }
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return renderingPass == 1 ? m1 : m0;
	}
}
