package pl.asie.computronics.integration.railcraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.tile.TileTicketMachine;
import pl.asie.lib.gui.managed.GuiProviderBase;

/**
 * @author Vexatos
 */
public class GuiProviderTicketMachine extends GuiProviderBase {
	@Override
	public GuiContainer makeGui(int i, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new GuiTicketMachine(entityPlayer.inventory, (TileTicketMachine) tile, entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem() == null);
		}
		return null;
	}

	@Override
	public Container makeContainer(int i, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new ContainerTicketMachine(entityPlayer.inventory, (TileTicketMachine) tile, entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem() == null);
		}
		return null;
	}
}
