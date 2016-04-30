package pl.asie.computronics.integration.railcraft;

import mods.railcraft.client.render.IIconProvider;
import mods.railcraft.common.blocks.signals.ISignalTileDefinition;
import mods.railcraft.common.blocks.signals.TileSignalFoundation;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalReceiverBox;

/**
 * @author Vexatos
 */
public enum SignalTypes implements IIconProvider, ISignalTileDefinition {
	DigitalReceiver("digitalReceiverBox", 3.0F, true, Computronics.railcraft.digitalReceiverBox, TileDigitalReceiverBox.class, ForgeDirection.UP),
	DigitalController("digitalControllerBox", 3.0F, true, Computronics.railcraft.digitalControllerBox, TileDigitalControllerBox.class, ForgeDirection.UP);

	private final boolean needsSupport;
	private final float hardness;
	private final String tag;
	private final ForgeDirection direction;
	private final Class<? extends TileSignalFoundation> tile;
	private Block block;

	SignalTypes(String tag, float hardness, boolean needsSupport, Block block, Class<? extends TileSignalFoundation> tile, ForgeDirection direction) {
		this.tag = tag;
		this.hardness = hardness;
		this.needsSupport = needsSupport;
		this.block = block;
		this.tile = tile;
		this.direction = direction;
	}

	@Override
	public String getTag() {
		return "tile.computronics." + this.tag;
	}

	@Override
	public float getHardness() {
		return this.hardness;
	}

	@Override
	public int getMeta() {
		return this.ordinal();
	}

	@Override
	public Class<? extends TileSignalFoundation> getTileClass() {
		return this.tile;
	}

	@Override
	public boolean needsSupport() {
		return this.needsSupport;
	}

	@Override
	public boolean isEnabled() {
		return this.block != null;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public IIcon getIcon() {
		return block.getIcon(direction.ordinal(), getMeta());
	}
}
