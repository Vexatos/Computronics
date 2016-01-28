package pl.asie.computronics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.util.EnumParticleTypes;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.driver.DriverCardSound;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class NetworkHandlerClient extends MessageHandlerBase {

	private static class CodecData {

		public final int x, y, z;
		public final byte[] data;

		public CodecData(int x, int y, int z, byte[] data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.data = data;
		}
	}

	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(32768, 8, 1, false, false);

	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
		throws IOException {
		switch(command) {
			case Packets.PACKET_PARTICLE_SPAWN: {
				double x = packet.readFloat();
				double y = packet.readFloat();
				double z = packet.readFloat();
				double vx = packet.readFloat();
				double vy = packet.readFloat();
				double vz = packet.readFloat();
				int particle = packet.readInt();
				Minecraft.getMinecraft().thePlayer.getEntityWorld().spawnParticle(EnumParticleTypes.getParticleFromId(particle), x, y, z, vx, vy, vz);
			}
			break;
			case Packets.PACKET_COMPUTER_BEEP: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					DriverCardSound.onSound(packet, player);
				}
			}
			break;
			case Packets.PACKET_COMPUTER_BOOM: {
				if(Mods.isLoaded(Mods.OpenComputers)) {
					Computronics.proxy.goBoom(packet);
				}
			}
			break;
			/*case Packets.PACKET_TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, false);
				}
			}
			break;*/
		}
	}
}
