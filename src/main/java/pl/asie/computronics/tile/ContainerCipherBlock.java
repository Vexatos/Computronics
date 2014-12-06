package pl.asie.computronics.tile;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import pl.asie.lib.gui.inventory.ContainerInventoryBase;

public class ContainerCipherBlock extends ContainerInventoryBase {

	public ContainerCipherBlock(IInventory inventory,
		InventoryPlayer inventoryPlayer) {
		super(inventory, inventoryPlayer);
		for(int i = 0; i < 6; i++) {
			this.addSlotToContainer(new Slot(inventory, i, 35 + (i * 18), 34));
		}
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
