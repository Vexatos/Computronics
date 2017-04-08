package pl.asie.computronics.oc.client;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.driver.item.UpgradeRenderer.MountPointName;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.event.RobotRenderEvent.MountPoint;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.client.renderer.PetRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.client.model.ModelRadar;
import pl.asie.computronics.item.ItemOpenComputers;
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
public class UpgradeRenderer {

	private final ResourceLocation
		upgradeRadar = new ResourceLocation("computronics", "textures/models/UpgradeRadar.png"),
		modelRadar = new ResourceLocation("computronics", "textures/models/ModelRadar.png"),
		upgradeChatBox = new ResourceLocation("computronics", "textures/models/UpgradeChatBox.png"),
		beepCard = new ResourceLocation("computronics", "textures/models/CardBeep.png");

	AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);

	private static final List<Integer> upgrades = Arrays.asList(1, 2, 5, 8);

	@Optional.Method(modid = Mods.OpenComputers)
	private boolean isUpgrade(ItemStack stack) {
		return stack != null && stack.getItem() != null
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
				drawSimpleBlock(mountPoint, 0, true);
				break;
			}
			case 2: {
				if(mountPoint.name.equals(MountPointName.TopLeft) || mountPoint.name.equals(MountPointName.TopRight)) {
					float degrees = robot.shouldAnimate() ?
						((robot.world().getTotalWorldTime() + (robot.hashCode() ^ 0xFF)) % 160 + pt) / 160F * 360F : 0F;
					if(mountPoint.name.equals(MountPointName.TopRight)) {
						degrees = 360 - degrees;
					}
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					tm.bindTexture(modelRadar);
					GL11.glDisable(GL11.GL_CULL_FACE);
					GL11.glRotatef(180, 1, 0, 0);
					GL11.glRotatef(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
					GL11.glTranslatef(0F, -0.8F, 0F);
					GL11.glTranslatef(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());
					GL11.glScalef(0.3f, 0.3f, 0.3f);
					GL11.glPushMatrix();
					radarModel.render(Math.max(degrees, 0));
					GL11.glPopMatrix();
					GL11.glPopAttrib();
				} else {
					tm.bindTexture(upgradeRadar);
					drawSimpleBlock(mountPoint, 0, true);
				}
				break;
			}
			case 5:
			case 8: {
				tm.bindTexture(beepCard);
				drawSimpleBlock(mountPoint, 0, true);
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
	private void drawSimpleBlock(MountPoint mountPoint, float frontOffset, boolean separateTopBottomTextures) {
		GL11.glRotatef(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
		GL11.glTranslatef(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());
		GL11.glBegin(GL11.GL_QUADS);

		// Front.
		GL11.glNormal3f(0, 0, 1);
		GL11.glTexCoord2f(frontOffset, 0.5f);
		GL11.glVertex3d(bounds.minX, bounds.minY, bounds.maxZ);
		GL11.glTexCoord2f(frontOffset + 0.5f, 0.5f);
		GL11.glVertex3d(bounds.maxX, bounds.minY, bounds.maxZ);
		GL11.glTexCoord2f(frontOffset + 0.5f, 0);
		GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.maxZ);
		GL11.glTexCoord2f(frontOffset, 0);
		GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.maxZ);

		// Top.
		if(!separateTopBottomTextures) {
			GL11.glNormal3f(0, 1, 0);
			GL11.glTexCoord2f(1, 0.5f);
			GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.maxZ);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.minZ);
			GL11.glTexCoord2f(0.5f, 1);
			GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.minZ);
			GL11.glTexCoord2f(0.5f, 0.5f);
			GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.maxZ);
		} else {
			GL11.glNormal3f(0, 1, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.maxZ);
			GL11.glTexCoord2f(1, 0.5f);
			GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.minZ);
			GL11.glTexCoord2f(0.5f, 0.5f);
			GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.minZ);
			GL11.glTexCoord2f(0.5f, 0);
			GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.maxZ);
		}

		// Bottom.

		GL11.glNormal3f(0, -1, 0);
		GL11.glTexCoord2f(0.5f, 0.5f);
		GL11.glVertex3d(bounds.minX, bounds.minY, bounds.maxZ);
		GL11.glTexCoord2f(0.5f, 1);
		GL11.glVertex3d(bounds.minX, bounds.minY, bounds.minZ);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3d(bounds.maxX, bounds.minY, bounds.minZ);
		GL11.glTexCoord2f(1, 0.5f);
		GL11.glVertex3d(bounds.maxX, bounds.minY, bounds.maxZ);

		// Left.
		GL11.glNormal3f(1, 0, 0);
		GL11.glTexCoord2f(0, 0.5f);
		GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.maxZ);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3d(bounds.maxX, bounds.minY, bounds.maxZ);
		GL11.glTexCoord2f(0.5f, 1);
		GL11.glVertex3d(bounds.maxX, bounds.minY, bounds.minZ);
		GL11.glTexCoord2f(0.5f, 0.5f);
		GL11.glVertex3d(bounds.maxX, bounds.maxY, bounds.minZ);

		// Right.
		GL11.glNormal3f(-1, 0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3d(bounds.minX, bounds.minY, bounds.maxZ);
		GL11.glTexCoord2f(0, 0.5f);
		GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.maxZ);
		GL11.glTexCoord2f(0.5f, 0.5f);
		GL11.glVertex3d(bounds.minX, bounds.maxY, bounds.minZ);
		GL11.glTexCoord2f(0.5f, 1);
		GL11.glVertex3d(bounds.minX, bounds.minY, bounds.minZ);

		GL11.glEnd();
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
		String uuid = e.entityPlayer.getUniqueID().toString();
		if(PetRenderer.hidden().contains(uuid) || !entitledPlayers.containsKey(uuid)) {
			return;
		}
		rendering = true;
		time = e.entityPlayer.getEntityWorld().getTotalWorldTime() + (e.entityPlayer.hashCode() ^ 0xFF);
		color = entitledPlayers.get(uuid);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotRender(RobotRenderEvent e) {
		if(!rendering) {
			return;
		}
		if(e.mountPoints == null || e.mountPoints.length < 2) {
			return;
		}
		MountPoint mountPoint = e.mountPoints[1];
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		GL11.glColor3f(color.r, color.g, color.b);
		float degrees = 360 - ((time % 160) / 160F * 360F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(modelRadar);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glRotatef(mountPoint.rotation.getW(), mountPoint.rotation.getX(), mountPoint.rotation.getY(), mountPoint.rotation.getZ());
		GL11.glTranslatef(0F, -0.8F, 0F);
		GL11.glTranslatef(mountPoint.offset.getX(), mountPoint.offset.getY(), mountPoint.offset.getZ());
		GL11.glScalef(0.3f, 0.3f, 0.3f);
		GL11.glPushMatrix();
		radarModel.render(Math.max(degrees, 0));
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
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
