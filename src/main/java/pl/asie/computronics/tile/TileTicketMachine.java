package pl.asie.computronics.tile;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.integration.railcraft.slot.PaperSlotFilter;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.api.tile.IInventoryProvider;

import java.util.UUID;

/**
 * Contains a little bit of Railcraft code.
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.railcraft.api.core.IOwnable", modid = Mods.Railcraft)
})
public class TileTicketMachine extends TileEntityPeripheralBase implements IInventoryProvider, IOwnable {

	private GameProfile owner = new GameProfile((UUID) null, "[Railcraft]");
	private boolean isLocked = true;
	private boolean isSelectionLocked = false;
	private boolean isPrintLocked = false;
	private static final int
		paperSlot = 10,
		ticketSlot = 11;

	public TileTicketMachine() {
		super("ticket_machine");
		this.createInventory(12);
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

	private void checkSlot(int slot) {
		if(slot < 0 || slot > 9) {
			throw new IllegalArgumentException("invalid slot: " + slot);
		}
	}

	public Object[] printTicket(int slot) {
		slot -= 1;
		if(this.getStackInSlot(ticketSlot) != null) {
			return new Object[] { false, "output slot already contains ticket" };
		}
		checkSlot(slot);
		ItemStack stack = getStackInSlot(slot);
		if(stack == null) {
			return new Object[] { false, "no ticket in slot" };
		}
		ItemStack ticket = ItemTicket.getTicket();
		if(ticket == null) {
			return new Object[] { false, "tickets not enabled in config" };
		}
		String destination = ItemTicket.getDestination(stack);
		if(!ItemTicket.setTicketData(ticket, destination, destination, getOwner())) {
			return new Object[] { false, "invalid destination" };
		}
		setInventorySlotContents(ticketSlot, ticket);
		return new Object[] { true };
	}

	private Object[] setDestination(int slot, String destination) {
		slot -= 1;
		checkSlot(slot);
		ItemStack ticket = getStackInSlot(slot);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			if(!ItemTicketGold.setTicketData(ticket, destination, destination, getOwner())) {
				return new Object[] { false, "invalid destination" };
			}
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

	//Computer stuff
	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "printTicket", "setPrintLock", "setSelectLock",
			"hasPrintLock", "hasSelectLock", "getDestination", "setDestination" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			switch(method) {
				case 0: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					return this.printTicket(((Number) arguments[0]).intValue());
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Boolean)) {
						throw new LuaException("first argument needs to be a boolean");
					}
					this.isPrintLocked = ((Boolean) arguments[0]);
					return new Object[] { this.isPrintLocked() };
				}
				case 2: {
					if(arguments.length < 1 || !(arguments[0] instanceof Boolean)) {
						throw new LuaException("first argument needs to be a boolean");
					}
					this.isSelectionLocked = ((Boolean) arguments[0]);
					return new Object[] { this.isSelectionLocked() };
				}
				case 3: {
					return new Object[] { this.isPrintLocked() };
				}
				case 4: {
					return new Object[] { this.isSelectionLocked() };
				}
				case 5: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					return getDestination(((Number) arguments[0]).intValue());
				}
				case 6: {
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					if(arguments.length < 2 || !(arguments[1] instanceof String)) {
						throw new LuaException("second argument needs to be a string");
					}
					return setDestination(((Number) arguments[0]).intValue(), ((String) arguments[1]));
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
	}

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
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		super.readFromRemoteNBT(tag);
		if(tag.hasKey("locked")) {
			isLocked = tag.getBoolean("locked");
		}
		if(tag.hasKey("selectionLocked")) {
			isSelectionLocked = tag.getBoolean("selectionLocked");
		}
		if(tag.hasKey("printLocked")) {
			isPrintLocked = tag.getBoolean("printLocked");
		}
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setBoolean("locked", isLocked);
		tag.setBoolean("selectionLocked", isSelectionLocked);
		tag.setBoolean("printLocked", isPrintLocked);
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean connectable(int side) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {

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
		return ACCESSIBLE_SLOTS;
	}
}
