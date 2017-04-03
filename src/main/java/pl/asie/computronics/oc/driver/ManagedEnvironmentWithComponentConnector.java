package pl.asie.computronics.oc.driver;

import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import pl.asie.computronics.util.OCUtils;

import java.util.Map;

/**
 * @author Vexatos
 */
public abstract class ManagedEnvironmentWithComponentConnector extends AbstractManagedEnvironment implements DeviceInfo {

	protected ComponentConnector node;

	@Override
	public Node node() {
		return this.node != null ? this.node : super.node();
	}

	@Override
	protected void setNode(Node value) {
		if(value == null) {
			this.node = null;
		} else if(value instanceof ComponentConnector) {
			this.node = ((ComponentConnector) value);
		}
		super.setNode(value);
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			OCUtils.Device device = deviceInfo();
			return deviceInfo = device.deviceInfo();
		}
		return deviceInfo;
	}

	protected abstract OCUtils.Device deviceInfo();
}
