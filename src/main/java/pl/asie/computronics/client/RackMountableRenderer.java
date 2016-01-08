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
import pl.asie.computronics.oc.driver.DriverBoardLight;
import pl.asie.computronics.reference.Mods;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vexatos
 */
public class RackMountableRenderer {

	private final ResourceLocation
		lightBoardLights = new ResourceLocation("computronics", "textures/blocks/light_board_lights.png"),
		boomBoardActive = new ResourceLocation("computronics", "textures/blocks/boom_board_on.png"),
		boomBoardTicking = new ResourceLocation("computronics", "textures/blocks/boom_board_ticking.png");
	private IIcon
		lightBoard,
		boomBoard;

	private static final List<Integer> mountables = Arrays.asList(8, 9);

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
				for(int index = 1; index <= DriverBoardLight.Light.amount; index++) {
					boolean isActive = e.data.getBoolean("r_" + index);
					if(isActive) {
						int color = e.data.getInteger("c_" + index);
						if(color < 0) {
							return;
						}
						color = color & 0xFFFFFF;
						GL11.glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
						float u0 = ((index - 1) * 3) / 16f;
						float u1 = u0 + (3 / 16f);
						e.renderOverlay(lightBoardLights, u0, u1);
					}
				}
				GL11.glColor3f(1, 1, 1);
				disableLight();
				break;
			}
			case 9: {
				enableLight();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				if(e.data.getBoolean("r")) {
					e.renderOverlay(boomBoardActive);
				}
				if(e.data.getBoolean("t") && (e.rack.world().getTotalWorldTime() + e.rack.hashCode() + (e.mountable * 10)) % 20 < 10) {
					e.renderOverlay(boomBoardTicking);
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
				break;
			}
			case 9: {
				e.setFrontTextureOverride(boomBoard);
				break;
			}
		}
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void textureHook(TextureStitchEvent.Pre e) {
		if(e.map.getTextureType() == 0) {
			lightBoard = e.map.registerIcon("computronics:light_board");
			boomBoard = e.map.registerIcon("computronics:boom_board");
		}
	}
}
