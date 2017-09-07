package pl.asie.lib.api.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/*
 * Interface heavily inspired by CoFH (RF) and IC2 (EU) APIs.
 */
public interface IBattery {

	public double insert(@Nullable EnumFacing side, double maximum, boolean simulate);

	public double extract(@Nullable EnumFacing side, double maximum, boolean simulate);

	public double getEnergyStored();

	public double getMaxEnergyStored();

	public double getMaxEnergyInserted();

	public double getMaxEnergyExtracted();

	public boolean canInsert(@Nullable EnumFacing side, String type);

	public boolean canExtract(@Nullable EnumFacing side, String type);

	public void readFromNBT(NBTTagCompound tag);

	public void writeToNBT(NBTTagCompound tag);

	public void onTick();

	public double getEnergyUsage();

	public double getMaxEnergyUsage();

	@Nullable
	IEnergyStorage getStorage(EnumFacing side);
}
