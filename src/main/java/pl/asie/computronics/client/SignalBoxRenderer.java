package pl.asie.computronics.client;

import mods.railcraft.client.render.BlockRenderer;
import mods.railcraft.client.render.RenderSignalBox;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.SignalTypes;

/**
 * @author CovertJaguar, Vexatos
 */
public class SignalBoxRenderer extends BlockRenderer {

	public SignalBoxRenderer() {
		super(Computronics.railcraft.digitalBox);
		addCombinedRenderer(0, new RenderSignalBox(SignalTypes.Digital));
	}
}
