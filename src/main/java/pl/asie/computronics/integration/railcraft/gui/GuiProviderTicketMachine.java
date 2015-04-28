package pl.asie.computronics.integration.railcraft.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.lib.gui.managed.GuiProviderBase;

/**
 * @author Vexatos
 */
public class GuiProviderTicketMachine extends GuiProviderBase {

	@Override
	public boolean canOpen(World world, int x, int y, int z, EntityPlayer player, int side) {
		if(!super.canOpen(world, x, y, z, player, side)) {
			return false;
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			TileTicketMachine machine = ((TileTicketMachine) tile);
			boolean triesMaintenanceWork = player.isSneaking() && player.getCurrentEquippedItem() == null;
			return triesMaintenanceWork
				&& (!machine.isLocked()
				|| machine.isOwner(player.getGameProfile())
				|| PlayerPlugin.isOwnerOrOp(machine.getOwner(), player.getGameProfile()))
				|| !triesMaintenanceWork;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new GuiTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getCurrentEquippedItem() == null);
		}
		return null;
	}

	@Override
	public Container makeContainer(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new ContainerTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getCurrentEquippedItem() == null);
		}
		return null;
	}
}
