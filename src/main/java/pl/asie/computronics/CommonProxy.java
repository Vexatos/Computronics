package pl.asie.computronics;

import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.lib.gui.GuiHandler;

public class CommonProxy {
	public boolean isClient() { return false; }

	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, null);
	}
}
