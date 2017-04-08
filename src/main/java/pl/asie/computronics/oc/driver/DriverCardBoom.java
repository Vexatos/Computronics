package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.boom.SelfDestruct;

/**
 * @author Vexatos
 */
public class DriverCardBoom extends ManagedEnvironmentWithComponentConnector {

	protected final EnvironmentHost container;

	public DriverCardBoom(EnvironmentHost container) {
		this.container = container;
		createNode();
	}

	protected void createNode() {
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("self_destruct").
			create());
	}

	@Override
	public void onConnect(final Node node) {

	}

	@Override
	public void onDisconnect(final Node node) {

	}

	@Override
	public void onMessage(final Message message) {
		super.onMessage(message);
		if((message.name().equals("computer.stopped")
			|| message.name().equals("computer.started"))
			&& node().isNeighborOf(message.source())) {
			setTime(-1);
		}
	}
	// Boom code

	protected int time = -1;

	protected void setTime(int time) {
		this.time = time;
	}

	@Callback(doc = "function([time:number]):number; Starts the countdown; Will be ticking down until the time is reached. 5 seconds by default. Returns the time set")
	public Object[] start(Context context, Arguments args) {
		if(time >= 0) {
			return new Object[] { -1, "fuse has already been set" };
		}
		double fuse = args.optDouble(0, 5);
		if(fuse > 100000) {
			throw new IllegalArgumentException("time may not be greater than 100000");
		}
		setTime((int) Math.round(Math.floor(fuse * 20)));
		return new Object[] { fuse };
	}

	@Callback(doc = "function():number; Returns the time in seconds left", direct = true)
	public Object[] time(Context context, Arguments args) {
		if(time < 0) {
			return new Object[] { -1, "fuse has not been set" };
		}
		return new Object[] { (double) this.time / 20D };
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(nbt.getBoolean("ticking")) {
			setTime(nbt.getInteger("time"));
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		if(this.time >= 0) {
			nbt.setBoolean("ticking", true);
			nbt.setInteger("time", this.time);
		} else {
			nbt.setBoolean("ticking", false);
		}
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Generic,
			"Machine destruction service",
			OCUtils.Vendors.HuggingCreeper,
			"SD-Struct 1"
		);
	}

	@Override
	public void update() {
		super.update();
		if(this.time < 0) {
			return;
		}
		if(this.time <= 0) {
			//Bye bye.
			goBoom();
		} else {
			--this.time;
		}
	}

	protected void goBoom() {
		SelfDestruct.goBoom(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), true);
	}
}
