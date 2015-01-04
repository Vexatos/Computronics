package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.core.render.ITextureStates;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Vexatos
 */
public class DroneStationRenderer implements IPipePluggableRenderer {
	@Override
	public void renderPluggable(RenderBlocks renderblocks, IPipe pipe, ForgeDirection side, PipePluggable pipePluggable, ITextureStates blockStateMachine, int renderPass, int x, int y, int z) {

	}
}
