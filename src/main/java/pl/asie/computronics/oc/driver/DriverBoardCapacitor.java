package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;

/**
 * @author Vexatos
 */
public class DriverBoardCapacitor extends RackMountableWithComponentConnector {

	protected final Rack host;

	public DriverBoardCapacitor(Rack host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("rack_capacitor", Visibility.Network).
			withConnector(Config.RACK_CAPACITOR_CAPACITY).
			create());
	}

	@Callback(doc = "function():number; Returns the amount of energy stored in this capacitor.", direct = true)
	public Object[] energy(Context context, Arguments args) {
		return new Object[] { node.localBuffer() };
	}

	@Callback(doc = "function():number; Returns the total amount of energy this capacitor can store.", direct = true)
	public Object[] maxEnergy(Context context, Arguments args) {
		return new Object[] { node.localBufferSize() };
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Power,
			"Battery",
			OCUtils.Vendors.Soluna,
			"CapCube 64 (Rev. 2)"
		);
	}

	@Override
	public NBTTagCompound getData() {
		return null;
	}
}
