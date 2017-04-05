package pl.asie.computronics.integration.forestry.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSwarm extends RenderLiving<EntitySwarm> {

	public RenderSwarm() {
		super(Minecraft.getMinecraft().getRenderManager(), new ModelBase(){
		}, 0.15f);
	}

	@Override
	public void doRender(EntitySwarm par1EntityLiving, double x, double y, double z, float entityYaw, float partialTicks) {
		// NO-OP
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		// NO-OP
	}

	private static final ResourceLocation resource = new ResourceLocation("forestry:bees/honeyBee");

	@Override
	protected ResourceLocation getEntityTexture(EntitySwarm entity) {
		return resource;
	}

	public static class Factory implements IRenderFactory<EntitySwarm> {

		@Override
		public Render<? super EntitySwarm> createRenderFor(RenderManager manager) {
			return new RenderSwarm();
		}

	}
}
