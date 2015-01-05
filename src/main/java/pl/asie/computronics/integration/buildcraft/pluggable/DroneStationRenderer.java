package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.core.render.ITextureStates;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.utils.MatrixTranformations;
import buildcraft.transport.render.FakeBlock;
import buildcraft.transport.render.TextureStateManager;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.pluggable.IntegrationBuildCraft.Textures;

/**
 * @author Vexatos
 */
public class DroneStationRenderer implements IPipePluggableRenderer {

	private float zFightOffset = 1 / 4096.0F;

	private void droneStationPartRender(RenderBlocks renderblocks,
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

		/*IIcon[] icons = ((TextureStateManager) blockStateMachine.getTextureState()).popArray();
		icons[0] = Textures.DRONE_STATION_NOOK_TOP.getIcon();
		icons[1] = Textures.DRONE_STATION_NOOK_TOP.getIcon();
		for(int i = 2; i < icons.length; i++) {
			icons[i] = Textures.DRONE_STATION_NOOK_SIDE.getIcon();
		}
		((TextureStateManager) blockStateMachine.getTextureState()).popArray();*/

		blockStateMachine.getTextureState().set(Textures.DRONE_STATION_TOP.getIcon());

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
		//((TextureStateManager) blockStateMachine.getTextureState()).pushArray();
	}

	@Override
	public void renderPluggable(RenderBlocks renderblocks, IPipe pipe, ForgeDirection side, PipePluggable pipePluggable, ITextureStates blockStateMachine, int renderPass, int x, int y, int z) {
		if(renderPass != 0) {
			return;
		}

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.60F, 0.70F,
			0.0F, 0.224F,
			0.30F, 0.40F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.60F, 0.70F,
			0.0F, 0.224F,
			0.60F, 0.70F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.30F, 0.40F,
			0.0F, 0.224F,
			0.30F, 0.40F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.30F, 0.40F,
			0.0F, 0.224F,
			0.60F, 0.70F);

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
/*
		IIcon[] icons = ((TextureStateManager) blockStateMachine.getTextureState()).popArray();
		icons[0] = Textures.DRONE_STATION_BOTTOM.getIcon();
		icons[1] = Textures.DRONE_STATION_TOP.getIcon();
		for(int i = 2; i < icons.length; i++) {
			icons[i] = Textures.DRONE_STATION_SIDE.getIcon();
		}
		((TextureStateManager) blockStateMachine.getTextureState()).popArray();
*/
		blockStateMachine.getTextureState().set(Textures.DRONE_STATION_TOP.getIcon());

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);
		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);

		// X START - END
		zeroState[0][0] = 0.25F + 0.125F / 2 + zFightOffset;
		zeroState[0][1] = 0.75F - 0.125F / 2 + zFightOffset;
		// Y START - END
		zeroState[1][0] = 0.25F;
		zeroState[1][1] = 0.25F + 0.125F;
		// Z START - END
		zeroState[2][0] = 0.25F + 0.125F / 2;
		zeroState[2][1] = 0.75F - 0.125F / 2;

		//((TextureStateManager) blockStateMachine.getTextureState()).pushArray();

		blockStateMachine.getTextureState().set(Textures.DRONE_STATION_BOTTOM.getIcon());
		rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
	}
}
