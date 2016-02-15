package pl.asie.computronics.util;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

public class ParticleUtils {
	public static void sendParticlePacket(String name, World worldObj, double x, double y, double z, double vx, double vy, double vz) {
		try {
			Packet pkt = Computronics.packet.create(PacketType.PARTICLE_SPAWN.ordinal())
				.writeFloat((float)x).writeFloat((float)y).writeFloat((float)z)
				.writeFloat((float)vx).writeFloat((float)vy).writeFloat((float)vz)
				.writeString(name);
			Computronics.packet.sendToAllAround(pkt, new TargetPoint(worldObj.provider.dimensionId, x, y, z, 64.0D));
		} catch(Exception e) { e.printStackTrace(); }
    }
}
