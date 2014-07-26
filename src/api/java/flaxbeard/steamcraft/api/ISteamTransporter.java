package flaxbeard.steamcraft.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISteamTransporter {
	public final float pressureResistance = 0.5F;
	public float getPressure();
	public boolean canInsert(ForgeDirection face);
	public int getCapacity();
	public int getSteam();
	public void explode();
	public void insertSteam(int amount, ForgeDirection face);
	public void decrSteam(int i);
	public boolean doesConnect(ForgeDirection face);
	public abstract boolean acceptsGauge(ForgeDirection face);
}
