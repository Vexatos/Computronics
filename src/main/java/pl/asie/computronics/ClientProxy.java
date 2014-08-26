package pl.asie.computronics;

import cpw.mods.fml.common.registry.EntityRegistry;
import pl.asie.computronics.gui.GuiCipherBlock;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.tile.ContainerCipherBlock;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.lib.gui.GuiHandler;

public class ClientProxy extends CommonProxy {
	public boolean isClient() { return true; }

	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, GuiTapePlayer.class);
		gui.registerGui(ContainerCipherBlock.class, GuiCipherBlock.class);
	}

	public void registerEntities(){
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "computronics.itemTape", 1, Computronics.instance, 64, 20, true);
	}
}
