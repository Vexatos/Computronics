package pl.asie.computronics.api.multiperipheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * Basic implementation of a MultiPeripheral which wraps a normal {@link dan200.computercraft.api.peripheral.IPeripheral}
 * instance and has a default priority of 0.
 * @author Vexatos
 */
public class WrappedMultiPeripheral implements IMultiPeripheral {

	protected IPeripheral peripheral;

	public WrappedMultiPeripheral(IPeripheral peripheral) {
		this.peripheral = peripheral;
	}

	@Override
	public int peripheralPriority() {
		return 0;
	}

	@Override
	public String getType() {
		return peripheral.getType();
	}

	@Override
	public String[] getMethodNames() {
		return peripheral.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return peripheral.callMethod(computer, context, method, arguments);
	}

	@Override
	public void attach(IComputerAccess computer) {
		peripheral.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		peripheral.detach(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other != null && other.equals(peripheral);
	}
}
