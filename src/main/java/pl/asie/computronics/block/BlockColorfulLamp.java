package pl.asie.computronics.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.util.LampUtil;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = Mods.MFR)
})
public class BlockColorfulLamp extends BlockPeripheral implements IRedNetInputNode {
	public IIcon m0, m1;

	public BlockColorfulLamp() {
		super("colorful_lamp");
		this.setCreativeTab(Computronics.tab);
		this.setBlockName("computronics.colorfulLamp");
		this.lightValue = 15;
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
		if(Loader.isModLoaded(Mods.ProjectRed)) {
			((TileColorfulLamp) tile).onProjectRedBundledInputChanged();
		}
	}

	public void setLightValue(int value) {
		if(LampUtil.shouldColorLight()) {
			//Bit-shift all the things!
			int r = (((value & 0x7C00) >>> 10) / 2),
				g = (((value & 0x03E0) >>> 5) / 2),
				b = ((value & 0x001F) / 2);
			r = value > 0x7FFF ? 15 : r < 0 ? 0 : r > 15 ? 15 : r;
			g = value > 0x7FFF ? 15 : g < 0 ? 0 : g > 15 ? 15 : g;
			b = value > 0x7FFF ? 15 : b < 0 ? 0 : b > 15 ? 15 : b;
			int brightness = Math.max(Math.max(r , g), b);
			this.lightValue = brightness | ((b << 15) + (g << 10) + (r << 5));
		} else {
			this.lightValue = value;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return LampRender.id();
	}

	private int renderingPass = 0;

	public void setRenderingPass(int i) {
		renderingPass = i & 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return renderingPass == 1 ? m1 : m0;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public RedNetConnectionType getConnectionType(World world, int x, int y,
		int z, ForgeDirection side) {
		return RedNetConnectionType.CableSingle;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
		ForgeDirection side, int[] inputValues) {
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputChanged(World world, int x, int y, int z,
		ForgeDirection side, int inputValue) {
		((TileColorfulLamp) world.getTileEntity(x, y, z)).setLampColor(inputValue & 0x7FFF);
	}

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileColorfulLamp.class;
	}
}
