package pl.asie.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.widgets.Widget;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class SlotSelectionWidget extends Widget {

	private List<SelectedSlotWidget> slots = new ArrayList<SelectedSlotWidget>(10);
	private final TileTicketMachine tile;
	//private boolean maintenanceMode;

	public SlotSelectionWidget(TileTicketMachine tile, int x, int y, int u, int v, int w, int h, boolean maintenanceMode) {
		super(x, y, u, v, w, h);
		this.tile = tile;
		//this.maintenanceMode = maintenanceMode;
		this.hidden = true;

		for(int i = 0; i < 5; i++) {
			slots.add(new SelectedSlotWidget(this, i, tile, x + (i * 18), y, 184, 0, 16, 16, maintenanceMode));
		}
		for(int i = 0; i < 5; i++) {
			slots.add(new SelectedSlotWidget(this, i + 5, tile, x + (i * 18), y + 18, 184, 0, 16, 16, maintenanceMode));
		}
	}

	@Override
	public void addToContainer(RailcraftContainer container) {
		super.addToContainer(container);

		for(SelectedSlotWidget slot : this.slots) {
			container.addWidget(slot);
		}

	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
	}
}
