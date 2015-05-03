package pl.asie.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class ProgressBarWidget extends Widget {
	private final TileTicketMachine tile;

	public ProgressBarWidget(TileTicketMachine tile, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.tile = tile;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, this.u, this.v, this.w, getProgress());
	}

	public int getProgress() {
		int p = tile.getProgress();
		if(p <= 0) {
			return 0;
		}
		int n = (int) ((double) this.h * ((double) p / (double) tile.getMaxProgress()));
		return Math.min(n <= 0 ? n : n + 1, this.h);
	}
}
