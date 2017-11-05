package pl.asie.computronics.api.audio;

import pl.asie.lib.network.Packet;
import pl.asie.lib.util.WorldUtils;

import java.io.IOException;

public abstract class AudioPacketClientHandler {
	protected abstract void readData(Packet packet, int packetId, int sourceId) throws IOException;

	@Deprecated
	protected void playData(int packetId, int sourceId, int x, int y, int z, int distance, byte volume) {
		playData(packetId, sourceId, x + 0.5F, y + 0.5F, z + 0.5F, distance, volume, "");
	}

	protected abstract void playData(int packetId, int sourceId, float x, float y, float z, int distance, byte volume, String deviceId);

	public final void receivePacket(Packet packet) throws IOException {
		int packetId = packet.readInt();
		int sourceId = packet.readInt();

		readData(packet, packetId, sourceId);

		short receiverCount = packet.readShort();

		for (int j = 0; j < receiverCount; j++) {
			int dimension = packet.readInt();
			float x = packet.readFloat();
			float y = packet.readFloat();
			float z = packet.readFloat();
			int distance = packet.readUnsignedShort();
			byte volume = packet.readByte();
			String deviceId = packet.readString();

			if (dimension != WorldUtils.getCurrentClientDimension()) {
				continue;
			}

			playData(packetId, sourceId, x, y, z, distance, volume, deviceId);
		}
	}
}
