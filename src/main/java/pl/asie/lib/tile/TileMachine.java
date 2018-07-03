package pl.asie.lib.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import pl.asie.lib.api.tile.IBattery;

import javax.annotation.Nullable;
import java.util.Arrays;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic)
})*/
public class TileMachine extends TileEntityBase
	/*IConnectable, ISidedInventory, RedLogic */ {

	private IBattery battery;
	//private IBundledRedstoneProvider brP;

	public ItemHandler items = null;

	public TileMachine() {
	}

	public IBattery getBatteryProvider() {
		return battery;
	}

	/*public IBundledRedstoneProvider getBundledRedstoneProvider() {
		return brP;
	}

	protected void registerBundledRedstone(IBundledRedstoneProvider brP) {
		this.brP = brP;
	}*/

	protected void registerBattery(IBattery p) {
		this.battery = p;
	}

	protected void createInventory(int slots) {
		this.items = new ItemHandler(slots);
	}

	protected void createInventory(ItemHandler itemHandler) {
		this.items = itemHandler;
	}

	@Override
	public void validate() {
		super.validate();
	}

	public void update() {
		/*if(!didInitIC2) {
			if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
				this.initIC();
			}
			didInitIC2 = true; // Just so this check won't be done every tick.
		}*//*
		if(!didInitIC2C) {
			if(Mods.isLoaded(Mods.IC2Classic) && this.battery != null) {
				this.initICClassic();
			}
			didInitIC2C = true; // Just so this check won't be done every tick.
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
			this.deinitIC();
		}/*
		if(Mods.isLoaded(Mods.IC2Classic) && this.battery != null) {
			this.deinitICClassic();
		}*/
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		/*if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
			this.deinitIC();
		}*//*
		if(Mods.isLoaded(Mods.IC2Classic) && this.battery != null) {
			this.deinitICClassic();
		}*/
	}
	// (Bundled) Redstone

	/*@Optional.Method(modid = Mods.RedLogic)
	public boolean connects(IWire wire, int blockFace, int fromDirection) {
		if(wire instanceof IBareRedstoneWire && this.blockType != null
			&& ((BlockBase) this.blockType).emitsRedstone(world, xCoord, yCoord, zCoord, fromDirection)) {
			return true;
		} else if(wire instanceof IBundledWire) {
			if(this.brP != null) {
				return this.brP.canBundledConnectTo(fromDirection, blockFace);
			} else {
				return false;
			}
		}

		return false;
	}

	@Optional.Method(modid = Mods.RedLogic)
	public boolean connectsAroundCorner(IWire wire, int blockFace,
		int fromDirection) {
		return false;
	}

	@Optional.Method(modid = Mods.RedLogic)
	public void onBundledInputChanged() {
		if(this.brP != null) {
			for(int side = 0; side < 6; side++) {
				IBundledEmitter be = null;
				for(int face = -1; face < 6; face++) {
					if(this.brP.canBundledConnectTo(side, face)) {
						if(be == null) {
							ForgeDirection fd = ForgeDirection.values()[side];
							TileEntity t = world.getTileEntity(xCoord + fd.offsetX, yCoord + fd.offsetY, zCoord + fd.offsetZ);
							if(t != null && t instanceof IBundledEmitter) {
								be = (IBundledEmitter) t;
							} else {
								break;
							}
						}
						this.brP.onBundledInputChange(side, face, be.getBundledCableStrength(face, side));
					}
				}
			}
		}
	}

	@Optional.Method(modid = Mods.RedLogic)
	public byte[] getBundledCableStrength(int blockFace, int toDirection) {
		if(this.brP != null && this.brP.canBundledConnectTo(toDirection, blockFace)) {
			return this.brP.getBundledOutput(toDirection, blockFace);
		} else {
			return null;
		}
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public byte[] getBundledSignal(int side) {
		if(this.brP != null && this.brP.canBundledConnectTo(side, -1)) {
			return this.brP.getBundledOutput(side, -1);
		} else {
			return null;
		}
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public boolean canConnectBundled(int side) {
		return this.brP.canBundledConnectTo(side, -1);
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public void onProjectRedBundledInputChanged() {
		if(this.brP != null) {
			for(int i = 0; i < 6; i++) {
				if(!this.brP.canBundledConnectTo(i, -1)) {
					continue;
				}
				byte[] data = ProjectRedAPI.transmissionAPI.getBundledInput(world, xCoord, yCoord, zCoord, i);
				if(data != null) {
					this.brP.onBundledInputChange(i, -1, data);
				}
			}
		}
	}*/

	// Inventory

	public class ItemHandler implements IItemHandler {

		private ItemStack[] stacks;

		public ItemHandler(int slots) {
			this.stacks = new ItemStack[slots];
			Arrays.fill(this.stacks, ItemStack.EMPTY);
		}

		@Override
		public int getSlots() {
			return this.stacks.length;
		}

		public boolean isEmpty() {
			for(ItemStack item : this.stacks) {
				if(!item.isEmpty()) {
					return false;
				}
			}
			return true;
		}

		public void setStack(int slot, ItemStack stack) {
			if(slot >= 0 && slot < this.stacks.length) {
				this.stacks[slot] = stack;
				this.onSlotUpdate(slot);
			}
		}

		public void onSlotUpdate(int slot) {

		}

		public boolean canInsert(int slot, ItemStack stack) {
			return true;
		}

		public boolean canExtract(int slot, int amount) {
			return true;
		}

		public void clearInventory() {
			for(int i = 0; i < this.stacks.length; i++) {
				this.setStack(i, ItemStack.EMPTY);
			}
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if(slot >= 0 && slot < this.stacks.length) {
				return this.stacks[slot];
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if(canInsert(slot, stack) && slot >= 0 && slot < stacks.length) {
				if(stacks[slot].isEmpty()) {
					setStack(slot, stack.copy());
					return ItemStack.EMPTY;
				} else if(ItemHandlerHelper.canItemStacksStack(stacks[slot], stack)) {
					int toAdd = Math.min(stack.getCount(), Math.min(getSlotLimit(slot), stacks[slot].getMaxStackSize()) - stacks[slot].getCount());
					stacks[slot].grow(toAdd);
					stack.shrink(toAdd);
					this.onSlotUpdate(slot);
				}
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if(canExtract(slot, amount) && slot >= 0 && slot < stacks.length && !stacks[slot].isEmpty()) {
				ItemStack stack;
				if(stacks[slot].getCount() <= amount) {
					stack = stacks[slot];
					stacks[slot] = ItemStack.EMPTY;
					onSlotUpdate(slot);
					return stack;
				} else {
					stack = stacks[slot].splitStack(amount);

					if(stacks[slot].getCount() == 0) {
						stacks[slot] = ItemStack.EMPTY;
					}

					onSlotUpdate(slot);

					return stack;
				}
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}

		private void setStacks(ItemStack[] stacks) {
			this.stacks = stacks;
		}
	}

	public class DelegateItemHandler extends ItemHandler {

		protected final ItemHandler delegate;

		public DelegateItemHandler(ItemHandler delegate) {
			super(0);
			this.delegate = delegate;
		}

		@Override
		public int getSlots() {
			return delegate.getSlots();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public void setStack(int slot, ItemStack stack) {
			delegate.setStack(slot, stack);
		}

		@Override
		public void onSlotUpdate(int slot) {
			delegate.onSlotUpdate(slot);
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack) {
			return delegate.canInsert(slot, stack);
		}

		@Override
		public boolean canExtract(int slot, int amount) {
			return delegate.canExtract(slot, amount);
		}

		@Override
		public void clearInventory() {
			delegate.clearInventory();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return delegate.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return delegate.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return delegate.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return delegate.getSlotLimit(slot);
		}
	}

	// Energy (RF)

	public boolean canConnectEnergy(EnumFacing from) {
		if(this.battery != null) {
			return this.battery.canInsert(from, "RF");
		} else {
			return false;
		}
	}

	public int receiveEnergy(EnumFacing from, int maxReceive,
		boolean simulate) {
		if(this.battery != null && this.battery.canInsert(from, "RF")) {
			return (int) Math.floor(this.battery.insert(from, maxReceive, simulate));
		} else {
			return 0;
		}
	}

	public int extractEnergy(@Nullable EnumFacing from, int maxExtract,
		boolean simulate) {
		if(this.battery != null && this.battery.canExtract(from, "RF")) {
			return (int) Math.floor(this.battery.extract(from, maxExtract, simulate));
		} else {
			return 0;
		}
	}

	public int getEnergyStored(@Nullable EnumFacing from) {
		if(this.battery != null) {
			return (int) Math.floor(this.battery.getEnergyStored());
		} else {
			return 0;
		}
	}

	public int getMaxEnergyStored(@Nullable EnumFacing from) {
		if(this.battery != null) {
			return (int) Math.floor(this.battery.getMaxEnergyStored());
		} else {
			return 0;
		}
	}

	// Energy (EU - IC2 Experimental)

	private boolean didInitIC2 = false;

	/*@Optional.Method(modid = Mods.IC2)
	public void initIC() {
		if(!didInitIC2 && (world == null || !world.isRemote)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) this));
		}
		didInitIC2 = true;
	}

	@Optional.Method(modid = Mods.IC2)
	public void deinitIC() {
		if(didInitIC2 && (world == null || !world.isRemote)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) this));
		}
		didInitIC2 = false;
	}

	@Optional.Method(modid = Mods.IC2)
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
		if(this.battery != null) {
			return this.battery.canInsert(direction, "EU");
		} else {
			return false;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public double injectEnergy(EnumFacing directionFrom, double amount,
		double voltage) {
		if(this.battery != null) {
			double amountRF = EnergyConverter.convertEnergy(amount, "EU", "RF");
			double injectedRF = this.battery.insert(directionFrom, amountRF, false);
			return amount - EnergyConverter.convertEnergy(injectedRF, "RF", "EU");
		} else {
			return amount;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public double getDemandedEnergy() {
		if(this.battery != null) {
			return EnergyConverter.convertEnergy(battery.getMaxEnergyInserted(), "RF", "EU");
		} else {
			return 0.0;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public int getSinkTier() {
		return Integer.MAX_VALUE;
	}*/

	// Energy (EU - IC2 Classic)

	/*private boolean didInitIC2C = false;

	@Optional.Method(modid = Mods.IC2Classic)
	private void initICClassic() {
		if(!didInitIC2C) {
			MinecraftForge.EVENT_BUS.post(new ic2classic.api.energy.event.EnergyTileLoadEvent((ic2classic.api.energy.tile.IEnergyTile) this));
		}
		didInitIC2C = true;
	}

	@Optional.Method(modid = Mods.IC2Classic)
	private void deinitICClassic() {
		if(didInitIC2C) {
			MinecraftForge.EVENT_BUS.post(new ic2classic.api.energy.event.EnergyTileUnloadEvent((ic2classic.api.energy.tile.IEnergyTile) this));
		}
		didInitIC2C = false;
	}

	@Optional.Method(modid = Mods.IC2Classic)
	public boolean acceptsEnergyFrom(TileEntity arg0, Direction arg1) {
		if(this.battery != null) {
			return this.battery.canInsert(arg1.toSideValue(), "EU");
		} else {
			return false;
		}
	}

	@Optional.Method(modid = Mods.IC2Classic)
	public boolean isAddedToEnergyNet() {
		return didInitIC2C;
	}

	@Optional.Method(modid = Mods.IC2Classic)
	public int demandsEnergy() {
		if(this.battery != null) {
			return (int) Math.floor(EnergyConverter.convertEnergy(battery.getMaxEnergyInserted(), "RF", "EU"));
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.IC2Classic)
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	@Optional.Method(modid = Mods.IC2Classic)
	public int injectEnergy(Direction arg0, int amount) {
		if(this.battery != null) {
			double amountRF = EnergyConverter.convertEnergy(amount, "EU", "RF");
			double injectedRF = this.battery.insert(arg0.toSideValue(), amountRF, false);
			return amount - (int) Math.floor(EnergyConverter.convertEnergy(injectedRF, "RF", "EU"));
		} else {
			return amount;
		}
	}*/

	// NBT
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if(this.battery != null) {
			this.battery.readFromNBT(tagCompound);
		}
		if(this.items != null) {
			NBTTagList nbttaglist = tagCompound.getTagList("Inventory", 10);
			this.items.setStacks(new ItemStack[this.items.getSlots()]);

			for(int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 255;

				if(j >= 0 && j < this.items.stacks.length) {
					this.items.stacks[j] = new ItemStack(nbttagcompound1);
				}
			}
			for(int i = 0; i < items.stacks.length; i++) {
				if(items.stacks[i] == null) {
					items.stacks[i] = ItemStack.EMPTY;
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound = super.writeToNBT(tagCompound);
		if(this.battery != null) {
			this.battery.writeToNBT(tagCompound);
		}
		if(this.items != null) {
			NBTTagList itemList = new NBTTagList();
			for(int i = 0; i < items.stacks.length; i++) {
				ItemStack stack = items.stacks[i];
				if(stack != null && !stack.isEmpty()) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("Inventory", itemList);
		}
		return tagCompound;
	}

	// Remove NBT data for transfer with the BuildCraft Builder
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		data.removeTag("Inventory");
		data.removeTag("bb_energy");
	}

	@Nullable
	protected IItemHandler getItemHandler(@Nullable EnumFacing side) {
		return items;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if(battery != null && capability == CapabilityEnergy.ENERGY && battery.getStorage(facing) != null) {
			return true;
		}
		if(items != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(battery != null && capability == CapabilityEnergy.ENERGY && battery.getStorage(facing) != null) {
			return CapabilityEnergy.ENERGY.cast(battery.getStorage(facing));
		}
		if(items != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getItemHandler(facing));
		}
		return super.getCapability(capability, facing);
	}
/*@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoEnergyPerTick() {
		if(this.battery != null) {
			return (int) Math.round(battery.getEnergyUsage());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoMaxEnergyPerTick() {
		if(this.battery != null) {
			return (int) Math.round(battery.getMaxEnergyUsage());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoEnergyStored() {
		if(this.battery != null) {
			return (int) Math.round(battery.getEnergyStored());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoMaxEnergyStored() {
		if(this.battery != null) {
			return (int) Math.round(battery.getMaxEnergyStored());
		} else {
			return 0;
		}
	}*/

	/*@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side,
		EntityPlayer player, boolean debug) {
		if(this instanceof IInformationProvider) {
			IInformationProvider p = (IInformationProvider) this;
			ArrayList<String> data = new ArrayList<String>();
			p.getInformation(player, side, data, debug);
			for(String s : data) {
				info.add(new ChatComponentText(s));
			}
		}
	}

	@Optional.Method(modid = Mods.GregTech)
	public boolean isGivingInformation() {
		return (this instanceof IInformationProvider);
	}

	@Optional.Method(modid = Mods.GregTech)
	public String[] getInfoData() {
		if(this instanceof IInformationProvider) {
			IInformationProvider p = (IInformationProvider) this;
			ArrayList<String> data = new ArrayList<String>();
			p.getInformation(null, ForgeDirection.UNKNOWN, data, false);
			return data.toArray(new String[data.size()]);
		} else {
			return new String[] {};
		}
	}*/
}
