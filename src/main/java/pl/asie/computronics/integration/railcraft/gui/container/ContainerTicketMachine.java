package pl.asie.computronics.integration.railcraft.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.railcraft.gui.slot.PaperSlotFilter;
import pl.asie.computronics.integration.railcraft.gui.slot.SlotSecureInput;
import pl.asie.computronics.integration.railcraft.gui.widget.LockButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.PrintButtonWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.ProgressBarWidget;
import pl.asie.computronics.integration.railcraft.gui.widget.SlotSelectionWidget;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.reference.Config;

/**
 * @author Vexatos
 */
public class ContainerTicketMachine extends RailcraftContainer {

	private final InventoryPlayer inventoryPlayer;
	private final RFEnergyIndicator energyIndicator;
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

		this.energyIndicator = new RFEnergyIndicator(tile);
		if(Config.TICKET_MACHINE_CONSUME_RF) {
			this.addWidget(new Widget(160, 14, 184, 25, 8, 50));
			this.addWidget(new IndicatorWidget(this.energyIndicator, 161, 15, 194, 26, 6, 48));
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

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
		if(!maintenanceMode()) {
			setTicketsAndPaperLocked(true);
		} else {
			setTicketsAndPaperLocked(false);
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
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
		for(Widget widget : getElements()) {
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
		for(Object crafter : this.crafters) {
			if(crafter instanceof ICrafting) {
				if(this.lastEnergy != tile.getEnergyStored(ForgeDirection.UNKNOWN)) {
					((ICrafting) crafter).sendProgressBarUpdate(this, 2, tile.getEnergyStored(ForgeDirection.UNKNOWN));
				}
				if(this.lastProgress != tile.getProgress()) {
					((ICrafting) crafter).sendProgressBarUpdate(this, 3, tile.getProgress());
				}
			}
		}
		this.lastProgress = this.tile.getProgress();
		this.lastEnergy = tile.getEnergyStored(ForgeDirection.UNKNOWN);
	}

	@Override
	public void addCraftingToCrafters(ICrafting player) {
		super.addCraftingToCrafters(player);
		this.canLock = PlayerPlugin.isOwnerOrOp(tile.getOwner(), inventoryPlayer.player.getGameProfile());
		updateLock();
		player.sendProgressBarUpdate(this, 0, this.canLock ? 1 : 0);
		player.sendProgressBarUpdate(this, 2, this.tile.getEnergyStored(ForgeDirection.UNKNOWN));
		player.sendProgressBarUpdate(this, 3, tile.getProgress());
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
				this.energyIndicator.updateEnergy(value);
				break;
			}
			case 2: {
				this.energyIndicator.setEnergy(value);
				break;
			}
			case 3: {
				this.tile.setProgress(value);
				break;
			}
		}
	}
}
