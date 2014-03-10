package pl.asie.computronics.util;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pl.asie.computronics.Computronics;

public class Camera {
	public CollisionFinder cf;
	private Object hit;
	
	public boolean setRayDirection(World worldObj, float xCoord, float yCoord, float zCoord, ForgeDirection dir, float x, float y) {
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) return false;
		if(dir != null) {
			float xInc = 0.0F;
			float yInc = y;
			float zInc = 0.0F;
			switch(dir) {
				case EAST: { xInc = 1.0F; zInc = x; xCoord += 0.5F; } break;
				case NORTH: { zInc = -1.0F; xInc = x; zCoord -= 0.5F; } break;
				case SOUTH: { zInc = 1.0F; xInc = x; zCoord += 0.5F; } break;
				case WEST: { xInc = -1.0F; zInc = x; xCoord -= 0.5F; } break;
				case DOWN: { yInc = -1.0F; xInc = x; zInc = y; yCoord -= 0.5F; } break;
				case UP: { yInc = 1.0F; xInc = x; zInc = y; yCoord += 0.5F; } break;
				case UNKNOWN: return false;
				default: return false;
			}
			if(cf != null && cf.xDirection() == xInc && cf.yDirection() == yInc && cf.zDirection() == zInc)
				return true;
			if(cf != null) reset();
			cf = new CollisionFinder(worldObj, xCoord, yCoord, zCoord, xInc, yInc, zInc);
			hit = cf.nextCollision(32);
			return true;
		}
		return false;
	}
	
	public Object getHit() { return hit; }
	
	public void reset() {
		if(cf != null) {
			synchronized(cf) {
				hit = null;
				cf = null;
			}
		}
	}
	
	public double getDistance() {
		if(cf != null && hit != null) synchronized(cf) { return cf.distance(); }
		else return -1.0;
	}
	
	public Map<String, Object> getBlockData() {
		if(cf != null && hit != null) synchronized(cf) { return cf.blockData(); }
		else return null;
	}
}
