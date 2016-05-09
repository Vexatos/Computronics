package pl.asie.computronics.gui.providers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.gui.container.ContainerTapeReader;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.gui.container.ContainerBase;
import pl.asie.lib.gui.managed.LegacyGuiProvider;
import pl.asie.lib.tile.TileEntityBase;

/**
 * @author Vexatos
 */
public class GuiProviderTapeDrive extends LegacyGuiProvider {

	@Override
	@SideOnly(Side.CLIENT)
	protected GuiBase makeGuiBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new GuiTapePlayer(makeContainerBase(guiID, entityPlayer, world, pos, tile));
	}

	@Override
	protected ContainerBase makeContainerBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new ContainerTapeReader(tile, entityPlayer.inventory);
	}
}
