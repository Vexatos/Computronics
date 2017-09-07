package pl.asie.lib.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.api.tile.IBattery;

import javax.annotation.Nullable;

public class BatteryBasic implements IBattery {

	private double energy, maxEnergy, maxInsert, maxExtract;

	public BatteryBasic(double maxEng, double maxExt, double maxIns) {
		this.maxEnergy = maxEng;
		this.maxInsert = maxExt;
		this.maxExtract = maxIns;
	}

	public BatteryBasic(double maxEng, double maxIO) {
		this(maxEng, maxIO, maxIO);
	}

	public BatteryBasic(double maxEng) {
		this(maxEng, maxEng, maxEng);
	}

	@Override
	public double insert(@Nullable EnumFacing side, double maximum, boolean simulate) {
		if(maximum > maxInsert) {
			maximum = maxInsert;
		}
		if(energy + maximum > maxEnergy) {
			if(!simulate) {
				energy = maxEnergy;
			}
			return (maxEnergy - energy);
		} else {
			if(!simulate) {
				energy += maximum;
			}
			return maximum;
		}
	}

	@Override
	public double extract(@Nullable EnumFacing side, double maximum, boolean simulate) {
		double amount = Math.min(energy, Math.min(maximum, maxExtract));
		if(!simulate) {
			energy -= amount;
		}
		return amount;
	}

	@Override
	public double getEnergyStored() {
		return energy;
	}

	@Override
	public double getMaxEnergyStored() {
		return maxEnergy;
	}

	@Override
	public double getMaxEnergyInserted() {
		return maxInsert;
	}

	@Override
	public double getMaxEnergyExtracted() {
		return maxExtract;
	}

	@Override
	public boolean canInsert(@Nullable EnumFacing side, String type) {
		return (maxInsert > 0.0);
	}

	@Override
	public boolean canExtract(@Nullable EnumFacing side, String type) {
		return (maxExtract > 0.0);
	}

	private double[] averageUsage = new double[8];
	private double lastEnergy = 0.0;
	private double peakEnergyUsage = 0.0;
	private byte avgUsPtr = -1;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.hasKey("bb_energy")) {
			energy = tag.getDouble("bb_energy");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("bb_energy", energy);
	}

	@Override
	public void onTick() {
		if(!AsieLibMod.ENABLE_DYNAMIC_ENERGY_CALCULATION) {
			return;
		} else if(avgUsPtr == -1) {
			lastEnergy = energy;
			avgUsPtr++;
		} else {
			double p = this.energy - lastEnergy;
			lastEnergy = energy;
			if(p > peakEnergyUsage) {
				peakEnergyUsage = p;
			}
			averageUsage[avgUsPtr++] = p;
			avgUsPtr &= 7;
		}
	}

	@Override
	public double getEnergyUsage() {
		double z = 0;
		for(int i = 0; i < 8; i++) {
			z += averageUsage[i];
		}
		return z / 8.0;
	}

	@Override
	public double getMaxEnergyUsage() {
		return peakEnergyUsage;
	}

	@Override
	@Nullable
	public IEnergyStorage getStorage(EnumFacing side) {
		return energyStorage;
	}

	public final IEnergyStorage energyStorage = new IEnergyStorage() {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return (int) Math.floor(BatteryBasic.this.insert(null, maxReceive, simulate));
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return (int) Math.floor(BatteryBasic.this.extract(null, maxExtract, simulate));
		}

		@Override
		public int getEnergyStored() {
			return MathHelper.floor(BatteryBasic.this.getEnergyStored());
		}

		@Override
		public int getMaxEnergyStored() {
			return MathHelper.floor(BatteryBasic.this.getMaxEnergyStored());
		}

		@Override
		public boolean canExtract() {
			return BatteryBasic.this.canExtract(null, "RF");
		}

		@Override
		public boolean canReceive() {
			return BatteryBasic.this.canInsert(null, "RF");
		}
	};
}
