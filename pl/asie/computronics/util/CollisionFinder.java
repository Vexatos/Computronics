package pl.asie.computronics.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class CollisionFinder {
	private World world;
	private double cx, cy, cz;
	private double ox, oy, oz;
	private final float xDir, yDir, zDir;
	
	public CollisionFinder(World world, int x, int y, int z, float xDir, float yDir, float zDir) {
		this.world = world; this.cx = x; this.cy = y; this.cz = z;
		this.ox = x; this.oy = y; this.oz = z;
		this.xDir = xDir; this.yDir = yDir; this.zDir = zDir;
	}
	
	public World world() { return world; }
	public int x() { return (int)Math.round(cx); }
	public int y() { return (int)Math.round(cy); }
	public int z() { return (int)Math.round(cz); }
	
	public float distance() {
		double x = cx-ox;
		double y = cy-oy;
		double z = cz-oz;
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public Object nextCollision(int steps) {
		for(int i = 0; i < steps; i++) {
			cx += xDir;
			cy += yDir;
			cz += zDir;
			int x = (int)Math.round(cx);
			int y = (int)Math.round(cy);
			int z = (int)Math.round(cz);
			if(y < 0 || y >= 256) return null;
			
			if(!world.isAirBlock(x, y, z)) {
				Block found = Block.blocksList[world.getBlockId(x, y, z)];
				if(found.isOpaqueCube()) return found; // Found an opaque cube block
			}
		}
		return null;
	}
}
