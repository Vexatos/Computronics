package pl.asie.lib.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.lib.util.WorldUtils;

import javax.annotation.Nullable;

public class TileEntityBase extends TileEntity {

	// Base functions for containers
	public void openInventory() {

	}

	public void closeInventory() {

	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(getPos()) == this
			&& player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	// Remote NBT data management
	public void readFromRemoteNBT(NBTTagCompound tag) {
		// NO-OP
	}

	public NBTTagCompound writeToRemoteNBT(NBTTagCompound tag) {
		return tag;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToRemoteNBT(super.getUpdateTag());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		if(tag != null) {
			this.readFromRemoteNBT(tag);
		}
	}

	protected void notifyBlockUpdate() {
		WorldUtils.notifyBlockUpdate(getWorld(), getPos());
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("old_redstone", this.oldRedstoneSignal);
		return tag;
	}

	public int requestCurrentRedstoneValue(@Nullable EnumFacing side) {
		return 0;
	}
}
