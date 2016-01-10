package pl.asie.lib.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCoord {
	private int x, y, z;
	
	public BlockCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setZ(int z) { this.z = z; }
	public void addX(int x) { this.x += x; }
	public void addY(int y) { this.y += y; }
	public void addZ(int z) { this.z += z; }
	public void add(int x, int y, int z) { this.x += x; this.y += y; this.z += z; }
	public void add(ForgeDirection dir) { this.x += dir.offsetX; this.y += dir.offsetY; this.z += dir.offsetZ; }
	
	public float distance(BlockCoord two) {
		int dx = this.x - two.x;
		int dy = this.y - two.y;
		int dz = this.z - two.z;
		return (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	public Block getBlock(IBlockAccess world) { return world.getBlock(x, y, z); }
	public int getBlockMetadata(IBlockAccess world) { return world.getBlockMetadata(x, y, z); }
	public TileEntity getTileEntity(IBlockAccess world) { return world.getTileEntity(x, y, z); }

	@Override
	public boolean equals(Object other) {
		if(other == null || !(other instanceof BlockCoord)) return false;
		BlockCoord ob = (BlockCoord)other;
		return ob.x == this.x && ob.y == this.y && ob.z == this.z;
	}
	
	@Override
	public int hashCode() {
		return x*227 + z*17 + y;
	}
}
