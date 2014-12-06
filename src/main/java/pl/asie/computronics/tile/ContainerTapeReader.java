package pl.asie.computronics.tile;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import pl.asie.computronics.Computronics;
import pl.asie.lib.gui.inventory.ContainerInventoryBase;
import pl.asie.lib.util.SlotTyped;

public class ContainerTapeReader extends ContainerInventoryBase {

	public ContainerTapeReader(IInventory inventory,
		InventoryPlayer inventoryPlayer) {
		super(inventory, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped(inventory, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
