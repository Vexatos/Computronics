package pl.asie.computronics;

import pl.asie.computronics.gui.container.ContainerCipherBlock;
import pl.asie.computronics.gui.container.ContainerTapeReader;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class CommonProxy {
	public boolean isClient() {
		return false;
	}

	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, null);
		gui.registerGui(ContainerCipherBlock.class, null);
	}

	public void registerEntities() {
		//NO-OP
	}

	public void registerRenderers() {
		//NO-OP
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}
}
