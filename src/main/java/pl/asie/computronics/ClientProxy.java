package pl.asie.computronics;

import pl.asie.computronics.gui.GuiCipherBlock;
import pl.asie.computronics.gui.GuiOneSlot;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.tile.ContainerCipherBlock;
import pl.asie.computronics.tile.ContainerEEPROMReader;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.lib.gui.GuiHandler;

public class ClientProxy extends CommonProxy {
	public boolean isClient() { return true; }
	
	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, GuiTapePlayer.class);
		gui.registerGui(ContainerCipherBlock.class, GuiCipherBlock.class);
		gui.registerGui(ContainerEEPROMReader.class, GuiOneSlot.class);
	}
}
