package pl.asie.computronics.integration.tis3d.module;

import li.cil.tis3d.api.FontRendererAPI;
import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.machine.Pipe;
import li.cil.tis3d.api.machine.Port;
import li.cil.tis3d.api.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.boom.SelfDestruct;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class ModuleBoom extends ComputronicsModule {

	private short steps = -1;

	public ModuleBoom(Casing casing, Face face) {
		super(casing, face);
	}

	@Override
	public void step() {
		super.step();
		if(steps == 0) {
			//Bye bye.
			IntegrationTIS3D.boomHandler.queueBoom(getCasing());
		} else if(steps > 0) {
			--steps;
			//steps /= 2;
			sendDataToClient();
		}
		for(Port port : Port.VALUES) {
			Pipe receivingPipe = this.getCasing().getReceivingPipe(this.getFace(), port);
			if(!receivingPipe.isReading()) {
				receivingPipe.beginRead();
			}
			if(receivingPipe.canTransfer()) {
				short newstep = receivingPipe.read();
				if(newstep < 0) {
					return;
				}
				if(steps < 0 || newstep < steps) {
					steps = newstep;
					sendDataToClient();
				}
			}
		}
	}

	@Override
	public void onDisabled() {
		steps = -1;
		sendDataToClient();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("steps")) {
			this.steps = nbt.getShort("steps");
		} else {
			this.steps = -1;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.steps >= 0) {
			nbt.setShort("steps", this.steps);
		}
	}

	private static final ResourceLocation ICON = new ResourceLocation("computronics:textures/blocks/tis3d/module_boom.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void render(boolean enabled, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		int alpha = 0x44;
		final int maxAlpha = enabled && steps < 0 ? 0x88 : 0xFF;
		final int speed = steps < 0 ? 100 : 20;
		alpha = enabled ?
			0x44 + (int) ((maxAlpha - alpha) * ((Math.sin((getCasing().getCasingWorld().getTotalWorldTime() + (hashCode() % speed) + partialTicks) / ((double) speed) * 2D * Math.PI) + 1D) / 2D))
			: 0x44;

		RenderUtil.bindTexture(ICON);
		GlStateManager.color(1f, 0f, 0f, (alpha & 0xFF) / 255f);
		RenderUtil.drawQuad();
		GlStateManager.color(1, 1, 1, 1);

		if(!enabled) {
			return;
		}
		if(steps >= 0) {
			//String timer = String.format("%05d", steps);
			//String s = StringUtils.center(timer, 8);
			String s = String.format("%05d", steps);
			int scale = FontRendererAPI.getCharHeight();
			GlStateManager.translate(FontRendererAPI.getCharWidth() / 15f, 7 / 16f, 0);
			GlStateManager.scale(scale / 128f, scale / 128f, 1);

			FontRendererAPI.drawString(s);
		}
		GlStateManager.disableBlend();
	}

	public static class BoomHandler {

		private final List<Casing> boomQueue = new ArrayList<Casing>();

		@SubscribeEvent(priority = EventPriority.HIGH)
		@Optional.Method(modid = Mods.TIS3D)
		public void onServerTick(TickEvent.ServerTickEvent e) {
			if(e.phase != TickEvent.Phase.START || boomQueue.isEmpty()) {
				return;
			}
			for(Casing casing : boomQueue) {
				if(casing != null && casing.getCasingWorld() != null) {
					SelfDestruct.goBoom(casing.getCasingWorld(), casing.getPosition(), true);
				}
			}
			boomQueue.clear();
		}

		@Optional.Method(modid = Mods.TIS3D)
		public void queueBoom(Casing casing) {
			if(!boomQueue.contains(casing)) {
				boomQueue.add(casing);
			}
		}
	}
}
