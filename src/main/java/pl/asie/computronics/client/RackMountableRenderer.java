package pl.asie.computronics.client;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.event.RackMountableRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.oc.DriverBoardLight;
import pl.asie.computronics.reference.Mods;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vexatos
 */
public class RackMountableRenderer {

	private final ResourceLocation
		lightBoardLights = new ResourceLocation("computronics", "textures/blocks/light_board_lights.png");
	private IIcon lightBoard;

	private static final List<Integer> mountables = Arrays.asList(8);

	@Optional.Method(modid = Mods.OpenComputers)
	private boolean isRackMountable(ItemStack stack) {
		return stack != null && stack.getItem() != null
			&& stack.getItem() instanceof ItemOpenComputers
			&& mountables.contains(stack.getItemDamage());
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRackMountableRender(RackMountableRenderEvent.TileEntity e) {
		ItemStack stack = e.rack.getStackInSlot(e.mountable);
		if(!isRackMountable(stack)) {
			return;
		}

		if(e.data == null) {
			return;
		}
		light = false;
		switch(stack.getItemDamage()) {
			case 8: {
				enableLight();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				for(DriverBoardLight.Light light : DriverBoardLight.Light.VALUES) {
					boolean isActive = e.data.getBoolean("r_" + light.index);
					if(isActive) {
						int color = e.data.getInteger("c_" + light.index);
						if(color < 0) {
							return;
						}
						color = color & 0xFFFFFF;
						GL11.glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
						float u0 = ((light.index - 1) * 3) / 16f;
						float u1 = u0 + (3 / 16f);
						e.renderOverlay(lightBoardLights, u0, u1);
					}
				}
				GL11.glColor3f(1, 1, 1);
				disableLight();
				break;
			}
		}
	}

	private boolean light = false;

	private void enableLight() {
		if(!light) {
			Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
			RenderHelper.disableStandardItemLighting();
			light = true;
		}
	}

	private void disableLight() {
		if(light) {
			Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
			RenderHelper.enableStandardItemLighting();
			light = false;
		}
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRackMountableRender(RackMountableRenderEvent.Block e) {
		ItemStack stack = e.rack.getStackInSlot(e.mountable);
		if(!isRackMountable(stack)) {
			return;
		}

		if(e.data == null) {
			return;
		}
		light = false;
		switch(stack.getItemDamage()) {
			case 8: {
				e.setFrontTextureOverride(lightBoard);
			}
		}
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void textureHook(TextureStitchEvent.Pre e) {
		if(e.map.getTextureType() == 0) {
			lightBoard = e.map.registerIcon("computronics:light_board");
		}
	}
}
