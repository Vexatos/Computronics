package pl.asie.computronics.integration.forestry.client.entity;

import forestry.apiculture.render.EntityBeeFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import pl.asie.computronics.integration.forestry.client.SwarmTextureHandler;

/**
 * Extension of EntityBeeFX
 */
public class EntitySwarmBeeFX extends EntityBeeFX {
	public EntitySwarmBeeFX(World world, double x, double y, double z, int color) {
		super(world, x, y, z, 0f, 0f, 0f, color);
		setParticleIcon(SwarmTextureHandler.Textures.BEE_FX.getIcon());
	}

	// TODO Make sure this is still valid in Forestry 4
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 1.08D;
		this.motionY *= 1.08D;
		this.motionZ *= 1.08D;
		if(this.particleMaxAge-- <= 0) {
			setDead();
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
		super.renderParticle(tessellator, f, f1, f2, f3, f4, f5);
	}
}
