package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.oc.ManagedEnvironmentWithComponentConnector;
import pl.asie.computronics.reference.Config;

import java.util.EnumSet;

/**
 * @author Vexatos
 */
public class DriverBoardCapacitor extends ManagedEnvironmentWithComponentConnector implements RackMountable {

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
	public NBTTagCompound getData() {
		return null;
	}

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public boolean onActivate(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}
}
