package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock;
import pl.asie.computronics.oc.manual.IBlockWithDocumentation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.ColorUtils.Color;

@Optional.InterfaceList({
	@Optional.Interface(iface = "pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock", modid = Mods.OpenComputers)
})
public abstract class BlockPeripheral extends BlockBase implements IComputronicsEnvironmentBlock, IBlockWithDocumentation {

	public BlockPeripheral(String documentationName) {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.documentationName = documentationName;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int a, float _x, float _y, float _z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored()) {
			Color color = ColorUtils.getColor(player.getHeldItem());
			if(color != null) {
				((TileEntityPeripheralBase) tile).setColor(color.color);
				world.markBlockForUpdate(x, y, z);
				return true;
			}
		}
		return super.onBlockActivated(world, x, y, z, player, a, _x, _y, _z);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored()) {
			return ((TileEntityPeripheralBase) tile).getColor();
		}
		return getRenderColor(world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored()) {
			((TileEntityPeripheralBase) tile).setColor(ColorUtils.fromWoolMeta(colour).color);
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return super.recolourBlock(world, x, y, z, side, colour);
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
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	protected String documentationName;

	@Override
	public String getDocumentationName(World world, int x, int y, int z) {
		return this.documentationName;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return this.documentationName;
	}
}
