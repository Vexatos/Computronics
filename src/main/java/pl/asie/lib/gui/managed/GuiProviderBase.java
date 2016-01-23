package pl.asie.lib.gui.managed;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public abstract class GuiProviderBase implements IGuiProvider {

	protected int guiID;

	@Override
	public void setGuiID(int guiID) {
		this.guiID = guiID;
	}

	@Override
	public int getGuiID() {
		return this.guiID;
	}

	@Override
	public boolean canOpen(World world, int x, int y, int z, EntityPlayer player, EnumFacing side) {
		return true;
	}
}
