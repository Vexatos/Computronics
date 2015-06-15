package pl.asie.computronics.util.internal;

import pl.asie.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
public interface ITapeDrive {
	public void switchState(State state);
}
