package pl.asie.computronics.integration.forestry.client.entity;

import forestry.apiculture.entities.EntityFXBee;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import pl.asie.computronics.integration.forestry.client.SwarmTextureHandler;

/**
 * Extension of EntityBeeFX
 */
public class EntitySwarmBeeFX extends EntityFXBee {
	public EntitySwarmBeeFX(World world, double x, double y, double z, int color) {
		super(world, x, y, z, color);
		setParticleIcon(SwarmTextureHandler.Textures.BEE_FX.getIcon());

		//From EntityFX.java
		this.motionX = (Math.random() * 2.0D - 1.0D) * 0.4D;
		this.motionY = (Math.random() * 2.0D - 1.0D) * 0.4D;
		this.motionZ = (Math.random() * 2.0D - 1.0D) * 0.4D;
		double modifier = (Math.random() + Math.random() + 1.0D) * 0.15F;
		double motionDelta = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = this.motionX / motionDelta * modifier * 0.4D;
		this.motionY = this.motionY / motionDelta * modifier * 0.4D + 0.1D;
		this.motionZ = this.motionZ / motionDelta * modifier * 0.4D;
		//End

		this.motionX *= 0.12D;
		this.motionY *= 0.12D;
		this.motionZ *= 0.12D;

		/*this.motionX = this.motionX / 0.2D * 0.12D;
		this.motionY = this.motionY / 0.015D * 0.12D;
		this.motionZ = this.motionZ / 0.2D * 0.12D;*/
		/*this.motionX *= 0.2D;
		this.motionY *= 0.015D;
		this.motionZ *= 0.2D;*/
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 1.08D;
		this.motionY *= 1.08D;
		this.motionZ *= 1.08D;
		if(this.particleAge++ >= this.particleMaxAge) {
			setDead();
		}
		//super.onUpdate();
	}

	@Override
	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
		super.renderParticle(tessellator, f, f1, f2, f3, f4, f5);
	}
}
