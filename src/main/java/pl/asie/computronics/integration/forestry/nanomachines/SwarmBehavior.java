package pl.asie.computronics.integration.forestry.nanomachines;

import li.cil.oc.api.nanomachines.DisableReason;
import li.cil.oc.api.prefab.AbstractBehavior;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
		if(reason == DisableReason.OutOfEnergy) {
			entity.setAttackTarget(player);
			entity.setPlayer(null);
			entity.setAdditionalLifespan(5 * 20);
		} else if(entity != null) {
			//entityTag = new NBTTagCompound();
			//entity.writeToNBT(entityTag);
			entity.setDead();
			entity = null;
		}
	}

	public void spawnNewEntity(double x, double y, double z){
		spawnNewEntity(x, y, z, 0xF0F000);
	}

	public void spawnNewEntity(double x, double y, double z, int color){
		entity = new EntitySwarm(player.worldObj);
		//if(entityTag != null) {
		//	entity.readFromNBT(entityTag);
		//}
		//TODO entity.setAmplifier(Nanomachines.getController(player).getInputCount(this));
		entity.setAmplifier(1); //TODO remove
		entity.setColor(color);
		entity.setPlayer(player);
		entity.setPosition(x, y, z);
		player.worldObj.spawnEntityInWorld(entity);
	}

	@Override
	public void update() {
		if(entity == null) {
			//spawnNewEntity(player.posX, player.posY + 2f, player.posZ);
		} else {
			if(entity.isDead) {
				entity = null;
			}
			//TODO entity.setAmplifier(Nanomachines.getController(player).getInputCount(this));
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		//entityTag = tag.getCompoundTag("computronics:swarm");
		if(entity != null) {
			//entity.readFromNBT(entityTag);
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		if(entity != null) {
			//entityTag = new NBTTagCompound();
			//entity.writeToNBT(entityTag);
		}
		//tag.setTag("computronics:swarm", entityTag);
	}
}
