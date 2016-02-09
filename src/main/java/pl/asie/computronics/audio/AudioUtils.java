package pl.asie.computronics.audio;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets;
import pl.asie.lib.network.Packet;

public final class AudioUtils {
	private AudioUtils() {

	}

	public static void removePlayer(int id) {
		Computronics.instance.audio.removePlayer(id);
		try {
			Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_STOP)
					.writeInt(id);
			Computronics.packet.sendToAll(pkt);
		} catch(Exception e) { e.printStackTrace(); }
	}
}
