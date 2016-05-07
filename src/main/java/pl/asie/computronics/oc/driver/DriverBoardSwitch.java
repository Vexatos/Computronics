package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

/**
 * @author Vexatos
 */
public class DriverBoardSwitch extends ManagedEnvironment implements RackMountable {

	protected final boolean[] switches = new boolean[4];
	protected final Rack container;

	public DriverBoardSwitch(Rack container) {
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("switch_board", Visibility.Network).
			create());
	}

	@Override
	public NBTTagCompound getData() {
		NBTTagCompound tag = new NBTTagCompound();
		int switchData = 0;
		for(int i = 0; i < switches.length; i++) {
			switchData |= (switches[i] ? 1 : 0) << i;
		}
		tag.setByte("active", (byte) switchData);
		return tag;
	}

	@Override
	public boolean onActivate(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
		return true; //TODO do this
	}

	// Unused things

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}
}
