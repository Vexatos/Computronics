package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.BuildCraftTransport;
import buildcraft.api.core.render.ITextureStates;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.utils.MatrixTranformations;
import buildcraft.transport.PipeIconProvider;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.pluggable.DroneStationPluggable.DroneStationState;

/**
 * @author Vexatos
 */
public class DroneStationRenderer implements IPipePluggableRenderer {
	private float zFightOffset = 1 / 4096.0F;

	private void robotStationPartRender(RenderBlocks renderblocks, DroneStationState state,
		ForgeDirection side, ITextureStates blockStateMachine, int x, int y, int z,
		float xStart, float xEnd, float yStart, float yEnd, float zStart,
		float zEnd) {

		float[][] zeroState = new float[3][2];
		// X START - END
		zeroState[0][0] = xStart + zFightOffset;
		zeroState[0][1] = xEnd - zFightOffset;
		// Y START - END
		zeroState[1][0] = yStart;
		zeroState[1][1] = yEnd;
		// Z START - END
		zeroState[2][0] = zStart + zFightOffset;
		zeroState[2][1] = zEnd - zFightOffset;

		switch (state) {
			case Available:
				blockStateMachine.getTextureState().set(BuildCraftTransport.instance.pipeIconProvider
					.getIcon(PipeIconProvider.TYPE.PipeRobotStation.ordinal()));
				break;
			case Used:
				blockStateMachine.getTextureState().set(BuildCraftTransport.instance.pipeIconProvider
					.getIcon(PipeIconProvider.TYPE.PipeRobotStationReserved.ordinal()));
				break;
		}

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
	}

	@Override
	public void renderPluggable(RenderBlocks renderblocks, IPipe pipe, ForgeDirection side, PipePluggable pipePluggable, ITextureStates blockStateMachine, int renderPass, int x, int y, int z) {
		if (renderPass != 0) {
			return;
		}

		DroneStationState state = ((DroneStationPluggable) pipePluggable).getState();

		robotStationPartRender (renderblocks, state, side, blockStateMachine, x, y, z,
			0.45F, 0.55F,
			0.0F, 0.224F,
			0.45F, 0.55F);

		float[][] zeroState = new float[3][2];


		// X START - END
		zeroState[0][0] = 0.25F + zFightOffset;
		zeroState[0][1] = 0.75F - zFightOffset;
		// Y START - END
		zeroState[1][0] = 0.225F;
		zeroState[1][1] = 0.251F;
		// Z START - END
		zeroState[2][0] = 0.25F + zFightOffset;
		zeroState[2][1] = 0.75F - zFightOffset;

		switch(state) {
			case Available:
				blockStateMachine.getTextureState().set(BuildCraftTransport.instance.pipeIconProvider
					.getIcon(PipeIconProvider.TYPE.PipeRobotStation.ordinal()));
				break;
			case Used:
				blockStateMachine.getTextureState().set(BuildCraftTransport.instance.pipeIconProvider
					.getIcon(PipeIconProvider.TYPE.PipeRobotStationReserved.ordinal()));
				break;
		}

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
	}
}
