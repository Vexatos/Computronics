package pl.asie.computronics.gui.providers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.gui.container.ContainerTapeReader;
import pl.asie.computronics.gui.handlers.TileTapeDriveHandler;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.gui.managed.GuiProviderBase;

/**
 * @author Vexatos
 */
public class GuiProviderTapeDrive extends GuiProviderBase {

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		if(y < 0 && world.getEntityByID(x) == null) {
			Computronics.log.warn("[Client] Entity not found when opening GUI: {0}", x);
			return null;
		}
		return new GuiTapePlayer(makeContainer(guiID, entityPlayer, world, x, y, z));
	}

	@Override
	public ContainerTapeReader makeContainer(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		if(y < 0) {
			Entity entity = world.getEntityByID(x);
			if(entity == null) {
				Computronics.log.warn("[Server] Entity not found when opening GUI: {0}", x);
				return null;
			} else {
				return null;
			}
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTapeDrive) {
			return new ContainerTapeReader((TileTapeDrive) tile, entityPlayer.inventory, new TileTapeDriveHandler((TileTapeDrive) tile));
		}
		return null;
	}
}
