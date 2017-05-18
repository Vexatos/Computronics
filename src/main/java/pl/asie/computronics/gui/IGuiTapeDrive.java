package pl.asie.computronics.gui;

import net.minecraft.inventory.Slot;
import pl.asie.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
public interface IGuiTapeDrive {

	void setState(State state);

	State getState();

	boolean isLocked(Slot slot, int index, int button, int shift);

	boolean shouldCheckHotbarKeys();
}
