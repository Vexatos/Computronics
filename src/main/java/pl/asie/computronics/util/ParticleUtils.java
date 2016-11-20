package pl.asie.computronics.util;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

import java.util.HashMap;
import java.util.Map;

public class ParticleUtils {

	public static void sendParticlePacket(EnumParticleTypes particle, World world, double x, double y, double z, double vx, double vy, double vz) {
		try {
			Packet pkt = Computronics.packet.create(PacketType.PARTICLE_SPAWN.ordinal())
				.writeFloat((float) x).writeFloat((float) y).writeFloat((float) z)
				.writeFloat((float) vx).writeFloat((float) vy).writeFloat((float) vz)
				.writeInt(particle.getParticleID());
			Computronics.packet.sendToAllAround(pkt, new TargetPoint(world.provider.getDimension(), x, y, z, 64.0D));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, EnumParticleTypes> particleTypeMap = null;

	private static Map<String, EnumParticleTypes> particleMap() {
		if(particleTypeMap == null) {
			particleTypeMap = new HashMap<String, EnumParticleTypes>();
			for(EnumParticleTypes type : EnumParticleTypes.values()) {
				particleTypeMap.put(type.getParticleName(), type);
			}
		}
		return particleTypeMap;
	}

	public static boolean isValidParticleType(String name) {
		return particleMap().containsKey(name);
	}

	public static EnumParticleTypes getParticleType(String name) {
		return particleMap().get(name);
	}
}
