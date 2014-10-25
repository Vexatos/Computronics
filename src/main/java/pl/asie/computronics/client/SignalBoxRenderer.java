package pl.asie.computronics.client;

import mods.railcraft.client.render.BlockRenderer;
import pl.asie.computronics.Computronics;

/**
 * @author CovertJaguar, Vexatos
 */
public class SignalBoxRenderer extends BlockRenderer {

	public SignalBoxRenderer() {
		super(Computronics.signalBox);
		addCombinedRenderer(0, SignalBoxCombinedRenderer.INSTANCE);
	}
}
