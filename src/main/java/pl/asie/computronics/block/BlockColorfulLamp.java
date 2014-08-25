package pl.asie.computronics.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.tile.TileColorfulLamp;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = "MineFactoryReloaded")
})
public class BlockColorfulLamp extends BlockPeripheral implements IRedNetInputNode {
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(Loader.isModLoaded("ProjRed|Core"))
			((TileColorfulLamp)tile).onProjectRedBundledInputChanged();
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

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) { return RedNetConnectionType.CableSingle; }

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) { }

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		((TileColorfulLamp)world.getTileEntity(x, y, z)).setLampColor(inputValue & 0x7FFF);
	}
}
