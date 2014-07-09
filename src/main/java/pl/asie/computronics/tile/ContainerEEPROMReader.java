package pl.asie.computronics.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import pl.asie.computronics.Computronics;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.util.SlotTyped;

public class ContainerEEPROMReader extends ContainerBase {
	public ContainerEEPROMReader(TileEntityInventory entity,
			InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped(entity, 0, 80, 34, new Object[]{GameRegistry.findItem("nedocomputers", "EEPROM")}));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}
}
