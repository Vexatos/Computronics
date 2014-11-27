package pl.asie.computronics.integration.railcraft;

import mods.railcraft.client.render.IIconProvider;
import mods.railcraft.common.blocks.signals.ISignalTileDefinition;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public enum SignalTypes implements IIconProvider, ISignalTileDefinition {
	Digital("digitalBox", 3.0F, true, Computronics.railcraft.digitalBox, ForgeDirection.UP);

	private final boolean needsSupport;
	private final float hardness;
	private final String tag;
	private final ForgeDirection direction;
	private Block block;

	SignalTypes(String tag, float hardness, boolean needsSupport, Block block, ForgeDirection direction) {
		this.tag = tag;
		this.hardness = hardness;
		this.needsSupport = needsSupport;
		this.block = block;
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
	public boolean needsSupport() {
		return this.needsSupport;
	}

	public IIcon getIcon() {
		return block.getIcon(direction.ordinal(), getMeta());
	}
}
