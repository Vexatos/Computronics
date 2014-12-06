package pl.asie.computronics;

import pl.asie.computronics.tile.ContainerCipherBlock;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.lib.gui.inventory.GuiInventoryHandler;

public class CommonProxy {
	public boolean isClient() {
		return false;
	}

	public void registerGuis(GuiInventoryHandler gui) {
		gui.registerGui(ContainerTapeReader.class, null);
		gui.registerGui(ContainerCipherBlock.class, null);
	}

	public void registerEntities() {
		//NO-OP
	}

	public void registerRenderers() {
		//NO-OP
	}
}
