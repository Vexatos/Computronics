package pl.asie.computronics.integration.forestry.client.entity;

import forestry.apiculture.entities.ParticleBee;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Extension of EntityBeeFX
 */
public class ParticleSwarm extends ParticleBee {

	public ParticleSwarm(World world, double x, double y, double z, int color) {
		super(world, x, y, z, color, new BlockPos(x, y, z));

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
			this.setExpired();
		}
		//super.onUpdate();
	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}
}
