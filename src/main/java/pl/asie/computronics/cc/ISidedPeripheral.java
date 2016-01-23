package pl.asie.computronics.cc;

import net.minecraft.util.EnumFacing;

/**
 * Used for peripherals that should only be accessed from a specific side.
 * @author Vexatos
 */
public interface ISidedPeripheral {

	public boolean canConnectPeripheralOnSide(EnumFacing side);

}
