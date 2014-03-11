package pl.asie.computronics.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import pl.asie.lib.util.MiscUtils;
import pl.asie.lib.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

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
	
	private String generateHash(ItemStack stack) {
		String temp = stack.itemID + ";" + stack.getItemDamage() + ";" + stack.getUnlocalizedName();
		try {
			byte[] data = MessageDigest.getInstance("MD5").digest(temp.getBytes());
			return MiscUtils.asHexString(data).substring(0, 8);
		} catch(Exception e) {
			return null;
		}
	}
	
	public Map<String, Object> blockData() {
		Block block = WorldUtils.getBlock(world(), x(), y(), z());
		if(block == null) return null;
		
		int meta = world().getBlockMetadata(x(), y(), z());
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", generateHash(new ItemStack(block, 1, meta)));
		data.put("distance", (double)distance());
		data.put("brightness", world().getBlockLightValue(x(), y(), z()));
		return data;
	}
	
	public Object nextCollision(int steps) {
		for(int i = 0; i < steps; i++) {
			cx += xDir;
			cy += yDir;
			cz += zDir;
			int x = (int)Math.floor(cx);
			int y = (int)Math.floor(cy);
			int z = (int)Math.floor(cz);
			if(y < 0 || y >= 256) return null;
			
			if(!world.isAirBlock(x, y, z)) {
				Block found = Block.blocksList[world.getBlockId(x, y, z)];
				if(found.isOpaqueCube()) {
					return found;
				}
			}
		}
		return null;
	}
}
