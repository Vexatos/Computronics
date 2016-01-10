package pl.asie.lib.lib;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class EntityCoord {
	public int dimensionID, x, y, z;
	
	public EntityCoord(TileEntity entity) {
		this.x = entity.xCoord;
		this.y = entity.yCoord;
		this.z = entity.zCoord;
		this.dimensionID = entity.getWorldObj().provider.dimensionId;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null || !(other instanceof EntityCoord)) return false;
		if(other == this) return true;
		EntityCoord e = (EntityCoord)other;
		return (e.x == this.x && e.y == this.y && e.z == this.z && e.dimensionID == this.dimensionID);
	}
	
	public TileEntity get() {
		World world = DimensionManager.getWorld(dimensionID);
		if(world == null) return null;
		else return world.getTileEntity(x, y, z);
	}
	
	@Override
	public String toString() {
		return "[DIM "+this.dimensionID+";"+this.x+","+this.y+","+this.z+"]";
	}
}
