package pl.asie.computronics;

import cpw.mods.fml.common.registry.EntityRegistry;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "computronics.itemTape", 1, Computronics.instance, 64, 20, true);
	}

	public void registerRenderers() {
		//NO-OP
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}
}
