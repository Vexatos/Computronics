package pl.asie.computronics.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.StringUtil;
import pl.asie.lib.block.ContainerInventory;
import pl.asie.lib.gui.GuiSpecialContainer;

public class GuiTapePlayer extends GuiSpecialContainer<ContainerInventory> {

	private static final int BUTTON_START_X = 48;
	private static final int BUTTON_START_Y = 58;
	private State state = State.STOPPED;

	public enum Button {
		REWIND,
		PLAY,
		STOP,
		FAST_FORWARD
	}

	private Button buttonMouse = null;

	private final IGuiTapeDrive tile;

	public GuiTapePlayer(IGuiTapeDrive tile, ContainerInventory container) {
		super(container, "computronics:tape_player", 176, 166);
		this.tile = tile;
	}

	public boolean isButtonPressed(Button button) {
		if(button == buttonMouse) {
			return true;
		}
		switch(state) {
			case FORWARDING:
				return button == Button.FAST_FORWARD;
			case PLAYING:
				return button == Button.PLAY;
			case REWINDING:
				return button == Button.REWIND;
			case STOPPED:
			default:
				return false;
		}
	}

	public void setState(State state) {
		if(tile != null) {
			tile.setState(state);
		}
	}

	public void handleButtonPress(Button button) {
		switch(button) {
			case REWIND:
				if(state == State.REWINDING) {
					setState(State.STOPPED);
				} else {
					setState(State.REWINDING);
				}
				break;
			case PLAY:
				setState(State.PLAYING);
				break;
			case FAST_FORWARD:
				if(state == State.FORWARDING) {
					setState(State.STOPPED);
				} else {
					setState(State.FORWARDING);
				}
				break;
			case STOP:
				setState(State.STOPPED);
				break;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.state = tile.getState();
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
	protected void handleMouseClick(Slot slot, int index, int button, int shift) {
		if(slot == null || !tile.isLocked(slot, index, button, shift)) {
			super.handleMouseClick(slot, index, button, shift);
		}
	}

	@Override
	protected boolean checkHotbarKeys(int keyCode) {
		return tile.shouldCheckHotbarKeys() && super.checkHotbarKeys(keyCode);
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

	// Uses NBT data.
	private String getLabel() {
		ItemStack stack = (ItemStack) this.container.getInventory().get(0);
		if(stack != null && stack.getItem() instanceof ItemTape) {
			String label = StringUtil.localize("tooltip.computronics.tape.unnamed");
			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("label")) {
				label = stack.getTagCompound().getString("label");
			}
			return label;
		} else {
			return null;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		// Update state from TileEntity
		this.state = tile.getState();

		super.drawGuiContainerBackgroundLayer(f, i, j);

		// Draw buttons
		for(Button button : Button.values()) {
			int button_ty = 170 + (button.ordinal() * 15);
			int button_tx = isButtonPressed(button) ? 20 : 0;
			int button_x = BUTTON_START_X + (button.ordinal() * 20);
			this.drawTexturedModalRect(this.xCenter + button_x, this.yCenter + BUTTON_START_Y, button_tx, button_ty, 20, 15);
		}

		// Draw label
		String label = getLabel();

		int labelColor = 0xFFFFFF;
		if(label == null) {
			label = StringUtil.localize("tooltip.computronics.tape.none");
			labelColor = 0xFF3333;
		}
		if(label.length() > 24) {
			label = label.substring(0, 22) + "...";
		}
		this.drawCenteredString(this.fontRendererObj, label, this.xCenter + 88, this.yCenter + 15, labelColor);
	}
}
