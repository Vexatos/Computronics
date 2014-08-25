package pl.asie.computronics.cc;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public abstract class TurtleUpgradeBase implements ITurtleUpgrade {
	private int upgradeID;
	
	public TurtleUpgradeBase(int id) {
		upgradeID = id;
	}
	
	@Override
	public int getUpgradeID() {
		return upgradeID;
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side,
			TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
	}
}
