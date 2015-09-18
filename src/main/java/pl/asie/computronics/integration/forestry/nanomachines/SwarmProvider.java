package pl.asie.computronics.integration.forestry.nanomachines;

import li.cil.oc.api.nanomachines.Behavior;
import li.cil.oc.api.prefab.AbstractProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;

/**
 * @author Vexatos
 */
public class SwarmProvider extends AbstractProvider {

	protected SwarmProvider() {
		super("computronics:forestry-swarmprovider");
	}

	@Override
	protected Behavior readBehaviorFromNBT(EntityPlayer player, NBTTagCompound nbt) {
		return null;
	}

	protected void writeBehaviorToNBT(Behavior behavior, NBTTagCompound nbt) {

	}

	@Override
	public Iterable<Behavior> createBehaviors(EntityPlayer player) {
		return Collections.<Behavior>singletonList(new SwarmBehavior(player));
	}
}
