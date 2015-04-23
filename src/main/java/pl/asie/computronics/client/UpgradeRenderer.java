package pl.asie.computronics.client;

import li.cil.oc.api.driver.item.UpgradeRenderer.MountPointName;
import li.cil.oc.api.event.RobotRenderEvent.MountPoint;
import li.cil.oc.api.internal.Robot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.client.model.ModelRadar;
import pl.asie.computronics.item.ItemOpenComputers;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Mostly stolen from Sangar.
 * Stolen with permission.
 * @author Sangar, Vexatos
 */
public class UpgradeRenderer {

	public static final UpgradeRenderer INSTANCE = new UpgradeRenderer();
	private final ResourceLocation
		upgradeRadar = new ResourceLocation("computronics", "textures/models/UpgradeRadar.png"),
		modelRadar = new ResourceLocation("computronics", "textures/models/ModelRadar.png"),
		upgradeChatBox = new ResourceLocation("computronics", "textures/models/UpgradeChatBox.png"),
		beepCard = new ResourceLocation("computronics", "textures/models/CardBeep.png");

	AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);

	private static final List<Integer> upgrades = Arrays.asList(1, 2, 5);

	private boolean isUpgrade(ItemStack stack) {
		return stack != null && stack.getItem() != null
			&& stack.getItem() instanceof ItemOpenComputers
			&& upgrades.contains(stack.getItemDamage());
	}

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
			case 5: {
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
				float degrees = ((robot.world().getTotalWorldTime() % 160 + pt) / 160F * 360F);
				degrees = 360 - degrees;
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
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				/*
				tm.bindTexture(upgradeRadar);
				drawSimpleBlock(mountPoint, 0, true);
				*/
				break;
			}
			case 5: {
				tm.bindTexture(beepCard);
				drawSimpleBlock(mountPoint, 0, true);
				break;
			}
		}
	}

	private void drawSimpleBlock(MountPoint mountPoint) {
		drawSimpleBlock(mountPoint, 0);
	}

	private void drawSimpleBlock(MountPoint mountPoint, float frontOffset) {
		drawSimpleBlock(mountPoint, frontOffset, false);
	}

	//Mostly stolen from Sangar, like most of the things in this class.
	//Stolen with permission.
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
}
