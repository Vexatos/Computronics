package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.boom.SelfDestruct;

/**
 * @author Vexatos
 */
public class DriverCardBoom extends ManagedEnvironment {

	protected final EnvironmentHost container;

	public DriverCardBoom(EnvironmentHost container) {
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("self_destruct").
			create());
		if(this.node() != null) {
			initOCFilesystem();
		}
	}

	private li.cil.oc.api.network.ManagedEnvironment oc_fs;

	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(Computronics.class, Mods.Computronics, "lua/component/self_destruct"),
			"self_destruct");
		((Component) oc_fs.node()).setVisibility(Visibility.Neighbors);
	}

	@Override
	public void onConnect(final Node node) {
		if(node.host() instanceof Context) {
			node.connect(oc_fs.node());
		}
	}

	@Override
	public void onDisconnect(final Node node) {
		if(node.host() instanceof Context) {
			// Remove our file systems when we get disconnected from a
			// computer.
			node.disconnect(oc_fs.node());
		} else if(node == this.node()) {
			this.time = -1;
			// Remove the file system if we are disconnected, because in that
			// case this method is only called once.
			oc_fs.node().remove();
		}
	}

	@Override
	public void onMessage(final Message message) {
		super.onMessage(message);
		if((message.name().equals("computer.stopped")
			|| message.name().equals("computer.started"))
			&& node().isNeighborOf(message.source())) {
			this.time = -1;
		}
	}
	// Boom code

	private int time = -1;

	@Callback(doc = "function([time:number]):number; Starts the countdown; Will be ticking down until the time is reached. 5 seconds by default. Returns the time set")
	public Object[] start(Context context, Arguments args) {
		if(time >= 0) {
			return new Object[] { -1, "fuse has already been set" };
		}
		double fuse = args.optDouble(0, 5);
		if(fuse > 100000) {
			throw new IllegalArgumentException("time may not be greater than 100000");
		}
		this.time = (int) Math.round(Math.floor(fuse * 20));
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
		if(oc_fs != null && oc_fs.node() != null) {
			oc_fs.node().load(nbt.getCompoundTag("oc:fs"));
		}
		if(nbt.getBoolean("ticking")) {
			this.time = nbt.getInteger("time");
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		if(oc_fs != null && oc_fs.node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs.node().save(fsNbt);
			nbt.setTag("oc:fs", fsNbt);
		}
		if(this.time >= 0) {
			nbt.setBoolean("ticking", true);
			nbt.setInteger("time", this.time);
		} else {
			nbt.setBoolean("ticking", false);
		}
	}

	@Override
	public void update() {
		super.update();
		if(this.time < 0) {
			return;
		}
		this.time--;
		if(this.time <= 0) {
			//Bye bye.
			this.time = -1;
			SelfDestruct.goBoom(container.world(), container.xPosition(), container.yPosition(), container.zPosition());
		}
	}
}
