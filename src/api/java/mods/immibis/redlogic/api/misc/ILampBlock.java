package mods.immibis.redlogic.api.misc;

import net.minecraft.world.IBlockAccess;

public interface ILampBlock {
	public static enum LampType {Normal, Decorative, Indicator}
	
	public LampType getType();
	public boolean isPowered();
	public int getColourRGB(IBlockAccess w, int x, int y, int z);
	public int getColourWool(IBlockAccess w, int x, int y, int z);
}
