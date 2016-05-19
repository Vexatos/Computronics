package pl.asie.computronics.oc.driver;

import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.EnumSet;

/**
 * @author Vexatos
 */
public abstract class RackMountableWithComponentConnector extends ManagedEnvironmentWithComponentConnector implements RackMountable {

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, ItemStack heldItem, float hitX, float hitY) {
		return false;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}
}
