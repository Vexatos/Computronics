package pl.asie.computronics.audio;

import net.minecraft.util.MathHelper;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

public final class AudioUtils {

	private AudioUtils() {

	}

	public static synchronized void removePlayer(int managerId, int codecId) {
		AudioPacketRegistry.INSTANCE.getManager(managerId).removePlayer(codecId);
		try {
			Packet pkt = Computronics.packet.create(PacketType.AUDIO_STOP.ordinal())
				.writeInt(managerId)
				.writeInt(codecId);
			Computronics.packet.sendToAll(pkt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String positionId(int x, int y, int z) {
		return String.format("(%d, %d, %d)", x, y, z);
	}

	public static String positionId(double x, double y, double z) {
		return positionId(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
	}
}
