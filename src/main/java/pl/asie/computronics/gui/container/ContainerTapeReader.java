package pl.asie.computronics.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import pl.asie.computronics.Computronics;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.util.SlotTyped;

public class ContainerTapeReader extends ContainerBase {

	public ContainerTapeReader(TileEntityBase entity,
		InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped((IInventory) entity, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
