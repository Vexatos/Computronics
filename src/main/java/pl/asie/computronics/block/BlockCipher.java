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
import pl.asie.computronics.tile.TileCipherBlock;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")
})
public class BlockCipher extends BlockMachineSidedIcon implements IRedNetOmniNode {
	private IIcon mFront;
	
	public BlockCipher() {
		super("bundled");
		this.setBlockName("computronics.cipher");
		this.setGuiID(1);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileCipherBlock();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(Loader.isModLoaded("ProjRed|Core"))
			((TileCipherBlock)tile).onProjectRedBundledInputChanged();
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
		mFront = r.registerIcon("computronics:cipher_front");
	}

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) {
		((TileCipherBlock)world.getTileEntity(x, y, z)).updateRedNet(inputValues);
		
	}

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		((TileCipherBlock)world.getTileEntity(x, y, z)).updateRedNet(inputValue);
	}

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		return RedNetConnectionType.PlateAll;
	}

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public int[] getOutputValues(World world, int x, int y, int z,
			ForgeDirection side) {
		return ((TileCipherBlock)world.getTileEntity(x, y, z)).redNetMultiOutput;
	}

	@Override
	@Optional.Method(modid = "MineFactoryReloaded")
	public int getOutputValue(World world, int x, int y, int z,
			ForgeDirection side, int subnet) {
		return ((TileCipherBlock)world.getTileEntity(x, y, z)).redNetSingleOutput;
	}
}
