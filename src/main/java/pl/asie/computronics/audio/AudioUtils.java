package pl.asie.computronics.audio;

import net.minecraft.util.math.BlockPos;
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

	public static String positionId(BlockPos pos) {
		return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
	}
}
