package pl.asie.computronics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock;
import pl.asie.computronics.oc.manual.IBlockWithDocumentation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.ColorUtils.Color;
import pl.asie.lib.util.internal.IColorable;

@Optional.InterfaceList({
	@Optional.Interface(iface = "pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock", modid = Mods.OpenComputers)
})
public abstract class BlockPeripheral extends BlockBase implements IComputronicsEnvironmentBlock, IBlockWithDocumentation {

	public BlockPeripheral(String documentationName, Rotation rotation) {
		super(Material.iron, Computronics.instance, rotation);
		this.setCreativeTab(Computronics.tab);
		this.documentationName = documentationName;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored() && player.getHeldItem() != null) {
			Color color = ColorUtils.getColor(player.getHeldItem());
			if(color != null) {
				((TileEntityPeripheralBase) tile).setColor(color.color);
				world.markBlockForUpdate(pos);
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			return ((IColorable) tile).getColor();
		}
		return getRenderColor(world.getBlockState(pos));
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			((IColorable) tile).setColor(ColorUtils.fromColor(color).color);
			world.markBlockForUpdate(pos);
			return true;
		}
		return super.recolorBlock(world, pos, side, color);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		super.onNeighborBlockChange(world, pos, state, block);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean isNormalCube() {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isBlockNormalCube() {
		return true;
	}

	@Override
	public boolean isFullCube() {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
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
