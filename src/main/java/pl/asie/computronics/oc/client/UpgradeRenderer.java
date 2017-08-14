package pl.asie.computronics.oc.client;

import li.cil.oc.api.driver.item.UpgradeRenderer.MountPointName;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.event.RobotRenderEvent.MountPoint;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.client.renderer.PetRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.oc.client.model.ModelRadar;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.internal.Triple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Mostly stolen from Sangar.
 * Stolen with permission.
 * @author Sangar, Vexatos
 */
@SideOnly(Side.CLIENT)
public class UpgradeRenderer {

	private final ResourceLocation
		upgradeRadar = new ResourceLocation("computronics", "textures/models/upgrade_radar.png"),
		modelRadar = new ResourceLocation("computronics", "textures/models/model_radar.png"),
		upgradeChatBox = new ResourceLocation("computronics", "textures/models/upgrade_chat_box.png"),
		beepCard = new ResourceLocation("computronics", "textures/models/card_beep.png");

	AxisAlignedBB bounds = new AxisAlignedBB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);

	private static final List<Integer> upgrades = Arrays.asList(1, 2, 5, 8);

	@Optional.Method(modid = Mods.OpenComputers)
	private boolean isUpgrade(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() != null
			&& stack.getItem() instanceof ItemOpenComputers
			&& upgrades.contains(stack.getItemDamage());
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public String computePreferredMountPoint(ItemStack stack, Robot robot, Set<String> availableMountPoints) {
		switch(stack.getItemDamage()) {
			case 1: {
				return MountPointName.Any;
			}
			case 2: {
				return availableMountPoints.contains(MountPointName.TopRight) ? MountPointName.TopRight
					: availableMountPoints.contains(MountPointName.TopLeft) ? MountPointName.TopLeft
					: MountPointName.Any;
			}
			case 5:
			case 8: {
				return availableMountPoints.contains(MountPointName.BottomFront) ? MountPointName.BottomFront
					: availableMountPoints.contains(MountPointName.BottomBack) ? MountPointName.BottomBack
					: MountPointName.Any;
			}
			default: {
				return MountPointName.None;
			}
		}
	}

	private ModelRadar radarModel = new ModelRadar();

	@Optional.Method(modid = Mods.OpenComputers)
	public void render(ItemStack stack, MountPoint mountPoint, Robot robot, float pt) {
		if(!isUpgrade(stack)) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager tm = mc.getTextureManager();

		switch(stack.getItemDamage()) {
			case 1: {
				tm.bindTexture(upgradeChatBox);
				drawSimpleBlock(mountPoint, 0);
				break;
			}
			case 2: {
				if(mountPoint.name.equals(MountPointName.TopLeft) || mountPoint.name.equals(MountPointName.TopRight)) {
					float degrees = robot.shouldAnimate() ?
						((robot.world().getTotalWorldTime() + (robot.hashCode() ^ 0xFF)) % 160 + pt) / 160F * 360F : 0F;
					if(mountPoint.name.equals(MountPointName.TopRight)) {
						degrees = 360 - degrees;
					}
					GlStateManager.pushAttrib();
					tm.bindTexture(modelRadar);
					GlStateManager.disableCull();
					GlStateManager.rotate(180, 1, 0, 0);
					GlStateManager.rotate(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
					GlStateManager.translate(0F, -0.8F, 0F);
					GlStateManager.translate(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());
					GlStateManager.scale(0.3f, 0.3f, 0.3f);
					GlStateManager.pushMatrix();
					radarModel.render(Math.max(degrees, 0));
					GlStateManager.popMatrix();
					GlStateManager.popAttrib();
				} else {
					tm.bindTexture(upgradeRadar);
					drawSimpleBlock(mountPoint, 0);
				}
				break;
			}
			case 5:
			case 8: {
				tm.bindTexture(beepCard);
				drawSimpleBlock(mountPoint, 0);
				break;
			}
		}
	}

	/*@Optional.Method(modid = Mods.OpenComputers)
	private void drawSimpleBlock(MountPoint mountPoint) {
		drawSimpleBlock(mountPoint, 0);
	}*/

	/*@Optional.Method(modid = Mods.OpenComputers)
	private void drawSimpleBlock(MountPoint mountPoint, float frontOffset) {
		drawSimpleBlock(mountPoint, frontOffset, false);
	}*/

	//Mostly stolen from Sangar, like most of the things in this class.
	//Stolen with permission.
	@Optional.Method(modid = Mods.OpenComputers)
	private void drawSimpleBlock(MountPoint mountPoint, float frontOffset) {
		GlStateManager.rotate(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
		GlStateManager.translate(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());

		Tessellator t = Tessellator.getInstance();
		BufferBuilder r = t.getBuffer();
		r.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

		// Front.
		r.pos(bounds.minX, bounds.minY, bounds.maxZ).tex(frontOffset, 0.5f).normal(0, 0, 1).endVertex();
		r.pos(bounds.maxX, bounds.minY, bounds.maxZ).tex(frontOffset + 0.5f, 0.5f).normal(0, 0, 1).endVertex();
		r.pos(bounds.maxX, bounds.maxY, bounds.maxZ).tex(frontOffset + 0.5f, 0).normal(0, 0, 1).endVertex();
		r.pos(bounds.minX, bounds.maxY, bounds.maxZ).tex(frontOffset, 0).normal(0, 0, 1).endVertex();

		// Top.
		r.pos(bounds.maxX, bounds.maxY, bounds.maxZ).tex(1, 0.5f).normal(0, 1, 0).endVertex();
		r.pos(bounds.maxX, bounds.maxY, bounds.minZ).tex(1, 1).normal(0, 1, 0).endVertex();
		r.pos(bounds.minX, bounds.maxY, bounds.minZ).tex(0.5f, 1).normal(0, 1, 0).endVertex();
		r.pos(bounds.minX, bounds.maxY, bounds.maxZ).tex(0.5f, 0.5f).normal(0, 1, 0).endVertex();

		// Bottom.
		r.pos(bounds.minX, bounds.minY, bounds.maxZ).tex(0.5f, 0.5f).normal(0, -1, 0).endVertex();
		r.pos(bounds.minX, bounds.minY, bounds.minZ).tex(0.5f, 1).normal(0, -1, 0).endVertex();
		r.pos(bounds.maxX, bounds.minY, bounds.minZ).tex(1, 1).normal(0, -1, 0).endVertex();
		r.pos(bounds.maxX, bounds.minY, bounds.maxZ).tex(1, 0.5f).normal(0, -1, 0).endVertex();

		// Left.
		r.pos(bounds.maxX, bounds.maxY, bounds.maxZ).tex(0, 0.5f).normal(1, 0, 0).endVertex();
		r.pos(bounds.maxX, bounds.minY, bounds.maxZ).tex(0, 1).normal(1, 0, 0).endVertex();
		r.pos(bounds.maxX, bounds.minY, bounds.minZ).tex(0.5f, 1).normal(1, 0, 0).endVertex();
		r.pos(bounds.maxX, bounds.maxY, bounds.minZ).tex(0.5f, 0.5f).normal(1, 0, 0).endVertex();

		// Right.
		r.pos(bounds.minX, bounds.minY, bounds.maxZ).tex(0, 1).normal(-1, 0, 0).endVertex();
		r.pos(bounds.minX, bounds.maxY, bounds.maxZ).tex(0, 0.5f).normal(-1, 0, 0).endVertex();
		r.pos(bounds.minX, bounds.maxY, bounds.minZ).tex(0.5f, 0.5f).normal(-1, 0, 0).endVertex();
		r.pos(bounds.minX, bounds.minY, bounds.minZ).tex(0.5f, 1).normal(-1, 0, 0).endVertex();

		t.draw();
	}

	private final HashMap<String, Triple> entitledPlayers;
	private final Triple WHITE;

	private boolean rendering = false;
	private long time = -1;
	private Triple color;

	public UpgradeRenderer() {
		if(!Mods.isLoaded(Mods.OpenComputers)) {
			entitledPlayers = null;
			WHITE = null;
			return;
		}
		entitledPlayers = new HashMap<String, Triple>();
		WHITE = new Triple(1, 1, 1);
		entitledPlayers.put("f3ba6ec8-c280-4950-bb08-1fcb2eab3a9c", WHITE); // Vexatos
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onPlayerTickPre(RenderPlayerEvent.Pre e) {
		String uuid = e.getEntityPlayer().getUniqueID().toString();
		if(PetRenderer.hidden().contains(uuid) || !entitledPlayers.containsKey(uuid)) {
			return;
		}
		rendering = true;
		time = e.getEntityPlayer().getEntityWorld().getTotalWorldTime() + (e.getEntityPlayer().hashCode() ^ 0xFF);
		color = entitledPlayers.get(uuid);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotRender(RobotRenderEvent e) {
		if(!rendering) {
			return;
		}
		if(e.mountPoints == null || e.mountPoints.length < 2) {
			return;
		}
		MountPoint mountPoint = e.mountPoints[1];
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(0.5f, 0.5f, 0.5f);
		GlStateManager.color(color.r, color.g, color.b);
		float degrees = 360 - ((time % 160) / 160F * 360F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(modelRadar);
		GlStateManager.disableCull();
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
		GlStateManager.translate(0F, -0.8F, 0F);
		GlStateManager.translate(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());
		GlStateManager.scale(0.3f, 0.3f, 0.3f);
		GlStateManager.pushMatrix();
		radarModel.render(Math.max(degrees, 0));
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onPlayerTickPost(RenderPlayerEvent.Pre e) {
		if(rendering) {
			rendering = false;
			time = -1;
			color = WHITE;
		}
	}
}
