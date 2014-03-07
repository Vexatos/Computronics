package pl.asie.computronics;

import pl.asie.computronics.gui.GuiOneSlot;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.lib.gui.GuiHandler;

public class ClientProxy extends CommonProxy {
	public boolean isClient() { return true; }
	
	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, GuiOneSlot.class);
	}
}
