package pl.asie.computronics.gui.providers;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import pl.asie.computronics.gui.GuiCipherBlock;
import pl.asie.computronics.gui.container.ContainerCipherBlock;
import pl.asie.lib.gui.container.ContainerBase;
import pl.asie.lib.tile.TileEntityBase;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.gui.managed.LegacyGuiProvider;

/**
 * @author Vexatos
 */
public class GuiProviderCipher extends LegacyGuiProvider {
	@Override
	@SideOnly(Side.CLIENT)
	protected GuiBase makeGuiBase(int i, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new GuiCipherBlock(makeContainerBase(guiID, entityPlayer, world, pos, tile));
	}

	@Override
	protected ContainerBase makeContainerBase(int i, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new ContainerCipherBlock(tile, entityPlayer.inventory);
	}
}
