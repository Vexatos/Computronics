package pl.asie.computronics.gui.providers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import pl.asie.computronics.gui.GuiCipherBlock;
import pl.asie.computronics.gui.container.ContainerCipherBlock;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.gui.managed.LegacyGuiProvider;

/**
 * @author Vexatos
 */
public class GuiProviderCipher extends LegacyGuiProvider {
	@Override
	@SideOnly(Side.CLIENT)
	protected GuiBase makeGuiBase(int i, EntityPlayer entityPlayer, World world, int x, int y, int z, TileEntityBase tile) {
		return new GuiCipherBlock(makeContainerBase(guiID, entityPlayer, world, x, y, z, tile));
	}

	@Override
	protected ContainerBase makeContainerBase(int i, EntityPlayer entityPlayer, World world, int x, int y, int z, TileEntityBase tile) {
		return new ContainerCipherBlock(tile, entityPlayer.inventory);
	}
}
