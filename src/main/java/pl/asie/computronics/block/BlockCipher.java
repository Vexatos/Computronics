package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCipherBlock;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = Mods.MFR)
})
public class BlockCipher extends BlockMachineSidedIcon implements IRedNetOmniNode {
	private IIcon mFront;
	
	public BlockCipher() {
		super("bundled", "cipher");
		this.setBlockName("computronics.cipher");
		this.setGuiProvider(Computronics.guiCipher);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileCipherBlock();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		boolean isLocked = false;
		if(!world.isRemote && Config.CIPHER_CAN_LOCK) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile != null) {
				isLocked = ((TileCipherBlock) tile).isLocked();
			}
			if(isLocked) {
				player.addChatMessage(new ChatComponentTranslation("chat.computronics.cipher.locked"));
			}
		}
		return isLocked || super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(Mods.isLoaded(Mods.ProjectRed))
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
	@Optional.Method(modid = Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) {
		((TileCipherBlock)world.getTileEntity(x, y, z)).updateRedNet(inputValues);
		
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		((TileCipherBlock)world.getTileEntity(x, y, z)).updateRedNet(inputValue);
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		return RedNetConnectionType.PlateAll;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public int[] getOutputValues(World world, int x, int y, int z,
			ForgeDirection side) {
		return ((TileCipherBlock)world.getTileEntity(x, y, z)).redNetMultiOutput;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public int getOutputValue(World world, int x, int y, int z,
			ForgeDirection side, int subnet) {
		return ((TileCipherBlock)world.getTileEntity(x, y, z)).redNetSingleOutput;
	}

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCipherBlock.class;
	}
}
