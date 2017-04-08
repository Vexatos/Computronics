package pl.asie.computronics.integration.railcraft.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import mods.railcraft.client.render.BlockRenderer;
import mods.railcraft.client.render.RenderSignalBox;
import net.minecraft.block.Block;
import pl.asie.computronics.integration.railcraft.SignalTypes;

/**
 * @author CovertJaguar, Vexatos
 */
public class SignalBoxRenderer extends BlockRenderer {

	public SignalBoxRenderer(Block block, SignalTypes type) {
		super(block);
		RenderSignalBox renderer = new RenderSignalBox(type);
		addCombinedRenderer(0, renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(type.getTileClass(), renderer);
	}
}

