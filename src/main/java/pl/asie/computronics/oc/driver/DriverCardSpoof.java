package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.NetworkCard;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Sangar, Vexatos
 */
public class DriverCardSpoof extends NetworkCard {

	protected final EnvironmentHost container;
	private ComponentConnector node;

	public DriverCardSpoof(EnvironmentHost container) {
		super(container);
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Network)
			.withComponent("modem", Visibility.Neighbors)
			.withConnector()
			.create());
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			return deviceInfo = new OCUtils.Device(
				DeviceClass.Network,
				"Ethernet contorter",
				OCUtils.Vendors.Hosencorp,
				"42i520 (MPN-01) - Br1ck"
			).deviceInfo();
		}
		return deviceInfo;
	}

	@Override
	@Callback(doc = "function(targetaddress:string, [sourceaddress:string,] port:number, data...) -- Sends the specified data to the specified target (from the source address if specified)")
	public Object[] send(Context context, Arguments args) {
		String target = args.checkString(0);
		String source = node().address();
		int port;
		int count = 2;
		if(args.isString(1)) {
			source = args.checkString(1);
			port = checkPort(args.checkInteger(2));
			count = 3;
		} else {
			port = checkPort(args.checkInteger(1));
		}
		if(node.tryChangeBuffer(-Config.SPOOFING_ENERGY_COST)) {
			Packet packet = Network.newPacket(source, target, port, dropArgs(args, count));
			doSend(packet);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	@Override
	@Callback(doc = "function([sourceaddress:string,] port:number, data...) -- Broadcasts the specified data on the specified port (from the source address if specified)")
	public Object[] broadcast(Context context, Arguments args) {
		String source = node().address();
		int port;
		int count = 1;
		if(args.isString(0)) {
			source = args.checkString(0);
			port = checkPort(args.checkInteger(1));
			count = 2;
		} else {
			port = checkPort(args.checkInteger(0));
		}
		if(node.tryChangeBuffer(-Config.SPOOFING_ENERGY_COST)) {
			Packet packet = Network.newPacket(source, null, port, dropArgs(args, count));
			doBroadcast(packet);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	private Object[] dropArgs(Arguments args, int count) {
		ArrayList<Object> list = new ArrayList<Object>();
		for(Object obj : args) {
			if(count > 0) {
				count--;
			} else {
				list.add(obj);
			}
		}
		return list.toArray();
	}

	//Because Scala
	@Override
	public EnvironmentHost host() {
		return this.container;
	}

	@Override
	public Component node() {
		return this.node != null ? this.node : super.node();
	}

	@Override
	protected void setNode(Node value) {
		if(value instanceof ComponentConnector) {
			this.node = (ComponentConnector) value;
		}
		super.setNode(value);
	}
}
