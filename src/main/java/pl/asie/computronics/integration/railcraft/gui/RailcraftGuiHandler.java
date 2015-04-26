package pl.asie.computronics.integration.railcraft.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class RailcraftGuiHandler implements IGuiHandler {

	public static void openGui(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		if(!world.isRemote) {
			player.openGui(Computronics.instance, guiID, world, x, y, z);
		}
	}

	public static void openGui(int guiID, EntityPlayer player, World world, Entity entity) {
		if(!world.isRemote) {
			player.openGui(Computronics.instance, guiID, world, entity.getEntityId(), -1, 0);
		}
	}

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new ContainerTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getCurrentEquippedItem() == null);
		}
		return null;
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			return new GuiTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getCurrentEquippedItem() == null);
		}
		return null;
	}
}
