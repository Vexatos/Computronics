package pl.asie.computronics.integration.forestry.nanomachines;

import li.cil.oc.api.Nanomachines;
import li.cil.oc.api.nanomachines.DisableReason;
import li.cil.oc.api.prefab.AbstractBehavior;
import net.minecraft.entity.player.EntityPlayer;
import pl.asie.computronics.integration.forestry.entity.EntitySwarm;

/**
 * @author Vexatos
 */
public class SwarmBehavior extends AbstractBehavior {

	private EntitySwarm entity;

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
			entity.setDead();
		}
	}

	@Override
	public void update() {
		if(entity == null) {
			entity = new EntitySwarm(player);
			entity.amplifier = Nanomachines.getController(player).getInputCount(this) - 1;
			entity.posX = player.posX;
			entity.posY = player.posY + (player.getEyeHeight() / 2f);
			entity.posX = player.posX;
			player.worldObj.spawnEntityInWorld(entity);
		}
	}
}
