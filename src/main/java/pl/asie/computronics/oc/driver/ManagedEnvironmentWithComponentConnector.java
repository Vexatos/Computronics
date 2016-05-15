package pl.asie.computronics.oc.driver;

import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.prefab.ManagedEnvironment;

/**
 * @author Vexatos
 */
public abstract class ManagedEnvironmentWithComponentConnector extends ManagedEnvironment {

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
}
