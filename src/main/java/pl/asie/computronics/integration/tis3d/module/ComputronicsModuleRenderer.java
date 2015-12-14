package pl.asie.computronics.integration.tis3d.module;

import li.cil.tis3d.api.prefab.client.SimpleModuleRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Vexatos
 */
public class ComputronicsModuleRenderer extends SimpleModuleRenderer {

	@Override
	protected void renderOverlay(ItemRenderType type, ItemStack stack, Object... data) {
		if(stack == null || stack.getItem() == null) {
			super.renderOverlay(type, stack, data);
			return;
		}
		if(stack.getItemDamage() == 0) { // Colorful Module
			int color = getColorForRendering(stack, 0);
			GL11.glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
		}
		super.renderOverlay(type, stack, data);
		/*int passes = stack.getItem().getRenderPasses(stack.getItemDamage());
		for(int i = 0; i < passes; i++) {
			IIcon icon = stack.getItem().getIconFromDamageForRenderPass(stack.getItemDamage(), i);
			if(icon != null) {
				RenderUtil.bindTexture(this.getTextureLocation(type, stack, data));
				RenderUtil.drawQuad(icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
			}
		}*/
	}

	private int getColorForRendering(ItemStack stack, int pass) {
		switch(stack.getItemDamage()) {
			case 0: {
				if(pass == 0) {
					if(!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					NBTTagCompound tag = stack.getTagCompound();
					if(tag.hasKey("computronics:color")) {
						int col = tag.getInteger("computronics:color");
						if(col >= 0) {
							return col;
						}
					}
					return Color.HSBtoRGB((((System.currentTimeMillis() + (stack.hashCode() % 30000)) % 30000) / 30000F), 1F, 1F) & 0xFFFFFF;
				}
			}
			default: {
				return stack.getItem().getColorFromItemStack(stack, pass);
			}
		}
	}
}
