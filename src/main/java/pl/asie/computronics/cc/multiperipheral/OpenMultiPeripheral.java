package pl.asie.computronics.cc.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import pl.asie.computronics.api.multiperipheral.WrappedMultiPeripheral;

/**
 * @author Vexatos
 */
public class OpenMultiPeripheral extends WrappedMultiPeripheral {
	private final boolean derped;

	public OpenMultiPeripheral(IPeripheral peripheral) {
		super(peripheral);
		this.derped = peripheral.getType().equals("broken_peripheral");
	}

	@Override
	public String[] getMethodNames() {
		return derped ? new String[] { "open_peripherals_derped" } : super.getMethodNames();
	}
}
