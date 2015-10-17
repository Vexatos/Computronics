package pl.asie.computronics.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRadar extends ModelBase {
	public ModelRenderer Pole;
	public ModelRenderer Core;
	public ModelRenderer LeftWing;
	public ModelRenderer RightWing;
	public ModelRenderer ReceiverPole;
	public ModelRenderer Receiver;

	public ModelRadar() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.Receiver = new ModelRenderer(this, 14, 2);
		this.Receiver.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.Receiver.addBox(-0.5F, 1.5F, 0.0F, 1, 1, 1, 0.0F);
		this.ReceiverPole = new ModelRenderer(this, 5, 0);
		this.ReceiverPole.setRotationPoint(0.0F, -1.0F, -3.5F);
		this.ReceiverPole.addBox(-0.5F, 1.5F, 0.0F, 1, 1, 3, 0.0F);
		this.Core = new ModelRenderer(this, 16, 8);
		this.Core.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.Core.addBox(-3.5F, -4.0F, -1.2F, 7, 6, 1, 0.0F);
		this.setRotateAngle(Core, -0.2617993877991494F, 0.0F, 0.0F);
		this.LeftWing = new ModelRenderer(this, 20, 0);
		this.LeftWing.setRotationPoint(3.6F, -4.0F, 0.2F);
		this.LeftWing.addBox(0.1F, -6.0F, 0.4F, 4, 6, 1, 0.0F);
		this.setRotateAngle(LeftWing, 3.141592653589793F, 0.5235987755982988F, 0.0F);
		this.RightWing = new ModelRenderer(this, 5, 8);
		this.RightWing.setRotationPoint(-3.6F, -4.0F, 0.2F);
		this.RightWing.addBox(0.1F, 0.0F, 0.4F, 4, 6, 1, 0.0F);
		this.setRotateAngle(RightWing, 0.0F, 2.6179938779914944F, 0.0F);
		this.Pole = new ModelRenderer(this, 0, 0);
		this.Pole.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.Pole.addBox(-0.5F, 0.0F, -0.5F, 1, 16, 1, 0.0F);
		this.setRotateAngle(Pole, 0.0F, -4.960524086056721E-16F, 0.0F);
		this.ReceiverPole.addChild(this.Receiver);
		this.Core.addChild(this.ReceiverPole);
		this.Core.addChild(this.LeftWing);
		this.Core.addChild(this.RightWing);
	}

	public void render(float degrees) {
		this.Pole.render(1 / 16f);
		this.Core.rotateAngleY = (degrees * (float) Math.PI / 180F) % 360;
		this.Core.render(1 / 16f);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
