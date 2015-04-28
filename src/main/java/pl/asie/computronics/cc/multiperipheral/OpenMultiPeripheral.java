package pl.asie.computronics.cc.multiperipheral;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.network.BlacklistedPeripheral;
import openperipheral.api.peripheral.IBrokenOpenPeripheral;
import pl.asie.computronics.api.multiperipheral.WrappedMultiPeripheral;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers)
public class OpenMultiPeripheral extends WrappedMultiPeripheral implements BlacklistedPeripheral {
	private final boolean derped;

	public OpenMultiPeripheral(IPeripheral peripheral) {
		super(peripheral);
		this.derped = Mods.isLoaded(Mods.OpenPeripheral)
			&& (peripheral instanceof IBrokenOpenPeripheral || peripheral.getType().equals("broken_peripheral"));
	}

	@Override
	public int peripheralPriority() {
		return -15;
	}

	@Override
	public String[] getMethodNames() {
		return derped ? new String[] { "open_peripherals_derped" } : super.getMethodNames();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}
}
