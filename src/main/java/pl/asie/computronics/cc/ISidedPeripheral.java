package pl.asie.computronics.cc;

/**
 * Used for peripherals that should only be accessed from a specific side.
 * @author Vexatos
 */
public interface ISidedPeripheral {

	public boolean canConnectPeripheralOnSide(int side);

}
