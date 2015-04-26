package pl.asie.computronics;

import pl.asie.lib.network.Packet;

import java.io.IOException;

public class CommonProxy {

	public boolean isClient() {
		return false;
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
