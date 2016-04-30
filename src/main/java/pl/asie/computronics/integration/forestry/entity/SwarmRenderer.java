package pl.asie.computronics.integration.forestry.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SwarmRenderer extends RenderLiving {
	public SwarmRenderer() {
		super(null, 0.0F);
	}

	@Override
	public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
	}

	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
	}

	private static final ResourceLocation resource = new ResourceLocation("forestry:bees/honeyBee");

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return resource;
	}
}
