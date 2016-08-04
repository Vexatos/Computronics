package pl.asie.lib.gui.managed;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public interface IGuiProvider {

	@Nullable
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int ID, EntityPlayer player, World world, int x, int y, int z);

	@Nullable
	public Container makeContainer(int ID, EntityPlayer player, World world, int x, int y, int z);

	public boolean canOpen(World world, int x, int y, int z, EntityPlayer player, EnumFacing side);

	public void setGuiID(int guiID);

	public int getGuiID();
}
