package pl.asie.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityBase extends TileEntity {

	// Base functions for containers
	public void openInventory() {

	}

	public void closeInventory() {

	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(getPos()) == this
			&& player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	// Remote NBT data management
	public void readFromRemoteNBT(NBTTagCompound tag) {
	}

	public void writeToRemoteNBT(NBTTagCompound tag) {
	}

	@Override
	public net.minecraft.network.Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToRemoteNBT(tag);
		return new S35PacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		if(tag != null) {
			this.readFromRemoteNBT(tag);
		}
	}

	// Dummy functions

	public void onBlockDestroy() {
	}

	protected int oldRedstoneSignal = -1;

	public int getOldRedstoneSignal() {
		return this.oldRedstoneSignal;
	}

	public void setRedstoneSignal(int value) {
		this.oldRedstoneSignal = value;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.oldRedstoneSignal = tag.getInteger("old_redstone");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("old_redstone", this.oldRedstoneSignal);
	}

	public int requestCurrentRedstoneValue(EnumFacing side) {
		return 0;
	}
}
