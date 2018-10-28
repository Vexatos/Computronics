package pl.asie.computronics.integration.railcraft.gui;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.entity.player.InventoryPlayer;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.integration.railcraft.gui.tooltip.ToolTips;
import pl.asie.computronics.integration.railcraft.gui.widget.ButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.LockButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.PrintButtonWidget;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.util.StringUtil;

/**
 * @author CovertJaguar, Vexatos
 */
public class GuiTicketMachine extends GuiContainerRailcraft {

	private final TileTicketMachine tile;

	private boolean maintenanceMode = false;

	private String ownerName = "[Unknown]";
	private ToolTip lockedToolTips;
	private ToolTip unlockedToolTips;
	private ToolTip notownedToolTips;
	private ToolTip notmaintenanceToolTips;
	private static final ToolTip printToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.print", 0);
	private static final ToolTip printLockedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.printLocked", 0);

	public GuiTicketMachine(InventoryPlayer inventory, TileTicketMachine tile, boolean maintenanceMode) {
		super(new ContainerTicketMachine(inventory, tile, maintenanceMode), "computronics:textures/gui/ticket_machine.png");
		this.tile = tile;
		this.maintenanceMode = maintenanceMode;
		this.ownerName = tile.getOwner().getName();
		this.lockedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.locked", 500, "{owner}=" + this.ownerName);
		this.unlockedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.unlocked", 500, "{owner}=" + this.ownerName);
		this.notownedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.notowner", 500, "{owner}=" + this.ownerName);
		this.notmaintenanceToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.notmaintenance", 500, "{owner}=" + this.ownerName);
	}

	public boolean maintenanceMode() {
		return maintenanceMode;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRenderer.drawString(
			StringUtil.localize("container.inventory"),
			8, ySize - 96 + 2, 0x404040);
		String name = StringUtil.localize(tile.getLocalizationTag());
		fontRenderer.drawString(
			name,
			(xSize / 2) - (fontRenderer.getStringWidth(name) / 2), 4, 0x404040);
	}

	@Override
	protected void mouseClickMove(int x, int y, int mouseButton, long time) {
		super.mouseClickMove(x, y, mouseButton, time);
		int mX = x - this.guiLeft;
		int mY = y - this.guiTop;
		for(Widget widget : container.getWidgets()) {
			if(widget instanceof ButtonWidget) {
				((ButtonWidget) widget).handleMouseMove(mX, mY, mouseButton, time);
			}
		}
	}

	@Override
	protected void mouseReleased(int x, int y, int eventType) {
		super.mouseReleased(x, y, eventType);
		int mX = x - this.guiLeft;
		int mY = y - this.guiTop;
		for(Widget widget : container.getWidgets()) {
			if(widget instanceof ButtonWidget) {
				((ButtonWidget) widget).handleMouseRelease(mX, mY, eventType);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.updateButtons();
	}

	private void updateButtons() {
		String username = tile.getOwner().getName();
		if(username != null && !username.equals(this.ownerName)) {
			this.ownerName = username;
			this.lockedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.locked", 500, "{owner}=" + this.ownerName);
			this.unlockedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.unlocked", 500, "{owner}=" + this.ownerName);
			this.notownedToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.notowner", 500, "{owner}=" + this.ownerName);
			this.notmaintenanceToolTips = ToolTips.buildToolTip("tooltip.computronics.ticket.notmaintenance", 500, "{owner}=" + this.ownerName);
		}

		for(Widget widget : container.getWidgets()) {
			if(widget instanceof LockButtonWidget) {
				if(((LockButtonWidget) widget).accessible) {
					((LockButtonWidget) widget).setToolTip(tile.isLocked() ? this.lockedToolTips : this.unlockedToolTips);
				} else {
					((LockButtonWidget) widget).setToolTip(((ContainerTicketMachine) container).canLock
						? this.notmaintenanceToolTips
						: this.notownedToolTips);
				}
			} else if(widget instanceof PrintButtonWidget) {
				((PrintButtonWidget) widget).setToolTip(tile.isPrintLocked() ? printLockedToolTips : printToolTips);
			}
		}
	}

}
