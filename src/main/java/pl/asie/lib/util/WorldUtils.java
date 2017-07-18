package pl.asie.lib.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pl.asie.lib.AsieLibMod;

import javax.annotation.Nullable;

public class WorldUtils {

	public static TileEntity getTileEntity(int dimensionId, int x, int y, int z) {
		World world = AsieLibMod.proxy.getWorld(dimensionId);
		if(world == null) {
			return null;
		}
		return world.getTileEntity(new BlockPos(x, y, z));
	}

	public static TileEntity getTileEntityServer(int dimensionId, int x, int y, int z) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimensionId);
		if(world == null) {
			return null;
		}
		return world.getTileEntity(new BlockPos(x, y, z));
	}
	
	/*public static boolean equalLocation(TileEntity a, TileEntity b) {
		if(a == null || b == null || a.getworld() == null || b.getworld() == null) return false;
		return a.xCoord == b.xCoord && a.yCoord == b.yCoord && a.zCoord == b.zCoord
				&& a.getworld().provider.dimensionId == b.getworld().provider.dimensionId;
	}
	public static Block getBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y, z);
	}*/

	public static int getCurrentClientDimension() {
		return AsieLibMod.proxy.getCurrentClientDimension();
	}

	public static void notifyBlockUpdate(World world, BlockPos pos) {
		notifyBlockUpdate(world, pos, world.getBlockState(pos));
	}

	public static void notifyBlockUpdate(@Nullable World world, BlockPos pos, IBlockState state) {
		if(world != null) {
			world.notifyBlockUpdate(pos, state, state, 11);
		}
	}
	
	/*public static void sendParticlePacket(String name, World world, double x, double y, double z, double vx, double vy, double vz) {
		try {
			Packet pkt = AsieLibMod.packet.create(Packets.SPAWN_PARTICLE)
				.writeFloat((float)x).writeFloat((float)y).writeFloat((float)z)
				.writeFloat((float)vx).writeFloat((float)vy).writeFloat((float)vz)
				.writeString(name);
			AsieLibMod.packet.sendToAllAround(pkt, new TargetPoint(world.provider.dimensionId, x, y, z, 64.0D));
		} catch(Exception e) { e.printStackTrace(); }
    }*/
}
