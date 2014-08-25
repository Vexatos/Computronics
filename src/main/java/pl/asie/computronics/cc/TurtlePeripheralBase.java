package pl.asie.computronics.cc;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public abstract class TurtlePeripheralBase implements IPeripheral {
	protected ITurtleAccess access;
	
	public TurtlePeripheralBase(ITurtleAccess access) {
		this.access = access;
	}

	@Override
	public void attach(IComputerAccess computer) { }

	@Override
	public void detach(IComputerAccess computer) { }

	@Override
	public boolean equals(IPeripheral other) {
		if (other == null || !(other instanceof TurtlePeripheralBase)) return false;
		return ((TurtlePeripheralBase)other).access.equals(access);
	}

}
