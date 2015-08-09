package pl.asie.computronics.cc.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import pl.asie.computronics.api.multiperipheral.WrappedMultiPeripheral;

/**
 * @author Vexatos
 */
public class DefaultMultiPeripheral extends WrappedMultiPeripheral {

	public DefaultMultiPeripheral(IPeripheral peripheral) {
		super(peripheral);
	}

	@Override
	public int peripheralPriority() {
		return 0;
	}
}
