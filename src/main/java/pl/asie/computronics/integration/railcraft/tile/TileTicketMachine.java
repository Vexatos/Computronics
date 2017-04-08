package pl.asie.computronics.integration.railcraft.tile;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.gui.slot.PaperSlotFilter;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.api.tile.IBatteryProvider;
import pl.asie.lib.api.tile.IInventoryProvider;
import pl.asie.lib.network.Packet;
import pl.asie.lib.tile.BatteryBasic;

import java.util.UUID;

/**
 * Contains a little bit of Railcraft code.
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.railcraft.api.core.IOwnable", modid = Mods.Railcraft)
})
public class TileTicketMachine extends TileEntityPeripheralBase implements IInventoryProvider, IOwnable, IBatteryProvider {

	private GameProfile owner = new GameProfile((UUID) null, "[Railcraft]");
	private boolean isLocked = false;
	private boolean isSelectionLocked = false;
	private boolean isPrintLocked = false;
	private boolean lockChanged = false;
	private static final int
		paperSlot = 10,
		ticketSlot = 11;
	private int selectedSlot = 0;
	private static final int powerUsage = 25;

	public TileTicketMachine() {
		super("ticket_machine");
		this.createInventory(12);
		if(Config.TICKET_MACHINE_CONSUME_RF) {
			this.registerBattery(new BatteryBasic(5000));
		}
	}

	public boolean isLocked() {
		return this.isLocked;
	}

	public boolean isSelectionLocked() {
		return this.isSelectionLocked;
	}

	public boolean isPrintLocked() {
		return this.isPrintLocked;
	}

	protected void sendLockChange() {
		try {
			int i = isLocked() ? 1 : 0;
			i |= isSelectionLocked() ? 1 << 1 : 0;
			i |= isPrintLocked() ? 1 << 2 : 0;
			i |= isActive ? 1 << 3 : 0;

			Packet packet = Computronics.packet.create(PacketType.TICKET_SYNC.ordinal())
				.writeTileLocation(this)
				.writeInt(i)
				.writeInt(selectedSlot);
			if(worldObj.isRemote) {
				Computronics.packet.sendToServer(packet);
			} else {
				Computronics.packet.sendToAllAround(packet, this, 64.0D);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int progress = 0;
	private ItemStack currentTicket;
	private int ticketQueue = 0;

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getProgress() {
		return this.progress;
	}

	public int getMaxProgress() {
		return 20;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	private boolean isActive;

	public void setActive(boolean active) {
		setActive(active, true);
	}

	public void setActive(boolean active, boolean causeUpdate) {
		this.isActive = active;
		if(causeUpdate) {
			markSaveDirty();
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(lockChanged) {
			sendLockChange();
			lockChanged = false;
		}
		if(worldObj.isRemote || currentTicket == null) {
			return;
		}
		if(progress < getMaxProgress()) {
			if(extractFromBattery(powerUsage)) {
				this.progress++;
				this.setActive(true, isActive);
			} else {
				this.setActive(false, !isActive);
			}
		}
		if(progress >= getMaxProgress()) {
			ItemStack outputSlotStack = this.getStackInSlot(ticketSlot);
			if(outputSlotStack != null) {
				if(!outputSlotStack.getItem().equals(currentTicket.getItem()) || !ItemStack.areItemStackTagsEqual(outputSlotStack, currentTicket)) {
					return;
				}
				if(!outputSlotStack.isStackable()
					|| outputSlotStack.stackSize >= outputSlotStack.getMaxStackSize()) {
					return;
				}
				this.decrStackSize(paperSlot, 1);
				outputSlotStack.stackSize++;
			} else {
				this.decrStackSize(paperSlot, 1);
				setInventorySlotContents(ticketSlot, currentTicket);
			}
			this.ticketQueue--;
			this.progress = 0;
			if(this.ticketQueue <= 0) {
				this.currentTicket = null;
				this.setActive(false);
			}
		}
	}

	private void markSaveDirty() {
		lockChanged = true;
	}

	public void setLocked(boolean locked) {
		setLocked(locked, true);
	}

	public void setLocked(boolean locked, boolean causeUpdate) {
		if(isLocked != locked) {
			this.isLocked = locked;
			if(causeUpdate) {
				markSaveDirty();
			}
		}
	}

	public void setSelectionLocked(boolean locked) {
		setSelectionLocked(locked, true);
	}

	public void setSelectionLocked(boolean locked, boolean causeUpdate) {
		if(isSelectionLocked() != locked) {
			this.isSelectionLocked = locked;
			if(causeUpdate) {
				markSaveDirty();
			}
		}
	}

	public void setPrintLocked(boolean locked) {
		setPrintLocked(locked, true);
	}

	public void setPrintLocked(boolean locked, boolean causeUpdate) {
		if(isPrintLocked() != locked) {
			this.isPrintLocked = locked;
			if(causeUpdate) {
				markSaveDirty();
			}
		}
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void setSelectedSlot(int slot) {
		setSelectedSlot(slot, true);
	}

	public void setSelectedSlot(int slot, boolean causeUpdate) {
		if(slot >= 0 && slot <= 9) {
			this.selectedSlot = slot;
			if(causeUpdate) {
				markSaveDirty();
			}
		}
	}

	// Computer utility methods

	private void checkSlot(int slot) {
		if(slot < 0 || slot > 9) {
			throw new IllegalArgumentException("invalid slot: " + String.valueOf(slot + 1));
		}
	}

	private void checkAmount(ItemStack stack, int amount) {
		if(amount < 0 || amount > stack.getMaxStackSize()) {
			throw new IllegalArgumentException("invalid amount: " + amount);
		}
	}

	private void checkDestination(String dest) {
		if(dest.length() > 32) {
			throw new IllegalArgumentException("invalid destination");
		} else if(owner == null || owner.getName().equals("")) {
			throw new IllegalArgumentException("invalid destination");
		}
	}

	public boolean extractFromBattery(double amount) {
		if(!Config.TICKET_MACHINE_CONSUME_RF) {
			return true;
		}
		if(this.getBatteryProvider().getEnergyStored() < amount) {
			return false;
		}
		this.getBatteryProvider().extract(-1, amount, false);
		return true;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private Object[] tryConsumeEnergy(double v, String methodName) {
		if(this.node() instanceof Connector) {
			int power = this.tryConsumeEnergy(v);
			if(power < 0) {
				return new Object[] { null, null, power + ": " + methodName + ": not enough energy available: required"
					+ v + ", found " + ((Connector) node()).globalBuffer() };
			}
		}
		return null;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private int tryConsumeEnergy(double v) {
		if(v < 0) {
			return -2;
		}
		v = -v;
		if(this.node() instanceof Connector) {
			Connector connector = ((Connector) this.node());
			return connector.tryChangeBuffer(v) ? 1 : -1;

		}
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Printer,
			"Ticket machine",
			OCUtils.Vendors.Railcraft,
			"Dot matrix 3000"
		);
	}

	// Methods for Computers

	public Object[] printTicket() {
		return printTicket(false);
	}

	public Object[] printTicket(boolean opencomputers) {
		return printTicket(getSelectedSlot() + 1, 1, opencomputers);
	}

	public Object[] printTicket(int amount, boolean opencomputers) {
		return printTicket(getSelectedSlot() + 1, amount, opencomputers);
	}

	public Object[] printTicket(int slot, int amount, boolean opencomputers) {
		if(worldObj.isRemote) {
			return null;
		}
		slot -= 1;
		if(progress > 0) {
			return new Object[] { "machine is already printing" };
		}
		if(this.getStackInSlot(paperSlot) == null || this.getStackInSlot(paperSlot).stackSize < 1) {
			return new Object[] { false, "no paper found in paper slot" };
		}
		checkSlot(slot);
		ItemStack stack = getStackInSlot(slot);
		if(stack == null) {
			return new Object[] { false, "no golden ticket in specified slot" };
		}
		ItemStack ticket = ItemTicket.getTicket();
		if(ticket == null) {
			return new Object[] { false, "tickets not enabled in config" };
		}
		String destination = ItemTicket.getDestination(stack);
		checkDestination(destination);
		ItemTicket.setTicketData(ticket, destination, destination, getOwner());
		if(opencomputers && Mods.isLoaded(Mods.OpenComputers)) {
			Object[] error = tryConsumeEnergy(50, "printTicket");
			if(error != null) {
				return error;
			}
		}
		if(Config.TICKET_MACHINE_CONSUME_RF && getBatteryProvider().getEnergyStored() < powerUsage) {
			return new Object[] { false, "not enough energy" };
		}
		checkAmount(ticket, amount);
		ItemStack outputSlotStack = this.getStackInSlot(ticketSlot);
		if(outputSlotStack != null) {
			if(!outputSlotStack.getItem().equals(ticket.getItem()) || !ItemStack.areItemStackTagsEqual(outputSlotStack, ticket)) {
				return new Object[] { false, "output slot already contains ticket with different destination" };
			}
			if(!outputSlotStack.isStackable()
				|| outputSlotStack.stackSize + amount > outputSlotStack.getMaxStackSize()) {
				return new Object[] { false, "output slot is too full" };
			}
		}
		this.currentTicket = ticket;
		this.ticketQueue = amount;
		return new Object[] { true };
	}

	private Object[] setDestination(int slot, String destination, boolean opencomputers) {
		slot -= 1;
		checkSlot(slot);
		ItemStack ticket = getStackInSlot(slot);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			checkDestination(destination);
			if(opencomputers && Mods.isLoaded(Mods.OpenComputers)) {
				Object[] error = tryConsumeEnergy(50, "printTicket");
				if(error != null) {
					return error;
				}
			}
			ItemTicketGold.setTicketData(ticket, destination, destination, getOwner());
			return new Object[] { true };
		} else {
			return new Object[] { false, "there is no golden ticket in that slot" };
		}
	}

	private Object[] getDestination(int slot) {
		slot -= 1;
		checkSlot(slot);
		ItemStack ticket = getStackInSlot(slot);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			return new Object[] { ItemTicketGold.getDestination(ticket) };
		} else {
			return new Object[] { false, "there is no golden ticket in that slot" };
		}
	}

	public Object[] setSelectedTicket(int slot) {
		slot -= 1;
		checkSlot(slot);
		this.setSelectedSlot(slot);
		return new Object[] { this.getSelectedSlot() + 1 };
	}

	// OpenComputers stuff

	@Callback(doc = "function([amount:number [, slot:number]]):boolean; Tries to print one or more tickets from the current or the specified ticket slot; amount may be nil; Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] printTicket(Context c, Arguments a) {
		if(a.count() >= 2) {
			return printTicket(a.checkInteger(1), a.checkAny(0) != null ? a.checkInteger(0) : 1, true);
		}
		if(a.count() >= 1) {
			return printTicket(a.checkAny(0) != null ? a.checkInteger(0) : 1, true);
		}
		return printTicket(true);
	}

	@Callback(doc = "function(allowed:boolean):boolean; permits or prohibits manual printing; Returns true if manual printing is allowed")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setManualPrintingAllowed(Context c, Arguments a) {
		this.setPrintLocked(!a.checkBoolean(0));
		return new Object[] { !this.isPrintLocked() };
	}

	@Callback(doc = "function():boolean; Returns true if manual printing is allowed", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isManualPrintingAllowed(Context c, Arguments a) {
		return new Object[] { !this.isPrintLocked() };
	}

	@Callback(doc = "function(allowed:boolean):boolean; permits or prohibits manually selecting a ticket; Returns true if manual ticket selection is allowed")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setManualSelectionAllowed(Context c, Arguments a) {
		this.setSelectionLocked(!a.checkBoolean(0));
		return new Object[] { !this.isSelectionLocked() };
	}

	@Callback(doc = "function():boolean; Returns true if manual ticket selection is allowed", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isManualSelectionAllowed(Context c, Arguments a) {
		return new Object[] { !this.isSelectionLocked() };
	}

	@Callback(doc = "function([slot:number,] destination:string):string; Tries to set the destination of the currently selected or the specified ticket; Returns the new destination")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDestination(Context c, Arguments a) {
		if(a.count() >= 2) {
			return setDestination(a.checkInteger(0), a.checkString(1), true);
		}
		return setDestination(this.getSelectedSlot() + 1, a.checkString(0), true);
	}

	@Callback(doc = "function([slot:number]):string; Returns the destination of the currently selected or the specified ticket")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getDestination(Context c, Arguments a) {
		return getDestination(a.optInteger(0, getSelectedSlot() + 1));
	}

	@Callback(doc = "function():number; Returns the index of the currently selected ticket", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSelectedTicket(Context c, Arguments a) {
		return new Object[] { this.getSelectedSlot() + 1 };
	}

	@Callback(doc = "function(slot:number):number; Sets the currently selected ticket slot; Returns the new selected slot")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setSelectedTicket(Context c, Arguments a) {
		return this.setSelectedTicket(a.checkInteger(0));
	}

	// Computercraft stuff

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "printTicket", "setManualPrintingAllowed", "isManualPrintingAllowed",
			"setManualSelectionAllowed", "isManualSelectionAllowed", "getDestination", "setDestination",
			"getSelectedTicket", "setSelectedTicket" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			switch(method) {
				case 0: {
					if(arguments.length < 1) {
						return this.printTicket(false);
					}
					if(arguments[0] != null && !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number or nil");
					}
					if(arguments.length < 2) {
						return this.printTicket(arguments[0] != null ? ((Number) arguments[0]).intValue() : 1, false);
					}
					if(!(arguments[1] instanceof Number)) {
						throw new LuaException("second argument needs to be a number or non-existant");
					}
					return this.printTicket(((Number) arguments[1]).intValue(),
						arguments[0] != null ? ((Number) arguments[0]).intValue() : 1, false);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Boolean)) {
						throw new LuaException("first argument needs to be a boolean");
					}
					this.setPrintLocked(!(Boolean) arguments[0]);
					return new Object[] { !this.isPrintLocked() };
				}
				case 2: {
					return new Object[] { !this.isPrintLocked() };
				}
				case 3: {
					if(arguments.length < 1 || !(arguments[0] instanceof Boolean)) {
						throw new LuaException("first argument needs to be a boolean");
					}
					this.setSelectionLocked(!(Boolean) arguments[0]);
					return new Object[] { !this.isSelectionLocked() };
				}
				case 4: {
					return new Object[] { !this.isSelectionLocked() };
				}
				case 5: {
					if(arguments.length < 1) {
						return this.getDestination(this.getSelectedSlot() + 1);
					}
					if(!(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number or non-existant");
					}
					return getDestination(((Number) arguments[0]).intValue());
				}
				case 6: {
					if(arguments.length < 1) {
						throw new LuaException("first argument needs to be a number or string");
					}
					if(arguments.length < 2) {
						if(!(arguments[0] instanceof String)) {
							throw new LuaException("first argument needs to be a number or string");
						}
						return setDestination(getSelectedSlot() + 1, (String) arguments[0], false);
					}
					if(!(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number or string");
					}
					if(!(arguments[1] instanceof String)) {
						throw new LuaException("second argument needs to be a string or non-existant");
					}
					return setDestination(((Number) arguments[0]).intValue(), ((String) arguments[1]), false);
				}
				case 7: {
					return new Object[] { this.getSelectedSlot() + 1 };
				}
				case 8: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					return this.setSelectedTicket(((Number) arguments[0]).intValue());
				}
			}
			return null;
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
	}

	//Required stuff

	public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
		if(entityliving instanceof EntityPlayer) {
			this.owner = ((EntityPlayer) entityliving).getGameProfile();
		}
	}

	@Override
	public GameProfile getOwner() {
		return this.owner;
	}

	@Override
	public String getLocalizationTag() {
		return getBlockType().getUnlocalizedName() + ".name";
	}

	public boolean isOwner(GameProfile player) {
		return isSamePlayer(this.owner, player);
	}

	private static boolean isSamePlayer(GameProfile a, GameProfile b) {
		return a.getId() != null && b.getId() != null ? a.getId().equals(b.getId()) : a.getName() != null && a.getName().equals(b.getName());
	}

	@Override
	public String getSoundName() {
		return "ticket_print";
	}

	@Override
	public boolean shouldPlaySound() {
		return this.isActive;
	}

	@Override
	public float getVolume() {
		return 0.5f;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		String ownerName = "[Unknown]";
		if(tag.hasKey("owner")) {
			ownerName = tag.getString("owner");
		}

		UUID ownerUUID = null;
		if(tag.hasKey("ownerId")) {
			ownerUUID = UUID.fromString(tag.getString("ownerId"));
		}

		this.owner = new GameProfile(ownerUUID, ownerName);

		if(tag.hasKey("locked")) {
			isLocked = tag.getBoolean("locked");
		}
		if(tag.hasKey("selectionLocked")) {
			isSelectionLocked = tag.getBoolean("selectionLocked");
		}
		if(tag.hasKey("printLocked")) {
			isPrintLocked = tag.getBoolean("printLocked");
		}
		if(tag.hasKey("selectedslot")) {
			selectedSlot = tag.getInteger("selectedslot");
		}
		if(tag.hasKey("progress")) {
			progress = tag.getInteger("progress");
			currentTicket = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("currentTicket"));
			if(currentTicket == null) {
				progress = 0;
			}
			isActive = tag.getBoolean("isActive");
			ticketQueue = tag.getInteger("ticketQueue");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(this.owner.getName() != null) {
			tag.setString("owner", this.owner.getName());
		}

		if(this.owner.getId() != null) {
			tag.setString("ownerId", this.owner.getId().toString());
		}
		tag.setBoolean("locked", isLocked);
		tag.setBoolean("selectionLocked", isSelectionLocked);
		tag.setBoolean("printLocked", isPrintLocked);
		tag.setInteger("selectedslot", selectedSlot);
		tag.setInteger("progress", progress);
		tag.setBoolean("isActive", isActive);
		if(currentTicket != null) {
			NBTTagCompound currentTicketTag = new NBTTagCompound();
			currentTicket.writeToNBT(currentTicketTag);
			tag.setTag("currentTicket", currentTicketTag);
			tag.setInteger("ticketQueue", this.ticketQueue);
		}
	}

	@Override
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		super.removeFromNBTForTransfer(data);
		data.removeTag("owner");
		data.removeTag("ownerId");
		data.removeTag("locked");
		data.removeTag("selectionLocked");
		data.removeTag("printLocked");
		data.removeTag("selectedslot");
		data.removeTag("progress");
		data.removeTag("isActive");
		data.removeTag("currentTicket");
		data.removeTag("ticketQueue");
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		super.readFromRemoteNBT(tag);
		String ownerName = "[Unknown]";
		if(tag.hasKey("owner")) {
			ownerName = tag.getString("owner");
		}

		UUID ownerUUID = null;
		if(tag.hasKey("ownerId")) {
			ownerUUID = UUID.fromString(tag.getString("ownerId"));
		}

		this.owner = new GameProfile(ownerUUID, ownerName);
		if(tag.hasKey("locked")) {
			isLocked = tag.getBoolean("locked");
		}
		if(tag.hasKey("selectionLocked")) {
			isSelectionLocked = tag.getBoolean("selectionLocked");
		}
		if(tag.hasKey("printLocked")) {
			isPrintLocked = tag.getBoolean("printLocked");
		}
		if(tag.hasKey("selectedslot")) {
			selectedSlot = tag.getInteger("selectedslot");
		}
		if(tag.hasKey("progress")) {
			progress = tag.getInteger("progress");
			isActive = tag.getBoolean("isActive");
		}
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		if(this.owner.getName() != null) {
			tag.setString("owner", this.owner.getName());
		}

		if(this.owner.getId() != null) {
			tag.setString("ownerId", this.owner.getId().toString());
		}
		tag.setBoolean("locked", isLocked);
		tag.setBoolean("selectionLocked", isSelectionLocked);
		tag.setBoolean("printLocked", isPrintLocked);
		tag.setInteger("selectedslot", selectedSlot);
		tag.setInteger("progress", progress);
		tag.setBoolean("isActive", isActive);
	}

	// Security

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(!super.isItemValidForSlot(slot, stack)) {
			return false;
		}
		switch(slot) {
			case paperSlot: {
				return PaperSlotFilter.FILTER.matches(stack);
			}
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return slot == paperSlot;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot == ticketSlot;
	}

	private static final int[] ACCESSIBLE_SLOTS = new int[] { paperSlot, ticketSlot };

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return ACCESSIBLE_SLOTS.clone();
	}
}
