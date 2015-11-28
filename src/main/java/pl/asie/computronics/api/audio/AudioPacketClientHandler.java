package pl.asie.computronics.api.audio;

import java.io.IOException;

import pl.asie.lib.network.Packet;
import pl.asie.lib.util.WorldUtils;

public abstract class AudioPacketClientHandler {
	protected abstract void readData(Packet packet, int packetId, int sourceId) throws IOException;
	protected abstract void playData(int packetId, int sourceId, int x, int y, int z, int distance, byte volume);

	public final void receivePacket(Packet packet) throws IOException {
		int packetId = packet.readInt();
		int sourceId = packet.readInt();

		readData(packet, packetId, sourceId);

		short receiverCount = packet.readShort();

		for (int j = 0; j < receiverCount; j++) {
			int dimension = packet.readInt();
			int x = packet.readInt();
			int y = packet.readInt();
			int z = packet.readInt();
			int distance = packet.readUnsignedShort();
			byte volume = packet.readByte();

			if (dimension != WorldUtils.getCurrentClientDimension()) {
				continue;
			}
			
			playData(packetId, sourceId, x, y, z, distance, volume);
		}
	}
}
