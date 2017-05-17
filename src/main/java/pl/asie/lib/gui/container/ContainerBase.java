package pl.asie.lib.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import pl.asie.lib.tile.TileEntityBase;

public abstract class ContainerBase extends ContainerInventory {

	private final TileEntityBase entity;

	public ContainerBase(TileEntityBase entity, InventoryPlayer inventoryPlayer) {
		super(entity instanceof IInventory ? ((IInventory) entity) : null);
		this.entity = entity;

		entity.openInventory();
	}

	public TileEntityBase getEntity() {
		return entity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.entity.isUsableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		this.entity.closeInventory();
	}
}
