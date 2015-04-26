package pl.asie.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import pl.asie.computronics.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class LockWidgetInaccessible extends Widget {
	private TileTicketMachine tile;

	public LockWidgetInaccessible(TileTicketMachine tile, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.tile = tile;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, tile.isLocked() ? this.u + this.w : this.u, v, this.w, this.h);
	}
}
