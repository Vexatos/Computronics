package pl.asie.computronics.integration.forestry.nanomachines;

import li.cil.oc.api.Nanomachines;
import li.cil.oc.api.nanomachines.Controller;
import li.cil.oc.api.nanomachines.DisableReason;
import li.cil.oc.api.prefab.AbstractBehavior;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.integration.forestry.entity.EntitySwarm;

/**
 * @author Vexatos
 */
public class SwarmBehavior extends AbstractBehavior {

	protected EntitySwarm entity;
	//protected int amplifier;
	//protected NBTTagCompound entityTag;

	protected SwarmBehavior(EntityPlayer player) {
		super(player);
	}

	@Override
	public String getNameHint() {
		return "hive_mind";
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable(DisableReason reason) {
		if(entity != null) {
			if(reason != DisableReason.Default && (player.capabilities == null || !player.capabilities.isCreativeMode)) {
				entity.setPlayer(null);
				entity.setAggressive(true);
				entity.setAttackTarget(player);
			} else {
				//entityTag = new NBTTagCompound();
				//entity.writeToNBT(entityTag);
				entity.setDead();
				entity = null;
			}
		}
	}

	public void spawnNewEntity(double x, double y, double z, ItemStack queen) {
		spawnNewEntity(x, y, z, 0xF0F000, true, queen);
	}

	public void spawnNewEntity(double x, double y, double z, int color, boolean tolerant, ItemStack queen) {
		if(!player.worldObj.isRemote) {
			entity = new EntitySwarm(player.worldObj, x, y, z, queen);
			//if(entityTag != null) {
			//	entity.readFromNBT(entityTag);
			//}
			entity.setAmplifier(Nanomachines.getController(player).getInputCount(this));
			//entity.setAmplifier(1);
			entity.setColor(color);
			entity.setTolerant(tolerant);
			entity.setPlayer(player);
			//entity.setPosition(x, y, z);
			player.worldObj.spawnEntityInWorld(entity);
		}
	}

	@Override
	public void update() {
		if(!player.worldObj.isRemote && entity != null) {
			if(entity.isDead) {
				entity = null;
				return;
			}
			Controller controller = Nanomachines.getController(player);
			int amplifier = controller.getInputCount(this);
			if(player.worldObj.getTotalWorldTime() % 10 == 0) {
				controller.changeBuffer(amplifier * 10 * -0.5);
			}
			entity.setAmplifier(amplifier);
			//} else {
			//spawnNewEntity(player.posX, player.posY + 2f, player.posZ);
		}
	}

	/*public void readFromNBT(NBTTagCompound tag) {
		//entityTag = tag.getCompoundTag("computronics:swarm");
		//if(entity != null) {
			//entity.readFromNBT(entityTag);
		//}
	}

	public void writeToNBT(NBTTagCompound tag) {
		//if(entity != null) {
			//entityTag = new NBTTagCompound();
			//entity.writeToNBT(entityTag);
		//}
		//tag.setTag("computronics:swarm", entityTag);
	}*/
}
