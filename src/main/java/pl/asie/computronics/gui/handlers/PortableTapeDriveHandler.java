package pl.asie.computronics.gui.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.network.Packets.Types;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class PortableTapeDriveHandler implements TapeGuiHandler, IInventory {

	private final ItemStack stack;
	private final EntityPlayer player;
	private ItemStack slot;

	public PortableTapeDriveHandler(ItemStack stack, EntityPlayer player) {
		this.stack = stack;
		this.player = player;
		this.slot = ItemStack.loadItemStackFromNBT((NBTTagCompound) getNBTData().getTag("computronics:item"));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return isUseableByPlayer(player);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public State getEnumState() {
		NBTTagCompound data = getNBTData();
		return State.values()[data.getInteger("computronics:state")];
	}

	@Override
	public void switchState(State state) {
		NBTTagCompound data = getNBTData();
		data.setInteger("computronics:state", state.ordinal());
	}

	private NBTTagCompound getNBTData() {
		NBTTagCompound data = stack.getTagCompound();
		if(data == null) {
			data = new NBTTagCompound();
			stack.setTagCompound(data);
		}
		return data;
	}

	@Override
	public void writeLocation(Packet packet) throws IOException {
		packet.writeInt(Types.Item);
		packet.writeInt(player.getEntityWorld().provider.dimensionId);
		packet.writeInt(player.getEntityId());
		packet.writeByte((byte) getNBTData().getInteger("computronics:state"));
	}

	@Override
	public int getSyncPacketID() {
		return Packets.PACKET_TAPE_GUI_STATE;
	}

	@Override
	public void initialize(ICrafting icrafting) {

	}

	@Override
	public void sendChanges() {

	}

	@Override
	public void updateFromRemote(int id, int value) {

	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? this.slot : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(slot != 0) {
			return null;
		}
		this.slot.stackSize--;
		if(this.slot.stackSize <= 0) {
			this.slot = null;
		}
		return this.slot;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int s) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int s, ItemStack stack) {
		if(s != 0) {
			return;
		}
		this.slot = stack;
	}

	@Override
	public String getInventoryName() {
		return "portabletapedrive.inventory";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int s, ItemStack stack) {
		return stack.getItem() instanceof IItemTapeStorage;
	}
}
