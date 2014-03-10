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
	public void handlePacket(INetworkManager manager, PacketOutput packet, int command, Player player,
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
				codec.playPacket(audio, x, y, z);
				codec.lastPacketId = packetId;
			} break;
			case Packets.PACKET_AUDIO_STOP: {
				if(!isClient) return;
				int codecId = packet.readInt();
				Computronics.instance.audio.removePlayer(codecId);
			} break;
		}
	}
}
