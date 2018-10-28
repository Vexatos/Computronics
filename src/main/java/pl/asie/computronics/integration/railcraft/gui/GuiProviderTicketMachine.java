package pl.asie.computronics.integration.railcraft.gui;

import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.lib.gui.managed.GuiProviderBase;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class GuiProviderTicketMachine extends GuiProviderBase {

	@Override
	public boolean canOpen(World world, int x, int y, int z, EntityPlayer player, EnumFacing side) {
		if(!super.canOpen(world, x, y, z, player, side)) {
			return false;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			TileTicketMachine machine = ((TileTicketMachine) tile);
			boolean triesMaintenanceWork = player.isSneaking() && player.getHeldItemMainhand().isEmpty();
			return !triesMaintenanceWork || (!machine.isLocked()
				|| machine.isOwner(player.getGameProfile())
				|| PlayerPlugin.isOwnerOrOp(machine.getOwner(), player.getGameProfile()));
		}
		return false;
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			return new GuiTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getHeldItemMainhand().isEmpty());
		}
		return null;
	}

	@Nullable
	@Override
	public Container makeContainer(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			return new ContainerTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getHeldItemMainhand().isEmpty());
		}
		return null;
	}
}
