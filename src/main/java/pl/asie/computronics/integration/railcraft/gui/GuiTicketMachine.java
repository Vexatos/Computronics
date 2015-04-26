package pl.asie.computronics.integration.railcraft.gui;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.entity.player.InventoryPlayer;
import pl.asie.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import pl.asie.computronics.integration.railcraft.gui.widget.ButtonWidget;
import pl.asie.computronics.tile.TileTicketMachine;
import pl.asie.computronics.util.StringUtil;

/**
 * @author CovertJaguar, Vexatos
 */
public class GuiTicketMachine extends GuiContainerRailcraft {

	private final TileTicketMachine tile;

	private boolean isLocked = true;
	private boolean maintenanceMode = false;

	public GuiTicketMachine(InventoryPlayer inventory, TileTicketMachine tile, boolean maintenanceMode) {
		super(new ContainerTicketMachine(inventory, tile, maintenanceMode), "computronics:textures/gui/container/ticket_machine.png");
		this.tile = tile;
		this.maintenanceMode = maintenanceMode;
	}

	protected boolean maintenanceMode() {
		return maintenanceMode;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		this.fontRendererObj.drawString(StringUtil.localize("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
		int slot = tile.getSelectedSlot();
		drawTexturedModalRect(31 + (slot * 18), slot < 5 ? 13 : 31, 184, 0, 22, 22);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseClickMove(int x, int y, int mouseButton, long time) {
		super.mouseClickMove(x, y, mouseButton, time);
		int mX = x - this.guiLeft;
		int mY = y - this.guiTop;
		for(Widget widget : container.getElements()) {
			if(widget instanceof ButtonWidget) {
				((ButtonWidget) widget).handleMouseMove(mX, mY, mouseButton, time);
			}
		}
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int eventType) {
		super.mouseMovedOrUp(x, y, eventType);
		int mX = x - this.guiLeft;
		int mY = y - this.guiTop;
		for(Widget widget : container.getElements()) {
			if(widget instanceof ButtonWidget) {
				((ButtonWidget) widget).handleMouseRelease(mX, mY, eventType);
			}
		}
	}

	//private static final int BUTTON_START_X = 48;
	//private static final int BUTTON_START_Y = 58;
	//private Button buttonMouse;
	/*public enum Button {
		PRINT(),
		LOCK();

		private final int x;
		private final int y;
		private final int w;
		private final int h;

		private Button() {
			x = 0;
			y = 0;
			w = 0;
			h = 0;
		}

		private Button(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	public GuiTicketMachine(ContainerBase container) {
		super(container, "computronics:cipherblock", 176, 166);
	}

	protected boolean maintenanceMode() {
		return false;
	}

	private boolean isButtonPressed(Button button) {
		switch(button) {
			case LOCK: {
				return isLocked;
			}
		}
		return buttonMouse == button;
	}

	public void handleButtonPress(Button button) {
		switch(button) {
			case PRINT: {
				Computronics.log.debug("You have pressed the print button!");
				if(!((TileTicketMachine) container.getEntity()).isPrintLocked()) {
					Computronics.log.debug("And it worked!");
				}
				break;
			}
			case LOCK: {
				TileTicketMachine machine = (TileTicketMachine) container.getEntity();
				machine.setLocked(!machine.isLocked());
				int i = machine.isLocked() ? 1 : 0;
				i |= machine.isSelectionLocked() ? 1 << 1 : 0;
				i |= machine.isPrintLocked() ? 1 << 2 : 0;

				try {
					Packet packet = Computronics.packet.create(Packets.PACKET_TICKET_SYNC)
						.writeTileLocation(machine)
						.writeInt(i);
					Computronics.packet.sendToServer(packet);
				} catch(IOException e) {
					//NO-OP
				}
			}
		}
	}

	@Override
	public void mouseClicked(int x, int y, int mb) {
		if(mb == 0) {
			for(Button button : Button.values()) {
				int button_x = this.xCenter + BUTTON_START_X + (button.ordinal() * 20);
				int button_y = this.yCenter + BUTTON_START_Y;
				if(x >= button_x && x < (button_x + 20) && y >= button_y && y < (button_y + 15)) {
					if(!isButtonPressed(button)) {
						buttonMouse = button;
						return;
					}
				}
			}
		}
		super.mouseClicked(x, y, mb);
	}

	@Override
	public void mouseMovedOrUp(int x, int y, int which) {
		if(which >= 0 && buttonMouse != null) {
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			handleButtonPress(buttonMouse);
			buttonMouse = null;
			return;
		}
		super.mouseMovedOrUp(x, y, which);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.isLocked = ((TileTicketMachine) this.container.getEntity()).isLocked();
		super.drawGuiContainerBackgroundLayer(f, i, j);
		for(Button button : Button.values()) {
			int button_ty = 170 + (button.ordinal() * 15);
			int button_tx = isButtonPressed(button) ? 20 : 0;
			int button_x = BUTTON_START_X + (button.ordinal() * 20);
			this.drawTexturedModalRect(this.xCenter + button_x, this.yCenter + BUTTON_START_Y, button_tx, button_ty, 20, 15);
		}
	}*/
}
