package pl.asie.computronics.gui;

import pl.asie.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
public interface IGuiTapeDrive {

	void setState(State state);

	State getState();
}
