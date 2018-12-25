package pl.asie.computronics.integration.railcraft.gui.container;

import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.gui.widgets.AnalogWidget;
import mods.railcraft.common.gui.widgets.ChargeNetworkIndicator;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.integration.railcraft.gui.slot.PaperSlotFilter;
import pl.asie.computronics.integration.railcraft.gui.slot.SlotSecureInput;
import pl.asie.computronics.integration.railcraft.gui.widget.LockButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.PrintButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.ProgressBarWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.SlotSelectionWidget;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.reference.Config;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class ContainerTicketMachine extends RailcraftContainer {

	private final InventoryPlayer inventoryPlayer;
	private TileTicketMachine tile;
	private boolean maintenanceMode = false;

	private boolean isSelectLocked = true;

	public ContainerTicketMachine(InventoryPlayer inventoryPlayer, TileTicketMachine tile, boolean maintenanceMode) {
		super(inventoryPlayer);
		this.inventoryPlayer = inventoryPlayer;
		this.tile = tile;
		this.maintenanceMode = maintenanceMode;
		this.addWidget(new PrintButtonWidget(tile, 67, 54, 0, 168, 20, 16));
		this.addWidget(new SlotSelectionWidget(tile, 33, 15, 184, 0, 88, 34, maintenanceMode));

		if(Config.TICKET_MACHINE_CONSUME_CHARGE) {
			this.addWidget(new Widget(92, 54, 178, 40, 30, 16));
			this.addWidget(new AnalogWidget(new ChargeNetworkIndicator(tile.getWorld(), tile.getPos()), 93, 55, 28, 14, 92+13, 54+12, 178+13, 40+12));
		}
		this.addWidget(new ProgressBarWidget(tile, 136, 34, 208, 25, 10, 13));
		if(maintenanceMode) {
			this.addWidget(new LockButtonWidget(tile, 6, 6, 224, 0, 16, 16, true));
		} else {
			this.addWidget(new LockButtonWidget(tile, 6, 6, 224, 0, 16, 16, false));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlot(new SlotSecure(ItemTicketGold.FILTER, tile, i, 33 + (i * 18), 15));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlot(new SlotSecure(ItemTicketGold.FILTER, tile, i + 5, 33 + (i * 18), 33));
		}
		this.addSlot(new SlotSecureInput(PaperSlotFilter.FILTER, tile, 10, 133, 15));
		this.addSlot(new SlotOutput(tile, 11, 133, 54));

		int j;
		for(j = 0; j < 3; ++j) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inventoryPlayer, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for(j = 0; j < 9; ++j) {
			this.addSlot(new Slot(inventoryPlayer, j, 8 + j * 18, 142));
		}
		setTicketsAndPaperLocked(!maintenanceMode());
	}

	public boolean maintenanceMode() {
		return this.maintenanceMode;
	}

	@Nullable
	@Override
	public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
		if(!maintenanceMode()) {
			setTicketsAndPaperLocked(true);
		} else {
			setTicketsAndPaperLocked(false);
		}
		return super.slotClick(slotId, mouseButton, clickType, player);
	}

	public void setTicketsAndPaperLocked(boolean locked) {
		if(isSelectLocked == locked) {
			return;
		}
		for(int i = 0; i <= 10; i++) {
			Object slot = this.inventorySlots.get(i);
			if(slot instanceof SlotSecure) {
				((SlotSecure) slot).locked = locked;
			}
		}
		isSelectLocked = locked;
	}

	public InventoryPlayer getInventoryPlayer() {
		return this.inventoryPlayer;
	}

	private void updateLock() {
		for(Widget widget : getWidgets()) {
			if(widget instanceof LockButtonWidget) {
				((LockButtonWidget) widget).accessible = this.maintenanceMode() && this.canLock;
			}
		}
	}

	//For synchronizing
	public boolean canLock;
	private int lastEnergy;
	private int lastProgress;

	@Override
	public void sendUpdateToClient() {
		super.sendUpdateToClient();
		for(IContainerListener listener : this.listeners) {
			if(this.lastProgress != tile.getProgress()) {
				listener.sendWindowProperty(this, 1, tile.getProgress());
			}
		}
		this.lastProgress = this.tile.getProgress();
		this.lastEnergy = tile.getEnergyStored(null);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		this.canLock = PlayerPlugin.isOwnerOrOp(tile.getOwner(), inventoryPlayer.player.getGameProfile());
		updateLock();
		listener.sendWindowProperty(this, 0, this.canLock ? 1 : 0);
		listener.sendWindowProperty(this, 1, tile.getProgress());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		switch(id) {
			case 0: {
				this.canLock = value == 1;
				updateLock();
				break;
			}
			case 1: {
				this.tile.setProgress(value);
				break;
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tile.isUsableByPlayer(playerIn);
	}
}
