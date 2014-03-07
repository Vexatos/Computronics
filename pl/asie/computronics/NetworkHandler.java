package pl.asie.computronics;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystem;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.audio.DFPWMCodec;
import pl.asie.lib.network.NetworkHandlerBase;
import pl.asie.lib.network.PacketOutput;
import pl.asie.lib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler extends NetworkHandlerBase implements IPacketHandler {
	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(32768, 8, 1, false, false);
	
	@Override
	public void handlePacket(PacketOutput packet, int command, Player player,
			boolean isClient) throws IOException {
		switch(command) {
			case Packets.PACKET_AUDIO_DATA: {
				if(!isClient) return;
				int dimId = packet.readInt();
				int x = packet.readInt();
				int y = packet.readInt();
				int z = packet.readInt();
				int packetId = packet.readInt();
				int codecId = packet.readInt();
				byte[] data = packet.readByteArrayData(1024);

				//SoundSystem sound = Minecraft.getMinecraft().sndManager.sndSystem;
				byte[] audio = new byte[8192];
				String sourceName = "dfpwm_"+codecId;
				DFPWMCodec codec = Computronics.instance.audio.getPlayer(codecId);

				if(dimId != WorldUtils.getCurrentClientDimension()) return;
				
				codec.decompress(audio, data, 0, 0, 1024);
				for(int i = 0; i < 8192; i++) {
					// Convert signed to unsigned data
					audio[i] = (byte)(((int)audio[i] & 0xFF) ^ 0x80);
				}
				
				if((codec.lastPacketId + 1) != packetId) {
					codec.reset();
				}
				// Feed data and work around paulscode bug
				/*if(!sound.playing(sourceName)) {
					sound.feedRawAudioData(sourceName, new byte[16]);
					sound.setPosition(sourceName, (float)x, (float)y, (float)z);
					sound.setLooping(sourceName, false);
	                sound.setVolume(sourceName, 0.5F * Minecraft.getMinecraft().gameSettings.soundVolume);
	                sound.pause(sourceName);
				}*/
				/*sound.feedRawAudioData(sourceName, audio);
				if(!sound.playing(sourceName) && packetId > 0 && ((codec.lastPacketId + 1) == packetId)) {
					// Won't play at first if it's the first packet in a series
					sound.play(sourceName);
				}*/
				codec.playPacket(audio, x, y, z);
				codec.lastPacketId = packetId;
			} break;
			case Packets.PACKET_AUDIO_STOP: {
				if(!isClient) return;
				//SoundSystem sound = Minecraft.getMinecraft().sndManager.sndSystem;
				int codecId = packet.readInt();
				Computronics.instance.audio.removePlayer(codecId);
				//sound.stop("dfpwm_"+codecId);
				//sound.removeSource("dfpwm_"+codecId);
			} break;
		}
	}
}
