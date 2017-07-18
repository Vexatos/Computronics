package pl.asie.computronics.integration.forestry.nanomachines;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import li.cil.oc.api.Nanomachines;
import li.cil.oc.api.nanomachines.Behavior;
import li.cil.oc.api.nanomachines.Controller;
import li.cil.oc.api.prefab.AbstractProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.integration.forestry.IntegrationForestry;
import pl.asie.lib.util.RayTracer;

import javax.annotation.Nullable;
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
		//SwarmBehavior behavior = new SwarmBehavior(player);
		//behavior.readFromNBT(nbt);
		return new SwarmBehavior(player);
	}

	@Override
	protected void writeBehaviorToNBT(Behavior behavior, NBTTagCompound nbt) {
		/*if(behavior instanceof SwarmBehavior) {
			((SwarmBehavior) behavior).writeToNBT(nbt);
		}*/
	}

	@Override
	public Iterable<Behavior> createBehaviors(EntityPlayer player) {
		return Collections.<Behavior>singletonList(new SwarmBehavior(player));
	}

	private void findTarget(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, Event e) {
		if(stack != null && player.getCooldownTracker().hasCooldown(stack.getItem())) {
			return;
		}
		SwarmBehavior behavior = getSwarmBehavior(player);
		if(behavior != null) {
			if(behavior.entity != null) {
				RayTracer.instance().fire(player, 30);
				RayTraceResult target = RayTracer.instance().getTarget();
				if((target != null) && (target.typeOfHit == RayTraceResult.Type.ENTITY)) {
					Entity entity = target.entityHit;
					if(entity != null && entity instanceof EntityLivingBase && entity != behavior.entity) {
						behavior.entity.setAttackTarget((EntityLivingBase) entity);
						swingItem(player, hand, stack, e);
					}
				} else if(behavior.entity.getAttackTarget() != null) {
					behavior.entity.setAttackTarget(null);
					swingItem(player, hand, stack, e);
				}
			} else if(player.capabilities != null && player.capabilities.isCreativeMode) {
				Vec3d pos = player.getPositionVector().add(player.getLookVec());
				behavior.spawnNewEntity(pos.x, pos.y + 2f, pos.z,
					BeeManager.beeRoot.getMemberStack(BeeManager.beeRoot.templateAsIndividual(BeeManager.beeRoot.getDefaultTemplate()), EnumBeeType.QUEEN));
				swingItem(player, hand, stack, e);
			}
		} /*else {
			Controller controller = Nanomachines.installController(player);
			boolean win = false;
			while(!win) {
				Iterator<Behavior> behaviorSet = ((ControllerImpl) controller).configuration().behaviorMap().keySet().iterator();
				while(behaviorSet.hasNext()) {
					Behavior next = behaviorSet.next();
					if(next instanceof SwarmBehavior) {
						win = true;
						break;
					}
				}
				controller.reconfigure();
			}
		}*/
	}

	private void makeSwarm(double x, double y, double z, EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, IBeeHousing tile, Event e) {
		if(stack != null && player.getCooldownTracker().hasCooldown(stack.getItem())) {
			return;
		}
		if(tile.getBeekeepingLogic() != null && tile.getBeekeepingLogic().canWork()) {
			ItemStack queenStack = tile.getBeeInventory().getQueen();
			IBee member = BeeManager.beeRoot.getMember(queenStack);
			if(member != null) {
				SwarmBehavior behavior = getSwarmBehavior(player);
				if(behavior != null) {
					if(behavior.entity != null) {
						behavior.entity.setDead();
					}
					Controller controller = Nanomachines.getController(player);
					if(controller != null) {
						controller.changeBuffer(-10);
					}
					behavior.spawnNewEntity(x, y, z,
						member.getGenome().getPrimary().getSpriteColour(0),
						member.getGenome().getToleratesRain(), queenStack.copy());
					swingItem(player, hand, e);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		if(player != null && !player.world.isRemote) {
			ItemStack heldItem = e.getItemStack();
			if(!heldItem.isEmpty() && heldItem.getItem() == IntegrationForestry.itemStickImpregnated) {
				if(e instanceof PlayerInteractEvent.RightClickItem) {
					findTarget(player, e.getHand(), heldItem, e);
				} else if(e instanceof PlayerInteractEvent.RightClickBlock) {
					if(player.isSneaking() && e.getWorld().isBlockLoaded(e.getPos())) {
						TileEntity te = e.getWorld().getTileEntity(e.getPos());
						if(te instanceof IBeeHousing) {
							BlockPos pos = e.getPos();
							makeSwarm(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, player, e.getHand(), heldItem, (IBeeHousing) te, e);
						} else {
							findTarget(player, e.getHand(), heldItem, e);
						}
					} else {
						findTarget(player, e.getHand(), heldItem, e);
					}
				}
			} else if(heldItem.isEmpty() && e.getHand() == EnumHand.MAIN_HAND && e instanceof PlayerInteractEvent.RightClickBlock) {
				if(player.isSneaking() && e.getWorld().isBlockLoaded(e.getPos())) {
					TileEntity te = e.getWorld().getTileEntity(e.getPos());
					if(te instanceof IBeeHousing) {
						BlockPos pos = e.getPos();
						makeSwarm(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, player, e.getHand(), null, (IBeeHousing) te, e);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onMinecartInteract(MinecartInteractEvent e) {
		EntityPlayer player = e.getPlayer();
		if(player != null && !player.world.isRemote) {
			ItemStack heldItem = e.getItem();
			if(!heldItem.isEmpty() && heldItem.getItem() == IntegrationForestry.itemStickImpregnated) {
				if(player.isSneaking() && e.getMinecart() instanceof IBeeHousing) {
					makeSwarm(e.getMinecart().posX, e.getMinecart().posY + 0.25, e.getMinecart().posZ, player, e.getHand(), heldItem, (IBeeHousing) e.getMinecart(), e);
				} else {
					findTarget(player, e.getHand(), heldItem, e);
				}
			} else if(heldItem.isEmpty() && e.getHand() == EnumHand.MAIN_HAND) {
				if(player.isSneaking() && e.getMinecart() instanceof IBeeHousing) {
					makeSwarm(e.getMinecart().posX, e.getMinecart().posY + 0.25, e.getMinecart().posZ, player, e.getHand(), null, (IBeeHousing) e.getMinecart(), e);
				}
			}
		}
	}

	/*@SubscribeEvent(priority = EventPriority.HIGH)
	public void tooltip(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		if(stack != null && stack.getItem() == IntegrationForestry.itemStickImpregnated) {
			SwarmBehavior behavior = getSwarmBehavior(e.getEntityPlayer());
			if(behavior != null) {
				e.getToolTip().add("" + TextFormatting.ITALIC + TextFormatting.DARK_GRAY + "Something inside you makes this somwhat look like a baton.");
			}
		}
	}*/

	public static void swingItem(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, @Nullable Event e) {
		if(stack != null) {
			player.getCooldownTracker().setCooldown(stack.getItem(), 20);
		}
		swingItem(player, hand, e);
	}

	public static void swingItem(EntityPlayer player, EnumHand hand, @Nullable Event e) {
		player.swingArm(hand);
		if(player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
			((EntityPlayerMP) player).connection.sendPacket(new SPacketAnimation(player, hand == EnumHand.MAIN_HAND ? 0 : 3));
		}
		if(e != null && e.isCancelable()) {
			e.setCanceled(true);
		}
	}

	/*@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		SwarmBehavior behavior = getSwarmBehavior(e.player);
		if(behavior != null && !e.player.worldObj.isRemote && e.phase == TickEvent.PlayerTickEvent.Phase.START) {
			behavior.update();
		}
	}*/

	//private final HashMap<String, SwarmBehavior> behaviors = new HashMap<String, SwarmBehavior>();

	@Nullable
	private SwarmBehavior getSwarmBehavior(EntityPlayer player) {
		Controller controller = Nanomachines.getController(player);
		if(controller != null) {
			Iterable<Behavior> behaviors = controller.getActiveBehaviors();
			for(Behavior behavior : behaviors) {
				if(behavior instanceof SwarmBehavior) {
					return (SwarmBehavior) behavior;
				}
			}
			controller.setInput(0, true);
		}
		return null;

		/*SwarmBehavior behavior = behaviors.get(player.getCommandSenderName());
		if(behavior == null) {
			behavior = new SwarmBehavior(player);
			behavior.onEnable();
			behaviors.put(player.getCommandSenderName(), behavior);
		}
		return behavior;*/
	}

}
