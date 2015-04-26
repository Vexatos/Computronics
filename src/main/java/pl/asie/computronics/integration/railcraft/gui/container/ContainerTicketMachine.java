package pl.asie.computronics.integration.railcraft.gui.container;

import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.integration.railcraft.gui.slot.PaperSlotFilter;
import pl.asie.computronics.integration.railcraft.gui.slot.SlotSecureInput;
import pl.asie.computronics.integration.railcraft.gui.widget.LockButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.LockWidgetInaccessible;
import pl.asie.computronics.integration.railcraft.gui.widget.PrintButtonWidget;
import pl.asie.computronics.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class ContainerTicketMachine extends RailcraftContainer {

	private final InventoryPlayer inventoryPlayer;
	private TileTicketMachine tile;
	//private final RFEnergyIndicator energyIndicator;
	private boolean maintenanceMode = false;

	private boolean isPaperLocked = true;
	private boolean isSelectLocked = true;

	public ContainerTicketMachine(InventoryPlayer inventoryPlayer, TileTicketMachine tile, boolean maintenanceMode) {
		super(inventoryPlayer);
		this.inventoryPlayer = inventoryPlayer;
		this.tile = tile;
		this.maintenanceMode = maintenanceMode;
		//this.energyIndicator = new RFEnergyIndicator(tile);
		//this.addWidget(new IndicatorWidget(this.energyIndicator, 157, 19, 176, 12, 6, 48));
		this.addWidget(new PrintButtonWidget(tile, 62, 58, 0, 168, 20, 16));
		if(maintenanceMode) {
			this.addWidget(new LockButtonWidget(tile, 6, 6, 224, 16, 16, 16));
		} else {
			this.addWidget(new LockWidgetInaccessible(tile, 6, 6, 224, 0, 16, 16));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlot(new SlotSecure(ItemTicketGold.FILTER, tile, i, 33 + (i * 18), 15));
		}
		for(int i = 0; i < 5; i++) {
			this.addSlot(new SlotSecure(ItemTicketGold.FILTER, tile, i + 5, 33 + (i * 18), 33));
		}
		this.addSlot(new SlotSecureInput(PaperSlotFilter.FILTER, tile, 10, 144, 15));
		this.addSlot(new SlotOutput(tile, 11, 144, 54));

		int j;
		for(j = 0; j < 3; ++j) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inventoryPlayer, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for(j = 0; j < 9; ++j) {
			this.addSlot(new Slot(inventoryPlayer, j, 8 + j * 18, 142));
		}
	}

	private boolean maintenanceMode() {
		return this.maintenanceMode;
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
		if(!tile.isOwner(player.getGameProfile())) {
			setTicketsLocked(true);
			setPaperLocked(true);
		} else {
			setTicketsLocked(false);
			setPaperLocked(false);
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	public void setTicketsLocked(boolean locked) {
		if(isSelectLocked == locked) {
			return;
		}
		for(int i = 0; i < 10; i++) {
			Object slot = this.inventorySlots.get(i);
			if(slot instanceof SlotSecure) {
				((SlotSecure) slot).locked = locked;
			}
		}
		isSelectLocked = locked;
	}

	public void setPaperLocked(boolean locked) {
		if(isPaperLocked == locked) {
			return;
		}
		Object slot = this.inventorySlots.get(10);
		if(slot instanceof SlotSecureInput) {
			((SlotSecureInput) slot).locked = locked;
		}
		isPaperLocked = locked;
	}

	public InventoryPlayer getInventoryPlayer() {
		return this.inventoryPlayer;
	}
}
