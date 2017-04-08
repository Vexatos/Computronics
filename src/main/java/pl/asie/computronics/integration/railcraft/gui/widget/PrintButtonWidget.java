package pl.asie.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.network.PacketType;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class PrintButtonWidget extends ButtonWidget {
	private final TileTicketMachine tile;

	public PrintButtonWidget(TileTicketMachine tile, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.tile = tile;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		int uu = this.pressed ? this.u + this.w : this.u;
		gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, uu, this.v, this.w, this.h);
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return !tile.isPrintLocked() && super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onPress(int mouseButton) {
		super.onPress(mouseButton);
		if(tile.isPrintLocked()) {
			return;
		}
		try {
			Computronics.packet.sendToServer(Computronics.packet.create(PacketType.TICKET_PRINT.ordinal()).writeTileLocation(tile));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
