package pl.asie.computronics.oc.client;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.event.RackMountableRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.oc.driver.DriverBoardLight.Mode;
import pl.asie.computronics.reference.Mods;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vexatos
 */
public class RackMountableRenderer {

	private final ResourceLocation
		boomBoardActive = new ResourceLocation("computronics", "textures/blocks/boom_board_on.png"),
		boomBoardTicking = new ResourceLocation("computronics", "textures/blocks/boom_board_ticking.png"),
		switchBoardActive = new ResourceLocation("computronics", "textures/blocks/switch_board_on.png");
	private IIcon
		boomBoard,
		rackCapacitor,
		switchBoard;

	private static final List<Integer> mountables = Arrays.asList(10, 11, 12, 13);

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

		switch(stack.getItemDamage()) {
			case 10: {
				if(e.data == null) {
					return;
				}
				enableLight();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Mode mode = Mode.fromIndex(e.data.getInteger("m"));
				if(mode == null) {
					mode = Mode.Default;
				}
				for(int index = 1; index <= mode.lightcount; index++) {
					if(e.data.getBoolean("r_" + index)) {
						int color = e.data.getInteger("c_" + index);
						if(color < 0) {
							continue;
						}
						color = color & 0xFFFFFF;
						GL11.glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
						//float u0 = ((index - 1) * 3) / 16f;
						//float u0 = ((index - 1) * 4) / 16f;
						//float u1 = u0 + (3 / 16f);
						//float u1 = u0 + (4 / 16f);
						float u0 = mode.getU0(index);
						float v0 = mode.getV0(index, e.v0, e.v1);
						renderOverlay(mode.foreground, u0, mode.getU1(index, u0), v0, mode.getV1(index, v0, e.v1));
					}
				}

				GL11.glColor3f(1, 1, 1);
				GL11.glDisable(GL11.GL_BLEND);
				disableLight();
				break;
			}
			case 11: {
				if(e.data == null) {
					return;
				}
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
				GL11.glDisable(GL11.GL_BLEND);
				disableLight();
				break;
			}
			case 13: {
				if(e.data == null) {
					return;
				}
				enableLight();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				final byte switchData = e.data.getByte("s");
				for(int i = 0; i < 4; i++) {
					if(((switchData >> i) & 1) == 1) {
						float u0 = (i * 4) / 16f;
						float u1 = u0 + (4 / 16f);
						e.renderOverlay(switchBoardActive, u0, u1);
					}
				}

				GL11.glColor3f(1, 1, 1);
				GL11.glDisable(GL11.GL_BLEND);
				disableLight();
				break;
			}
		}
	}

	private void renderOverlay(ResourceLocation texture, final float u0, final float u1, final float v0, final float v1) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		final Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(u0, v1, 0, u0, v1);
		t.addVertexWithUV(u1, v1, 0, u1, v1);
		t.addVertexWithUV(u1, v0, 0, u1, v0);
		t.addVertexWithUV(u0, v0, 0, u0, v0);
		t.draw();
	}

	private void enableLight() {
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
		RenderHelper.disableStandardItemLighting();
	}

	private void disableLight() {
		Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
		RenderHelper.enableStandardItemLighting();
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRackMountableRender(RackMountableRenderEvent.Block e) {
		ItemStack stack = e.rack.getStackInSlot(e.mountable);
		if(!isRackMountable(stack)) {
			return;
		}

		switch(stack.getItemDamage()) {
			case 10: {
				if(e.data == null) {
					return;
				}
				Mode mode = Mode.fromIndex(e.data.getInteger("m"));
				if(mode == null) {
					mode = Mode.Default;
				}
				e.setFrontTextureOverride(mode.background);
				break;
			}
			case 11: {
				e.setFrontTextureOverride(boomBoard);
				break;
			}
			case 12: {
				e.setFrontTextureOverride(rackCapacitor);
				break;
			}
			case 13: {
				e.setFrontTextureOverride(switchBoard);
				break;
			}
		}
	}

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void textureHook(TextureStitchEvent.Pre e) {
		if(e.map.getTextureType() == 0) {
			for(Mode mode : Mode.VALUES) {
				mode.registerIcons(e.map);
			}
			boomBoard = e.map.registerIcon("computronics:boom_board");
			rackCapacitor = e.map.registerIcon("computronics:rack_capacitor");
			switchBoard = e.map.registerIcon("computronics:switch_board");
		}
	}
}
