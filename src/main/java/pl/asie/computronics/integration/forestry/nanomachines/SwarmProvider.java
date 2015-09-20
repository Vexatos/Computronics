package pl.asie.computronics.integration.forestry.nanomachines;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import li.cil.oc.api.Nanomachines;
import li.cil.oc.api.nanomachines.Behavior;
import li.cil.oc.api.nanomachines.Controller;
import li.cil.oc.api.prefab.AbstractProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import pl.asie.lib.util.RayTracer;

import java.util.Collections;

/**
 * @author Vexatos
 */
public class SwarmProvider extends AbstractProvider {

	public SwarmProvider() {
		super("computronics:forestry-swarmprovider");
	}

	@Override
	protected Behavior readBehaviorFromNBT(EntityPlayer player, NBTTagCompound nbt) {
		SwarmBehavior behavior = new SwarmBehavior(player);
		behavior.readFromNBT(nbt);
		return behavior;
	}

	protected void writeBehaviorToNBT(Behavior behavior, NBTTagCompound nbt) {
		if(behavior instanceof SwarmBehavior) {
			((SwarmBehavior) behavior).writeToNBT(nbt);
		}
	}

	@Override
	public Iterable<Behavior> createBehaviors(EntityPlayer player) {
		return Collections.<Behavior>singletonList(new SwarmBehavior(player));
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e) {
		EntityPlayer player = e.entityPlayer;
		if(player != null && !player.worldObj.isRemote
			&& (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
			&& player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick) {
			SwarmBehavior behavior = getSwarmBehavior(player);
			if(behavior != null && behavior.entity != null) {
				RayTracer.instance().fire(player, 30);
				MovingObjectPosition target = RayTracer.instance().getTarget();
				if((target != null) && (target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)) {
					Entity entity = target.entityHit;
					if(entity != null && entity instanceof EntityLivingBase) {
						behavior.entity.setAttackTarget((EntityLivingBase) entity);
						player.swingItem();
					}
				} else if(behavior.entity.getAttackTarget() != null) {
					behavior.entity.setAttackTarget(null);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if(behavior != null && !e.player.worldObj.isRemote && e.phase == TickEvent.PlayerTickEvent.Phase.START) {
			behavior.update();
		}
	}

	private SwarmBehavior behavior;

	private SwarmBehavior getSwarmBehavior(EntityPlayer player) {
		Controller controller = Nanomachines.getController(player);
		if(controller != null) {
			Iterable<Behavior> behaviors = controller.getActiveBehaviors();
			for(Behavior behavior : behaviors) {
				if(behavior instanceof SwarmBehavior) {
					return (SwarmBehavior) behavior;
				}
			}
		}
		//return null

		// TODO remove
		if(behavior == null) {
			behavior = new SwarmBehavior(player);
			behavior.onEnable();
		}
		return behavior;
	}

}
