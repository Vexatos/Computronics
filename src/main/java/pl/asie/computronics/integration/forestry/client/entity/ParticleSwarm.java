package pl.asie.computronics.integration.forestry.client.entity;

import forestry.apiculture.ModuleApiculture;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Extension of Forestry's bee particles
 */
public class ParticleSwarm extends Particle {

	public ParticleSwarm(World world, double x, double y, double z, int color) {
		super(world, x, y, z, 0D, 0D, 0D);
		setParticleTexture(ModuleApiculture.getBeeSprite());

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

		particleRed = (color >> 16 & 255) / 255.0F;
		particleGreen = (color >> 8 & 255) / 255.0F;
		particleBlue = (color & 255) / 255.0F;

		this.motionX *= 0.12D;
		this.motionY *= 0.12D;
		this.motionZ *= 0.12D;

		this.setSize(0.1F, 0.1F);
		this.particleScale *= 0.2F;
		this.particleMaxAge = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

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
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 1.08D;
		this.motionY *= 1.08D;
		this.motionZ *= 1.08D;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}
		//super.onUpdate();
	}

	/**
	 * Renders the particle
	 */
	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		if(this.particleTexture != null) {
			minU = particleTexture.getMinU();
			maxU = particleTexture.getMaxU();
			minV = particleTexture.getMinV();
			maxV = particleTexture.getMaxV();
		}

		float f10 = 0.1F * particleScale;
		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		worldRendererIn.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(maxU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(maxU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(minU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(minU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
	}

	// avoid calculating lighting for bees, it is too much processing
	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		return 15728880;
	}

	// avoid calculating collisions
	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		this.resetPositionToBB();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
