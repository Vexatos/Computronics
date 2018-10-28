package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCipherBlock;
//import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
//import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = Mods.MFR)
})*/
public class BlockCipher extends BlockPeripheral /*implements IRedNetOmniNode*/ {

	public BlockCipher() {
		super("cipher", Rotation.FOUR);
		this.setTranslationKey("computronics.cipher");
		this.setGuiProvider(Computronics.guiCipher);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCipherBlock();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean isLocked = false;
		if(!world.isRemote && Config.CIPHER_CAN_LOCK) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null) {
				isLocked = ((TileCipherBlock) tile).isLocked();
			}
			if(isLocked) {
				player.sendStatusMessage(new TextComponentTranslation("chat.computronics.cipher.locked"), false);
			}
		}
		return isLocked || super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean supportsBundledRedstone() {
		return true;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		super.neighborChanged(state, world, pos, block, otherPos);
		/*TileEntity tile = world.getTileEntity(pos);
		if(Mods.isLoaded(Mods.ProjectRed))
			((TileCipherBlock)tile).onProjectRedBundledInputChanged();*/
	}

	/*@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
			EnumFacing side, int[] inputValues) {
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
	}*/

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCipherBlock.class;
	}
}
