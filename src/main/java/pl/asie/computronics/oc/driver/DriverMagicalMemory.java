package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.driver.item.Memory;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.util.OCUtils;

import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverMagicalMemory extends DriverOCSpecialPart implements Memory {

	public DriverMagicalMemory() {
		super(0);
	}

	@Override
	public double amount(ItemStack stack) {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		return new InternalManagedEnvironment();
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Memory;
	}

	@Override
	public int tier(ItemStack stack) {
		return 0;
	}

	private static class InternalManagedEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements DeviceInfo {

		public InternalManagedEnvironment() {
			this.setNode(Network.newNode(this, Visibility.Neighbors).create());
		}

		protected Map<String, String> deviceInfo;

		@Override
		public Map<String, String> getDeviceInfo() {
			if(deviceInfo == null) {
				return deviceInfo = new OCUtils.Device(
					DeviceClass.Memory,
					"Memory vortex",
					OCUtils.Vendors.ACME,
					"Mnemomagic 47"
				).deviceInfo();
			}
			return deviceInfo;
		}
	}
}
