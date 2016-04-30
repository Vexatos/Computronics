package pl.asie.computronics.integration.railcraft.gui.widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.gui.GuiContainerRailcraft;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class LockButtonWidget extends ButtonWidget {
	private final TileTicketMachine tile;
	public boolean accessible = true;

	public LockButtonWidget(TileTicketMachine tile, int x, int y, int u, int v, int w, int h, boolean accessible) {
		super(x, y, u, v, w, h);
		this.tile = tile;
		this.accessible = accessible;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		if(this.accessible) {
			int vv = this.pressed ? this.v + this.h : this.v;
			vv += this.h;
			int uu = tile.isLocked() ? this.u + this.w : this.u;
			gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, uu, vv, this.w, this.h);
		} else {
			gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, tile.isLocked() ? this.u + this.w : this.u, v, this.w, this.h);
		}
	}

	@Override
	public void onPress(int mouseButton) {
		super.onPress(mouseButton);
		this.tile.setLocked(!this.tile.isLocked());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return this.accessible && super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if(this.accessible) {
			super.handleMouseRelease(mouseX, mouseY, eventType);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if(this.accessible) {
			super.handleMouseMove(mouseX, mouseY, mouseButton, time);
		}
	}
}
