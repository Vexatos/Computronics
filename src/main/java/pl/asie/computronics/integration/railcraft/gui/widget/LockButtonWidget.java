package pl.asie.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import pl.asie.computronics.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class LockButtonWidget extends ButtonWidget {
	private final TileTicketMachine tile;

	public LockButtonWidget(TileTicketMachine tile, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.tile = tile;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		int vv = this.pressed ? this.v + this.h : this.v;
		int uu = tile.isLocked() ? this.u + this.w : this.u;
		gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, uu, vv, this.w, this.h);
	}

	@Override
	public void onPress(int mouseButton) {
		super.onPress(mouseButton);
		this.tile.setLocked(!this.tile.isLocked());
	}
}
