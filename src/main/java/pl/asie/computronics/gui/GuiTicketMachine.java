package pl.asie.computronics.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.tile.TileTicketMachine;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class GuiTicketMachine extends GuiBase {

	private static final int BUTTON_START_X = 48;
	private static final int BUTTON_START_Y = 58;
	private Button buttonMouse;
	private boolean isLocked = true;

	public enum Button {
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
		super.mouseClicked(x, y, mb);
		if(mb == 0) {
			for(Button button : Button.values()) {
				int button_x = this.xCenter + BUTTON_START_X + (button.ordinal() * 20);
				int button_y = this.yCenter + BUTTON_START_Y;
				if(x >= button_x && x < (button_x + 20) && y >= button_y && y < (button_y + 15)) {
					if(!isButtonPressed(button)) {
						buttonMouse = button;
					}
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(int x, int y, int which) {
		super.mouseMovedOrUp(x, y, which);
		if(which >= 0 && buttonMouse != null) {
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			handleButtonPress(buttonMouse);
			buttonMouse = null;
		}
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
	}
}
