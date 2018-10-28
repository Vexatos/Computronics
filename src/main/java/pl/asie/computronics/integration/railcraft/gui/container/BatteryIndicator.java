package pl.asie.computronics.integration.railcraft.gui.container;

import mods.railcraft.common.gui.widgets.IndicatorController;
import net.minecraft.util.math.MathHelper;
import pl.asie.lib.api.tile.IBattery;

/**
 * @author CovertJaguar, Vexatos
 */
public class BatteryIndicator extends IndicatorController {

	private final IBattery battery;
	private double energy;

	public BatteryIndicator(IBattery battery) {
		this.battery = battery;
	}

	@Override
	protected void refreshToolTip() {
		this.tip.text = String.format("%,d / %,d RF", MathHelper.floor(this.energy), MathHelper.floor(this.battery.getMaxEnergyStored()));
	}

	@Override
	public double getMeasurement() {
		double e = Math.min(this.energy, this.battery.getMaxEnergyStored());
		return e / this.battery.getMaxEnergyStored();
	}

	@Override
	public void setClientValue(double value) {
		this.energy = value;
	}

	@Override
	public double getServerValue() {
		return this.battery.getEnergyStored();
	}
}
