package pl.asie.computronics.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.tile.TileColorfulLamp;

public class LampRender implements ISimpleBlockRenderingHandler {

	private static int renderId;

	public LampRender() {
		renderId = RenderingRegistry.getNextAvailableRenderId();
	}

	private void renderInventoryIcon(BlockColorfulLamp block, IIcon icon, RenderBlocks renderer) {
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
		if(!(block instanceof BlockColorfulLamp)) {
			return;
		}
		BlockColorfulLamp lb = (BlockColorfulLamp) block;
		GL11.glPushMatrix();
		GL11.glColor3f(0.75f, 0.75f, 0.75f);
		GL11.glScalef(0.975f, 0.975f, 0.975f);
		renderInventoryIcon(lb, lb.m0, renderer);
		GL11.glPopMatrix();
		renderInventoryIcon(lb, lb.m1, renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
		Block block, int modelId, RenderBlocks renderer) {
		if(!(block instanceof BlockColorfulLamp)) {
			return false;
		}
		BlockColorfulLamp lb = (BlockColorfulLamp) block;
		GL11.glPushMatrix();
		//Tessellator t = Tessellator.instance;

		// calculate colors
		TileEntity tile = world.getTileEntity(x, y, z);
		int color = 0x6318;
		if(tile instanceof TileColorfulLamp) {
			color = ((TileColorfulLamp) tile).getLampColor();
		}
		float b = 0.2f + (((color & 31) / 31.0f) * 0.8f);
		float g = 0.2f + ((((color >> 5) & 31) / 31.0f) * 0.8f);
		float r = 0.2f + ((((color >> 10) & 31) / 31.0f) * 0.8f);

		// HACK! HACK! HACK! HACK! HACK!
		lb.setRenderingPass(0);
		renderer.renderStandardBlockWithColorMultiplier(lb, x, y, z, r, g, b);
		GL11.glPopMatrix();
		lb.setRenderingPass(1);
		renderer.renderStandardBlock(lb, x, y, z);
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
