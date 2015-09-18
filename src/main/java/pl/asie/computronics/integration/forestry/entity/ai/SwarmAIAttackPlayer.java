package pl.asie.computronics.integration.forestry.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;

/**
 * @author Vexatos
 */
public class SwarmAIAttackPlayer extends EntityAIAttackOnCollide {
	public SwarmAIAttackPlayer(EntityCreature entity, Class targetClass, double speed, boolean longMemory) {
		super(entity, targetClass, speed, longMemory);
	}

	public SwarmAIAttackPlayer(EntityCreature entity, double speed, boolean longMemory) {
		super(entity, speed, longMemory);
	}
}
