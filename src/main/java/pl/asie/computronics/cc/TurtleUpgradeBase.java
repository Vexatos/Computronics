package pl.asie.computronics.cc;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public abstract class TurtleUpgradeBase implements ITurtleUpgrade {
	private ResourceLocation upgradeID;
	
	public TurtleUpgradeBase(String id) {
		upgradeID = new ResourceLocation("computronics", id);
	}
	
	@Override
	public ResourceLocation getUpgradeID() {
		return upgradeID;
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side,
			TurtleVerb verb, EnumFacing direction) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
	}

	@Override
	public int getLegacyUpgradeID() {
		return -1;
	}
}
