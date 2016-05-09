package pl.asie.lib.gui.managed;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class ManagedGuiHandler implements IGuiHandler {

	private final ArrayList<IGuiProvider> guiProviders = new ArrayList<IGuiProvider>();

	public int registerGuiProvider(IGuiProvider provider) {
		guiProviders.add(provider);
		int guiID = guiProviders.size() - 1;
		provider.setGuiID(guiID);
		return guiID;
	}

	public IGuiProvider getGuiProvider(int guiID) {
		if(guiID < 0 || guiID >= guiProviders.size()) {
			return null;
		}
		return guiProviders.get(guiID);
	}

	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		if(guiID < 0 || guiID >= guiProviders.size()) {
			return null;
		}
		return guiProviders.get(guiID).makeContainer(guiID, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		if(guiID < 0 || guiID >= guiProviders.size()) {
			return null;
		}
		return guiProviders.get(guiID).makeGui(guiID, player, world, x, y, z);
	}
}
