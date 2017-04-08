package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.core.render.ITextureStates;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.render.TextureStateManager;
import buildcraft.core.lib.utils.MatrixTranformations;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.integration.buildcraft.pluggable.DroneStationRenderer.TextureHandler.Textures;

/**
 * @author Vexatos
 */
public class DroneStationRenderer implements IPipePluggableRenderer {

	private float zFightOffset = 1 / 4096.0F;

	private void droneStationPartRender(RenderBlocks renderblocks,
		ForgeDirection side, ITextureStates blockStateMachine, int x, int y, int z,
		float xStart, float xEnd, float yStart, float yEnd, float zStart,
		float zEnd) {

		float[][] zeroState = new float[3][2];
		// X START - END
		zeroState[0][0] = xStart + zFightOffset;
		zeroState[0][1] = xEnd - zFightOffset;
		// Y START - END
		zeroState[1][0] = yStart;
		zeroState[1][1] = yEnd;
		// Z START - END
		zeroState[2][0] = zStart + zFightOffset;
		zeroState[2][1] = zEnd - zFightOffset;

		IIcon[] icons = ((TextureStateManager) blockStateMachine.getTextureState()).popArray();
		icons[0] = Textures.DRONE_STATION_BOTTOM.getIcon();
		icons[1] = Textures.DRONE_STATION_BOTTOM.getIcon();
		for(int i = 2; i < icons.length; i++) {
			icons[i] = Textures.DRONE_STATION_SIDE.getIcon();
		}
		//((TextureStateManager) blockStateMachine.getTextureState()).popArray();

		//blockStateMachine.getTextureState().set(Textures.DRONE_STATION_TOP.getIcon());

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
		//((TextureStateManager) blockStateMachine.getTextureState()).pushArray();
	}

	@Override
	public void renderPluggable(RenderBlocks renderblocks, IPipe pipe, ForgeDirection side, PipePluggable pipePluggable, ITextureStates blockStateMachine, int renderPass, int x, int y, int z) {
		if(renderPass != 0) {
			return;
		}

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.56F, 0.625F,
			0.09F, 0.225F - zFightOffset,
			0.375F, 0.44F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.56F, 0.625F,
			0.09F, 0.225F - zFightOffset,
			0.56F, 0.625F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.375F, 0.44F,
			0.09F, 0.225F - zFightOffset,
			0.375F, 0.44F);

		droneStationPartRender(renderblocks, side, blockStateMachine, x, y, z,
			0.375F, 0.44F,
			0.09F, 0.225F - zFightOffset,
			0.56F, 0.625F);

		float[][] zeroState = new float[3][2];

		// X START - END
		zeroState[0][0] = 0.25F + zFightOffset;
		zeroState[0][1] = 0.75F - zFightOffset;
		// Y START - END
		zeroState[1][0] = 0.225F;
		zeroState[1][1] = 0.25F - zFightOffset;
		// Z START - END
		zeroState[2][0] = 0.25F + zFightOffset;
		zeroState[2][1] = 0.75F - zFightOffset;

		IIcon[] icons = ((TextureStateManager) blockStateMachine.getTextureState()).popArray();
		icons[0] = Textures.DRONE_STATION_SIDE.getIcon();
		icons[1] = Textures.DRONE_STATION_TOP.getIcon();
		for(int i = 2; i < icons.length; i++) {
			icons[i] = Textures.DRONE_STATION_SIDE.getIcon();
		}
		//((TextureStateManager) blockStateMachine.getTextureState()).popArray();

		//blockStateMachine.getTextureState().set(Textures.DRONE_STATION_TOP.getIcon());

		float[][] rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);
		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0],
			rotated[2][0], rotated[0][1], rotated[1][1],
			rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);

		// X START - END
		zeroState[0][0] = 0.25F + 0.125F / 2 + zFightOffset;
		zeroState[0][1] = 0.75F - 0.125F / 2 - zFightOffset;
		// Y START - END
		zeroState[1][0] = 0.25F;
		zeroState[1][1] = 0.25F + 0.125F;
		// Z START - END
		zeroState[2][0] = 0.25F + 0.125F / 2 + zFightOffset;
		zeroState[2][1] = 0.75F - 0.125F / 2 - zFightOffset;

		//((TextureStateManager) blockStateMachine.getTextureState()).pushArray();

		icons[0] = Textures.DRONE_STATION_BOTTOM.getIcon();
		icons[1] = Textures.DRONE_STATION_SIDE.getIcon();
		for(int i = 2; i < icons.length; i++) {
			icons[i] = Textures.DRONE_STATION_SIDE.getIcon();
		}

		//blockStateMachine.getTextureState().set(Textures.DRONE_STATION_BOTTOM.getIcon());
		rotated = MatrixTranformations.deepClone(zeroState);
		MatrixTranformations.transform(rotated, side);

		renderblocks.setRenderBounds(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
		renderblocks.renderStandardBlock(blockStateMachine.getBlock(), x, y, z);
		((TextureStateManager) blockStateMachine.getTextureState()).pushArray();
	}

	public static class ItemRenderer extends ModelBase implements IItemRenderer {

		private ResourceLocation texture = new ResourceLocation("computronics", "textures/blocks/buildcraft/pluggable/drone_station.png");

		@Override
		public boolean handleRenderType(ItemStack item, ItemRenderType type) {
			switch(type) {
				case ENTITY:
				case EQUIPPED:
				case EQUIPPED_FIRST_PERSON:
				case INVENTORY:
					return true;
				default:
					return false;
			}
		}

		@Override
		public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
			return helper != ItemRendererHelper.BLOCK_3D;
		}

		@Override
		public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();

			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDisable(GL11.GL_CULL_FACE);
			switch(type) {
				case ENTITY:
					GL11.glRotatef(-180, 1, 0, 0);
					this.Base.render(1 / 16f);
					break;
				case EQUIPPED_FIRST_PERSON:
					GL11.glRotatef(20, 1F, 0F, 1F);
					GL11.glRotatef(50, 1F, 1F, 0F);
					GL11.glRotatef(20, 1F, 1F, 1F);
					GL11.glRotatef(-10, 0F, 1F, 0F);
					GL11.glRotatef(-20, 0F, 1F, 0F);
					GL11.glTranslatef(0.6F, 0F, -0.3F);
				case EQUIPPED:
					GL11.glTranslatef(0.6F, 1F, 0.7F);
					GL11.glRotatef(-140, 0, 0, 1F);
					GL11.glRotatef(140, 0, 1F, 0);
					GL11.glRotatef(-35, 1F, 0, 0);
					GL11.glScalef(2F, 2F, 2F);
					this.Base.render(1 / 16f);
					break;
				case INVENTORY:
					GL11.glScalef(2F, 2F, 2F);
					GL11.glRotatef(-180, 1, 0, 0);
					GL11.glRotatef(60, 0, 1, 0);
					this.Base.render(1 / 16f);
					break;
				default:
					break;
			}
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}

		public ModelRenderer Base;
		public ModelRenderer Nook1;
		public ModelRenderer Nook2;
		public ModelRenderer Nook3;
		public ModelRenderer Nook4;

		public ItemRenderer() {
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.Nook3 = new ModelRenderer(this, 8, 12);
			this.Nook3.setRotationPoint(1.0F, -2.0F, -2.0F);
			this.Nook3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
			this.Nook4 = new ModelRenderer(this, 12, 12);
			this.Nook4.setRotationPoint(-2.0F, -2.0F, -2.0F);
			this.Nook4.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
			this.Base = new ModelRenderer(this, 0, 0);
			this.Base.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.Base.addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8, 0.0F);
			this.Nook1 = new ModelRenderer(this, 0, 12);
			this.Nook1.setRotationPoint(1.0F, -2.0F, 1.0F);
			this.Nook1.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
			this.Nook2 = new ModelRenderer(this, 4, 12);
			this.Nook2.setRotationPoint(-2.0F, -2.0F, 1.0F);
			this.Nook2.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
			this.Base.addChild(this.Nook3);
			this.Base.addChild(this.Nook4);
			this.Base.addChild(this.Nook1);
			this.Base.addChild(this.Nook2);
		}
	}

	public static class TextureHandler {

		@SubscribeEvent
		public void textureHook(TextureStitchEvent.Pre event) {
			if(event.map.getTextureType() == 0) {
				for(Textures t : Textures.VALUES) {
					t.registerIcon(event.map);
				}
			}
		}

		enum Textures {
			DRONE_STATION_TOP("drone_station_top"),
			DRONE_STATION_BOTTOM("drone_station_bottom"),
			DRONE_STATION_SIDE("drone_station_side");

			private IIcon icon;
			private final String location;
			public static final Textures[] VALUES = values();

			Textures(String location) {
				this.location = location;
			}

			public IIcon getIcon() {
				return icon;
			}

			public void registerIcon(IIconRegister iconRegister) {
				this.icon = new WrappedIcon(iconRegister.registerIcon("computronics:buildcraft/pluggable/" + location));
			}
		}

		private static class WrappedIcon implements IIcon {

			private IIcon icon;
			private final int size;

			private WrappedIcon(IIcon icon) {
				this(icon, 2);
			}

			private WrappedIcon(IIcon icon, int size) {
				this.icon = icon;
				this.size = size;
			}

			@Override
			public int getIconWidth() {
				return icon.getIconWidth();
			}

			@Override
			public int getIconHeight() {
				return icon.getIconHeight();
			}

			@Override
			public float getMinU() {
				return size > 0 ? icon.getMinU() - (icon.getMaxU() - icon.getMinU()) * size / 4F : icon.getMinU();
			}

			@Override
			public float getMaxU() {
				return size > 0 ? icon.getMaxU() + (icon.getMaxU() - icon.getMinU()) * size / 4F : icon.getMaxU();
			}

			@Override
			public float getInterpolatedU(double par1) {
				float f = this.getMaxU() - this.getMinU();
				//return this.getMinU() + f * ((float) par1 / 16.0F);
				float uOffset = this.getMinU() + f * (float) par1 / 16.0F;
				if(uOffset < icon.getMinU()) {
					uOffset += Math.abs(icon.getMinU() - this.getMinU());
				} else if(uOffset > icon.getMaxU()) {
					uOffset -= Math.abs(this.getMaxU() - icon.getMaxU());
				}
				return uOffset;
			}

			@Override
			public float getMinV() {
				float f = icon.getMaxV() - icon.getMinV();
				return size > 0 ? icon.getMinV() - f * size / 4F : icon.getMinV();
			}

			@Override
			public float getMaxV() {
				float f = icon.getMaxV() - icon.getMinV();
				return size > 0 ? icon.getMaxV() + f * size / 4F : icon.getMaxV();
			}

			@Override
			public float getInterpolatedV(double par1) {
				float f = this.getMaxV() - this.getMinV();
				//return this.getMinV() + f * ((float) par1 / 16.0F);
				float vOffset = this.getMinV() + f * (float) par1 / 16.0F;
				if(vOffset < icon.getMinV()) {
					vOffset += Math.abs(icon.getMinV() - this.getMinV());
				} else if(vOffset > icon.getMaxV()) {
					vOffset -= Math.abs(this.getMaxV() - icon.getMaxV());
				}
				return vOffset;
			}

			@Override
			public String getIconName() {
				return icon.getIconName();
			}
		}
	}
}
