package pl.asie.computronics.integration.charset;

import pl.asie.charset.api.wires.IBundledEmitter;
import pl.asie.charset.api.wires.IBundledReceiver;

/**
 * @author Vexatos
 */
public abstract class BundledTileEmitter<Q> implements IBundledEmitter, IBundledReceiver {

	protected final Q tile;

	protected BundledTileEmitter(Q tile) {
		this.tile = tile;
	}
}
