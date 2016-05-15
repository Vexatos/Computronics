package pl.asie.computronics.audio;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

public final class AudioUtils {

	private AudioUtils() {

	}

	public static void removePlayer(int id) {
		Computronics.instance.audio.removePlayer(id);
		try {
			Packet pkt = Computronics.packet.create(PacketType.AUDIO_STOP.ordinal())
				.writeInt(id);
			Computronics.packet.sendToAll(pkt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
