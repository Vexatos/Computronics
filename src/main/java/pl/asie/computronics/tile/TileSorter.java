package pl.asie.computronics.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import pl.asie.computronics.util.MiscCUtils;
import pl.asie.lib.util.ItemUtils;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileSorter extends TileEntityPeripheralInventory implements ISidedInventory {
	private int TICKS_SINCE_UPDATE = 0;
	
	public TileSorter() {
		super("sorter");
	};
	
	@Override
	public boolean canUpdate() { return true; }
	@Override
	public void updateEntity() {
		TICKS_SINCE_UPDATE++;
		// Pop out items which were not sorted for more than 5 seconds.
		// This way, prevent clogging.
		if(TICKS_SINCE_UPDATE >= 100 && this.getStackInSlot(0) != null) {
			ItemUtils.dropItem(worldObj, xCoord, yCoord, zCoord, this.getStackInSlot(0));
			this.setInventorySlotContents(0, null);
			TICKS_SINCE_UPDATE = 0;
		}
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public void onInventoryUpdate(int arg0) {
		ItemStack is = this.getStackInSlot(0);
		if(is != null) {
			if(Loader.isModLoaded("OpenComputers")) emitEventOC(is);
			if(Loader.isModLoaded("ComputerCraft")) emitEventCC(is);
		}
	}
	
	@Optional.Method(modid = "OpenComputers")
	public void emitEventOC(ItemStack is) {
		if(node != null)
			this.node.sendToReachable("computer.signal", "item_entered",
					MiscCUtils.getHashForStack(is, false),
					is.getItemDamage(),
					is.stackSize);
	}
	
	@Optional.Method(modid = "ComputerCraft")
	public void emitEventCC(ItemStack is) {
		for(IComputerAccess c: attachedComputersCC) {
			c.queueEvent("item_entered", new Object[]{
					MiscCUtils.getHashForStack(is, false),
					is.getItemDamage(),
					is.stackSize
			});
		}
	}

	@Override
	public String[] getMethodNames() {
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short busRead(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {
		// TODO Auto-generated method stub

	}
	
	// SidedInventory code
	// We will be inserting items into pipes ourselves, so only allow insertion
	// into the sorter from the sides and bottom.
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side != 1) return new int[]{0};
		else return new int[]{};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, int side) {
		return (side != 1 && this.getStackInSlot(0) == null);
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return false;
	}
}
