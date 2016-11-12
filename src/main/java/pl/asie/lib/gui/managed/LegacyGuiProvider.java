package pl.asie.lib.gui.managed;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.gui.container.ContainerBase;
import pl.asie.lib.tile.TileEntityBase;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public abstract class LegacyGuiProvider extends GuiProviderBase {

	@SideOnly(Side.CLIENT)
	protected abstract GuiBase makeGuiBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile);

	protected abstract ContainerBase makeContainerBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile);

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		final BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return makeGuiBase(guiID, entityPlayer, world, pos, (TileEntityBase) tile);
		}
		return null;
	}

	@Nullable
	@Override
	public Container makeContainer(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		final BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return makeContainerBase(guiID, entityPlayer, world, pos, (TileEntityBase) tile);
		}
		return null;
	}
}
