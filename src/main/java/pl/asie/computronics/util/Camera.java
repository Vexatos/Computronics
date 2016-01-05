package pl.asie.computronics.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Config;

public class Camera {
	private World world;
	private float oxPos, oyPos, ozPos, xPos, yPos, zPos, xDirection, yDirection, zDirection;
	private Object hit;
	
	public Camera() {
		// not initialized
	}
	
	public boolean ray(World worldObj, float xCoord, float yCoord, float zCoord, EnumFacing dir, float x, float y) {
		hit = null;
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) return false;
		if(dir != null) {
			xDirection = 0.0F;
			yDirection = y;
			zDirection = 0.0F;
			
			switch(dir) {
				case EAST: { xDirection = 1.0F; zDirection = -x; xCoord += 0.6F; } break;
				case NORTH: { zDirection = -1.0F; xDirection = x; zCoord -= 0.6F; } break;
				case SOUTH: { zDirection = 1.0F; xDirection = -x; zCoord += 0.6F; } break;
				case WEST: { xDirection = -1.0F; zDirection = x; xCoord -= 0.6F; } break;
				case DOWN: { yDirection = -1.0F; xDirection = x; zDirection = y; yCoord -= 0.6F; } break;
				case UP: { yDirection = 1.0F; xDirection = x; zDirection = y; yCoord += 0.6F; } break;
				default: return false;
			}
			world = worldObj;
			xPos = xCoord + 0.5f;
			yPos = yCoord + 0.5f;
			zPos = zCoord + 0.5f;
			oxPos = xPos; oyPos = yPos; ozPos = zPos;
			// A little workaround for the way I do things (skipping the block right in front, that is)
			BlockPos pos = new BlockPos((int)Math.floor(xPos), (int)Math.floor(yPos), (int)Math.floor(zPos));
			if(!world.isAirBlock(pos)) {
				hit = world.getBlockState(pos).getBlock();
				return true;
			}
					
			// shoot ray
			float steps = Config.CAMERA_DISTANCE;
			Vec3 origin = new Vec3(oxPos, oyPos, ozPos);
			Vec3 target = new Vec3(xPos + (xDirection * steps), yPos + (yDirection * steps), zPos + (zDirection * steps));
			MovingObjectPosition mop = world.rayTraceBlocks(origin, target);
			if(mop !=  null) {
				xPos = (float)mop.hitVec.xCoord;
				yPos = (float)mop.hitVec.yCoord;
				zPos = (float)mop.hitVec.zCoord;
				switch(mop.typeOfHit) {
					case ENTITY: {
							hit = mop.entityHit;
						} break;
					case BLOCK: {
							hit = world.getBlockState(mop.getBlockPos());
						} break;
					default: break;
				}
			}
			return true;
		}
		return false;
	}
	
	public double getDistance() {
		if(hit == null) return -1.0;
		double x = xPos-oxPos;
		double y = yPos-oyPos;
		double z = zPos-ozPos;
		return Math.sqrt(x*x + y*y + z*z);
	}
}
