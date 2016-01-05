package pl.asie.computronics.audio;

import java.io.IOException;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketClientHandler;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.network.Packet;

@SideOnly(Side.CLIENT)
public class AudioPacketClientHandlerDFPWM extends AudioPacketClientHandler {
	@Override
	protected void readData(Packet packet, int packetId, int codecId) throws IOException {
		int sampleRate = packet.readInt();
		short packetSize = packet.readShort();
		byte[] data = packet.readByteArrayData(packetSize);

		byte[] audio = new byte[packetSize * 8];
		StreamingAudioPlayer codec = Computronics.instance.audio.getPlayer(codecId);
		codec.decompress(audio, data, 0, 0, packetSize);
		for (int i = 0; i < (packetSize * 8); i++) {
			// Convert signed to unsigned data
			audio[i] = (byte) (((int) audio[i] & 0xFF) ^ 0x80);
		}

		codec.setSampleRate(sampleRate);
		codec.lastPacketId = packetId;

		codec.queueData(audio);
	}

	@Override
	protected void playData(int packetId, int codecId, int x, int y, int z, int distance, byte volume) {
		StreamingAudioPlayer codec = Computronics.instance.audio.getPlayer(codecId);

		codec.setDistance((float) distance);
		codec.setVolume(volume / 127.0F);
		codec.playPacket(x, y, z);
	}
}
