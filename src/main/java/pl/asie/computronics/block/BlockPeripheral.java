package pl.asie.computronics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock;
import pl.asie.computronics.oc.manual.IBlockWithDocumentation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.computronics.util.internal.IBlockWithColor;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.ColorUtils.Color;
import pl.asie.lib.util.internal.IColorable;

import static pl.asie.lib.util.WorldUtils.notifyBlockUpdate;

@Optional.InterfaceList({
	@Optional.Interface(iface = "pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock", modid = Mods.OpenComputers)
})
public abstract class BlockPeripheral extends BlockBase implements IComputronicsEnvironmentBlock, IBlockWithDocumentation, IBlockWithColor {

	public BlockPeripheral(String documentationName, Rotation rotation) {
		super(Material.IRON, Computronics.instance, rotation);
		this.setCreativeTab(Computronics.tab);
		this.documentationName = documentationName;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		ItemStack heldItem = player.getHeldItem(hand);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored() && !heldItem.isEmpty()) {
			Color color = ColorUtils.getColor(heldItem);
			if(color != null) {
				((TileEntityPeripheralBase) tile).setColor(color.color);
				notifyBlockUpdate(world, pos, state);
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	public int getRenderColor(IBlockState state) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int renderPass) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			return ((IColorable) tile).getColor();
		}
		return getRenderColor(state);
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			((IColorable) tile).setColor(ColorUtils.fromColor(color).color);
			notifyBlockUpdate(world, pos);
			return true;
		}
		return super.recolorBlock(world, pos, side, color);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		super.neighborChanged(state, world, pos, block, otherPos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isNormalCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isBlockNormalCube(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}

	protected String documentationName;

	@Override
	public String getDocumentationName(World world, BlockPos pos) {
		return this.documentationName;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return this.documentationName;
	}
}
