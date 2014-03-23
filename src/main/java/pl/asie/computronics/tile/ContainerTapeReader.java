package pl.asie.computronics.tile;

import net.minecraft.entity.player.InventoryPlayer;
import pl.asie.computronics.Computronics;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.util.SlotTyped;

public class ContainerTapeReader extends ContainerBase {

	public ContainerTapeReader(TileEntityInventory entity,
			InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped(entity, 0, 80, 34, new Object[]{Computronics.instance.itemTape}));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
