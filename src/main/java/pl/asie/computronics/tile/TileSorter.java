package pl.asie.computronics.tile;

public class TileSorter {
}
/*
extends TileEntityPeripheralInventory implements ISidedInventory {
}
	private int TICKS_SINCE_UPDATE = 0;
	private boolean updatedOutputYet = false;
	private ArrayList<ISortingOutputHandler> sortingOutputHandlers;
	private ISortingOutputHandler sortingOutputHandler;
	
	public TileSorter() {
		super("sorter");
		
		sortingOutputHandlers = new ArrayList<ISortingOutputHandler>();
		
	};
	
	@Override
	public boolean canUpdate() { return true; }
	@Override
	public void updateEntity() {
		TICKS_SINCE_UPDATE++;
		if(!updatedOutputYet) { updatedOutputYet = true; updateOutput(); }
		// Pop out items which were not sorted for more than 5 seconds.
		// This way, prevent clogging.
		if(TICKS_SINCE_UPDATE >= 100 && this.getStackInSlot(0) != null) {
			ItemUtils.dropItem(worldObj, xCoord, yCoord, zCoord, this.getStackInSlot(0));
			this.setInventorySlotContents(0, null);
			TICKS_SINCE_UPDATE = 0;
		}
	}

	public void updateOutput() {
		// get TE on top
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
	
	// OpenComputers
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getItemName(Context ctx, Arguments args) {
		if(this.getStackInSlot(0) == null) return null;
		else return new Object[]{MiscCUtils.getHashForStack(this.getStackInSlot(0), false)};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getItemDamage(Context ctx, Arguments args) {
		if(this.getStackInSlot(0) == null) return null;
		else return new Object[]{this.getStackInSlot(0).getItemDamage()};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getStackSize(Context ctx, Arguments args) {
		if(this.getStackInSlot(0) == null) return null;
		else return new Object[]{this.getStackInSlot(0).stackSize};
	}
	// ComputerCraft

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
		addr &= 0xFFFE;
		if(addr < 8) {
			ItemStack is = this.getStackInSlot(0);
			if(is == null) return 0;
			switch(addr) {
			case 0: return (short)(MiscCUtils.getIntegerHashForStack(is, false) & 0xFFFF);
			case 2: return (short)(MiscCUtils.getIntegerHashForStack(is, false) >> 16);
			case 4: return (short)is.getItemDamage();
			case 6: return (short)is.stackSize;
			}
		}
		
		// catch-all
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {
		// TODO Auto-generated method stub

	}
	
	// SidedInventory code
	// We will be inserting items into pipes ourselves, so only allow insertion
	// into the sorter from the sides.
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side >= 2) return new int[]{0};
		else return new int[]{};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, int side) {
		return (side >= 2 && this.getStackInSlot(0) == null);
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return false;
	}
}*/
