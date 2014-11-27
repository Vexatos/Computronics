package pl.asie.computronics.api.multiperipheral;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import pl.asie.computronics.reference.Mods;

/**
 * Allows having Multiple peripherals merged into a single one.
 * <p/>
 * Register is using
 * @author Vexatos
 */
public interface IMultiPeripheral extends IPeripheral {

	/**
	 * The priority of the peripheral. Higher number means that this peripheral's methods will be preferred.
	 * @return The priority, default should be 0
	 */
	@Optional.Method(modid = Mods.ComputerCraft)
	public int peripheralPriority();
}
