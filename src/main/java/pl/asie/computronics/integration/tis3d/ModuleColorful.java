package pl.asie.computronics.integration.tis3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.Casing;
import li.cil.tis3d.api.Face;
import li.cil.tis3d.api.Pipe;
import li.cil.tis3d.api.Port;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author Vexatos
 */
public class ModuleColorful extends ComputronicsModule {

	private int color = 0x6318;

	public ModuleColorful(Casing casing, Face face) {
		super(casing, face);
	}

	@Override
	public void step() {
		super.step();
		for(Port port : Port.VALUES) {
			Pipe receivingPipe = this.getCasing().getReceivingPipe(this.getFace(), port);
			if(!receivingPipe.isReading()) {
				receivingPipe.beginRead();
			}
			if(receivingPipe.canTransfer()) {
				this.color = receivingPipe.read() & 0x7FFF;
				this.cancelWrite();
				receivingPipe.beginRead();
				sendData();
			}
		}
	}

	@Override
	public void onDisabled() {
		super.onDisabled();
		this.color = 0x6318;
		sendData();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.color = nbt.getInteger("c");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("c", this.color);
	}

	private static final ResourceLocation LAMP_ICON = new ResourceLocation("computronics:textures/blocks/lamp_layer_0.png");
	//private static final ResourceLocation front = new ResourceLocation("computronics:textures/blocks/lamp_layer_1.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void render(boolean enabled, float partialTicks) {
		if(!enabled) {
			return;
		}

		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);

		bindTexture(LAMP_ICON);
		GL11.glTranslated(0.0625F, 0.0625F, 0F);
		GL11.glScalef(0.875f, 0.875f, 0.875f);
		//int col = Color.HSBtoRGB((((System.currentTimeMillis() + (hashCode() % 30000)) % 30000) / 30000F), 1F, 1F) & 0xFFFFFF;
		//GL11.glColor3ub((byte) ((col >> 16) & 0xFF), (byte) ((col >> 8) & 0xFF), (byte) (col & 0xFF));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glColor3ub((byte) (((color >> 10) & 0x1F) * 8), (byte) (((color >> 5) & 0x1F) * 8), (byte) ((color & 0x1F) * 8));
		drawQuad();

		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		//bindTexture(front);
		//drawQuad();
	}
}
