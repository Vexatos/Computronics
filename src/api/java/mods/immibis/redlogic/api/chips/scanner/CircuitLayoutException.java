package mods.immibis.redlogic.api.chips.scanner;

import net.minecraft.util.IChatComponent;

/**
 * Can be thrown from some methods to abort the scanning process and display a message to the player.
 */
public class CircuitLayoutException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final IChatComponent display;
	
	public CircuitLayoutException(String exceptionMessage, IChatComponent display) {
		super(exceptionMessage);
		this.display = display;
	}
	
	public CircuitLayoutException(IChatComponent display) {
		this(display.toString(), display);
	}

	public IChatComponent getDisplayMessage() {
		return display;
	}
}
