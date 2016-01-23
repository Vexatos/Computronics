package pl.asie.computronics.util;

import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets;
import pl.asie.lib.network.Packet;

public class ParticleUtils {
	public static void sendParticlePacket(EnumParticleTypes particle, World worldObj, double x, double y, double z, double vx, double vy, double vz) {
		try {
			Packet pkt = Computronics.packet.create(Packets.PACKET_PARTICLE_SPAWN)
				.writeFloat((float)x).writeFloat((float)y).writeFloat((float)z)
				.writeFloat((float)vx).writeFloat((float)vy).writeFloat((float)vz)
				.writeInt(particle.getParticleID());
			Computronics.packet.sendToAllAround(pkt, new TargetPoint(worldObj.provider.getDimensionId(), x, y, z, 64.0D));
		} catch(Exception e) { e.printStackTrace(); }
    }
}
