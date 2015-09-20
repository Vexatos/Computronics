package pl.asie.computronics.integration.forestry.nanomachines;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import li.cil.oc.api.Nanomachines;
import li.cil.oc.api.nanomachines.Behavior;
import li.cil.oc.api.nanomachines.Controller;
import li.cil.oc.api.prefab.AbstractProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
		if(player != null && !player.worldObj.isRemote) {
			if((e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
				&& player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick) {
				SwarmBehavior behavior = getSwarmBehavior(player);
				if(behavior != null) {
					if(behavior.entity == null) {
						behavior.spawnNewEntity(player.posX, player.posY + 2f, player.posZ);
					} else {
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
			} else if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && player.getHeldItem() == null
				&& player.isSneaking() && e.world.blockExists(e.x, e.y, e.z)) {
				TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
				if(te instanceof IBeeHousing) {
					IBeeHousing tile = (IBeeHousing) te;
					// TODO make this use IBeekeepingLogic in Forestry 4
					if(tile.getQueen() != null) {
						SwarmBehavior behavior = getSwarmBehavior(player);
						if(behavior != null) {
							if(behavior.entity != null) {
								behavior.entity.setDead();
							}
							try {
								behavior.spawnNewEntity(e.x + 0.5, e.y + 0.5, e.z + 0.5, BeeManager.beeRoot.getMember(tile.getQueen()).getGenome().getPrimary().getIconColour(0));
							} catch(NullPointerException ex) {
								behavior.spawnNewEntity(e.x + 0.5, e.y + 0.5, e.z + 0.5);
							}
						}
					}
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
