package pl.asie.computronics.client;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.ICombinedRenderer;
import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.block.BlockDigitalReceiverBox;
import pl.asie.computronics.tile.TileDigitalReceiverBox;

public class SignalBoxCombinedRenderer
	implements ICombinedRenderer {
	public static final SignalBoxCombinedRenderer INSTANCE = new SignalBoxCombinedRenderer();
	private RenderFakeBlock.RenderInfo info = new RenderFakeBlock.RenderInfo();

	public SignalBoxCombinedRenderer() {
		this.info.texture = new IIcon[6];
		this.info.template = RailcraftBlocks.getBlockSignal();
	}

	public void renderBlock(RenderBlocks renderblocks, IBlockAccess iBlockAccess, int x, int y, int z, Block block) {
		TileDigitalReceiverBox tile = (TileDigitalReceiverBox) iBlockAccess.getTileEntity(x, y, z);
		float pix = 0.0625F;
		if(renderblocks.hasOverrideBlockTexture()) {
			this.info.override = renderblocks.overrideBlockTexture;
		} else {
			this.info.override = null;
		}
		this.info.texture[0] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[2];
		this.info.texture[1] = BlockDigitalReceiverBox.texturesBoxTop;
		this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];

		boolean eastWest = false;
		boolean northSouth = false;
		if((tile.isConnected(ForgeDirection.EAST)) || (tile.isConnected(ForgeDirection.WEST))) {
			eastWest = true;
		}
		if((tile.isConnected(ForgeDirection.NORTH)) || (tile.isConnected(ForgeDirection.SOUTH))) {
			northSouth = true;
		}
		boolean side2 = tile.isConnected(ForgeDirection.NORTH);
		boolean side3 = tile.isConnected(ForgeDirection.SOUTH);
		boolean side4 = tile.isConnected(ForgeDirection.WEST);
		boolean side5 = tile.isConnected(ForgeDirection.EAST);
		if((!eastWest) && (!northSouth)) {
			eastWest = true;
		}
		if(side2) {
			this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[1];
		}
		if(side3) {
			this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[1];
		}
		if(side4) {
			this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[1];
		}
		if(side5) {
			this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[1];
		}
		this.info.setBlockBounds(2.0F * pix, 0.0F, 2.0F * pix, 14.0F * pix, 15.0F * pix, 14.0F * pix);
		RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);

		this.info.renderSide[0] = false;
		this.info.renderSide[1] = false;
		for(int side = 2; side < 6; side++) {
			SignalAspect aspect = tile.getBoxSignalAspect(ForgeDirection.getOrientation(side));
			if(!aspect.isLit()) {
				aspect = SignalAspect.OFF;
			}
			IIcon lamp = mods.railcraft.common.blocks.signals.BlockSignal.texturesLampBox[aspect.getTextureIndex()];
			this.info.texture[2] = lamp;
			this.info.texture[3] = lamp;
			this.info.texture[4] = lamp;
			this.info.texture[5] = lamp;
			this.info.renderSide[2] = ((side == 2) && (!side2));
			this.info.renderSide[3] = ((side == 3) && (!side3));
			this.info.renderSide[4] = ((side == 4) && (!side4));
			this.info.renderSide[5] = ((side == 5) && (!side5));
			if(!renderblocks.hasOverrideBlockTexture()) {
				this.info.brightness = aspect.getTextureBrightness();
			}
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, this.info.brightness < 0, false);
		}
		this.info.brightness = -1;
		this.info.setRenderAllSides();
		if(!iBlockAccess.isAirBlock(x, y + 1, z)) {
			this.info.texture[1] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[3];
			this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
			this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
			this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
			this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
			this.info.setBlockBounds(5.0F * pix, 15.0F * pix, 5.0F * pix, 11.0F * pix, 16.0F * pix, 11.0F * pix);
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);
		}
		this.info.texture[0] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[4];
		this.info.texture[1] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[4];
		this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[5];
		this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[5];
		this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[5];
		this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[5];
		float min = 7.0F * pix;
		float max = 9.0F * pix;
		float minY = 10.0F * pix;
		float maxY = 12.0F * pix;
		float minXEW = side4 ? 0.0F : min;
		float maxXEW = side5 ? 1.0F : max;
		float minZNS = side2 ? 0.0F : min;
		float maxZNS = side3 ? 1.0F : max;
		if(eastWest) {
			this.info.setBlockBounds(minXEW, minY, min, maxXEW, maxY, max);
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);
		}
		if(northSouth) {
			this.info.setBlockBounds(min, minY, minZNS, max, maxY, maxZNS);
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);
		}
		minY = 5.0F * pix;
		maxY = 7.0F * pix;
		if(eastWest) {
			this.info.setBlockBounds(minXEW, minY, min, maxXEW, maxY, max);
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);
		}
		if(northSouth) {
			this.info.setBlockBounds(min, minY, minZNS, max, maxY, maxZNS);
			RenderFakeBlock.renderBlock(this.info, iBlockAccess, x, y, z, true, false);
		}
	}

	public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPushAttrib(8192);
		GL11.glEnable(2929);
		GL11.glEnable(3042);

		this.info.override = null;
		float pix = 0.0625F;
		this.info.setBlockBounds(2.0F * pix, 0.0F, 2.0F * pix, 14.0F * pix, 15.0F * pix, 14.0F * pix);
		this.info.texture[0] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[2];
		this.info.texture[1] = BlockDigitalReceiverBox.texturesBoxTop;
		this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesBox[0];
		RenderFakeBlock.renderBlockOnInventory(renderblocks, this.info, 1.0F);
		int texture = SignalAspect.RED.getTextureIndex();
		this.info.renderSide[0] = false;
		this.info.renderSide[1] = false;
		this.info.texture[2] = mods.railcraft.common.blocks.signals.BlockSignal.texturesLampBox[texture];
		this.info.texture[3] = mods.railcraft.common.blocks.signals.BlockSignal.texturesLampBox[texture];
		this.info.texture[4] = mods.railcraft.common.blocks.signals.BlockSignal.texturesLampBox[texture];
		this.info.texture[5] = mods.railcraft.common.blocks.signals.BlockSignal.texturesLampBox[texture];
		RenderFakeBlock.renderBlockOnInventory(renderblocks, this.info, 1.0F);
		this.info.setRenderAllSides();

		GL11.glPopAttrib();
	}
}
