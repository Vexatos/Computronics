package pl.asie.computronics.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.block.BlockAudioCable;
import pl.asie.computronics.tile.TileAudioCable;

public class AudioCableRender implements ISimpleBlockRenderingHandler {

	private static int renderId;

	public AudioCableRender() {
		renderId = RenderingRegistry.getNextAvailableRenderId();
	}

	private void renderInventoryIcon(Block block, IIcon icon, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
		RenderBlocks renderer) {
		renderer.setRenderBounds(0.3125, 0, 0.3125, 1 - 0.3125, 1, 1 - 0.3125);
		renderInventoryIcon(block, block.getIcon(0, 0), renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int connMask = 0;
		if(!(block instanceof BlockAudioCable)) {
			return false;
		}
		BlockAudioCable bac = (BlockAudioCable) block;
		TileAudioCable tac = (TileAudioCable) world.getTileEntity(x, y, z);
		if(tac != null) {
			for(int i = 0; i < 6; i++) {
				if(tac.connectsAudio(ForgeDirection.getOrientation(i))) {
					connMask |= (1 << i);
				}
			}
		}

		bac.setRenderMask(connMask ^ 0x3f);
		renderer.setRenderBounds(0.3125, 0.3125, 0.3125, 1 - 0.3125, 1 - 0.3125, 1 - 0.3125);
		renderer.renderStandardBlock(block, x, y, z);

		for(int i = 0; i < 6; i++) {
			if((connMask & (1 << i)) != 0) {
				bac.setRenderMask(0x3f ^ (1 << (i ^ 1)));
				switch(i) {
					case 0:
						renderer.setRenderBounds(0.3125, 0, 0.3125, 1 - 0.3125, 0.3125, 1 - 0.3125);
						break;
					case 1:
						renderer.setRenderBounds(0.3125, 1 - 0.3125, 0.3125, 1 - 0.3125, 1, 1 - 0.3125);
						break;
					case 2:
						renderer.setRenderBounds(0.3125, 0.3125, 0, 1 - 0.3125, 1 - 0.3125, 0.3125);
						break;
					case 3:
						renderer.setRenderBounds(0.3125, 0.3125, 1 - 0.3125, 1 - 0.3125, 1 - 0.3125, 1);
						break;
					case 4:
						renderer.setRenderBounds(0, 0.3125, 0.3125, 0.3125, 1 - 0.3125, 1 - 0.3125);
						break;
					case 5:
						renderer.setRenderBounds(1 - 0.3125, 0.3125, 0.3125, 1, 1 - 0.3125, 1 - 0.3125);
						break;
				}
				renderer.renderStandardBlock(block, x, y, z);
			}
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}

	public static int id() {
		return renderId;
	}
}
