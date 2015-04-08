package pl.asie.computronics.client;

import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.item.ItemOpenComputers;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sangar, Vexatos
 */
public class UpgradeRenderer implements IItemRenderer {

	private final ResourceLocation upgradeRadar = new ResourceLocation("computronics", "textures/models/UpgradeRadar.png");
	private final ResourceLocation upgradeChatBox = new ResourceLocation("computronics", "textures/models/UpgradeChatBox.png");

	AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);

	public static void initialize(Item item) {
		MinecraftForgeClient.registerItemRenderer(item, new UpgradeRenderer());
	}

	private static final List<Integer> upgrades = Arrays.asList(1, 2);

	public boolean isUpgrade(ItemStack stack) {
		return stack != null && stack.getItem() != null
			&& stack.getItem() instanceof ItemOpenComputers
			&& upgrades.contains(stack.getItemDamage());
	}

	@Override
	public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED && isUpgrade(stack);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return type == ItemRenderType.EQUIPPED && helper == ItemRendererHelper.EQUIPPED_BLOCK;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		RenderState.checkError(getClass().getName() + ".renderItem: entering (aka: wasntme)");
		if(!(stack.getItem() instanceof ItemOpenComputers)) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager tm = mc.getTextureManager();

		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		switch(stack.getItemDamage()) {
			case 1: {
				tm.bindTexture(upgradeChatBox);
				drawSimpleBlock(0, true);
				RenderState.checkError(getClass().getName() + ".renderItem: chat box upgrade");
				break;
			}
			case 2: {
				tm.bindTexture(upgradeRadar);
				drawSimpleBlock(0, true);
				RenderState.checkError(getClass().getName() + ".renderItem: radar upgrade");
				break;
			}
		}
	}

	private void drawSimpleBlock() {
		drawSimpleBlock(0);
	}

	private void drawSimpleBlock(float frontOffset) {
		drawSimpleBlock(frontOffset, false);
	}

	//Mostly stolen from Sangar, like most of the things in this class.
	//Stolen with permission.
	private void drawSimpleBlock(float frontOffset, boolean separateTopBottomTextures) {
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
