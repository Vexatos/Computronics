package pl.asie.computronics.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pl.asie.lib.util.MiscUtils;
import pl.asie.lib.util.WorldUtils;

public class CollisionFinder {
	private World world;
	private double cx, cy, cz;
	private double ox, oy, oz;
	private final float xDir, yDir, zDir;
	
	public CollisionFinder(World world, float x, float y, float z, float xDir, float yDir, float zDir) {
		this.world = world; this.cx = x+0.5; this.cy = y+0.5; this.cz = z+0.5;
		this.xDir = xDir; this.yDir = yDir; this.zDir = zDir;
		
		// Store original coords
		this.ox = cx; this.oy = cy; this.oz = cz;
	}
	
	public World world() { return world; }
	public int x() { return (int)Math.floor(cx); }
	public int y() { return (int)Math.floor(cy); }
	public int z() { return (int)Math.floor(cz); }
	public float xDirection() { return xDir; }
	public float yDirection() { return yDir; }
	public float zDirection() { return zDir; }
	
	public float distance() {
		double x = cx-ox;
		double y = cy-oy;
		double z = cz-oz;
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public String blockHash(ItemStack stack) {
		String temp = stack.itemID + ";" + stack.getItemDamage() + ";" + stack.getUnlocalizedName();
		try {
			byte[] data = MessageDigest.getInstance("MD5").digest(temp.getBytes());
			return MiscUtils.asHexString(data).substring(0, 8);
		} catch(Exception e) {
			return null;
		}
	}
	
	public String blockHash() {
		Block block = WorldUtils.getBlock(world(), x(), y(), z());
		if(block == null) return null;
		
		int meta = world().getBlockMetadata(x(), y(), z());

		return blockHash(new ItemStack(block, 1, meta));
	}
	
	public Map<String, Object> blockData() {
		Block block = WorldUtils.getBlock(world(), x(), y(), z());
		if(block == null) return null;
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", blockHash());
		data.put("distance", (double)distance());
		data.put("brightness", world().getBlockLightValue(x(), y(), z()));
		return data;
	}
	
	public Object nextCollision(int steps) {
		Vec3 origin = world.getWorldVec3Pool().getVecFromPool(cx, cy, cz);
		Vec3 target = world.getWorldVec3Pool().getVecFromPool(cx + (xDir * steps), cy + (yDir * steps), cz + (zDir * steps));
		MovingObjectPosition mop = world.clip(origin, target, true);

		if(mop == null) return null;
		cx = mop.hitVec.xCoord;
		cy = mop.hitVec.yCoord;
		cz = mop.hitVec.zCoord;
		switch(mop.typeOfHit) {
			case ENTITY: {
					return mop.entityHit;
				}
			case TILE: {
					return WorldUtils.getBlock(world, mop.blockX, mop.blockY, mop.blockZ);
				}
			default:
				return null;
		}
	}
}
