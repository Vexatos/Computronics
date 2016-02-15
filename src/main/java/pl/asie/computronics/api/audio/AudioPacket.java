package pl.asie.computronics.api.audio;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AudioPacket {
	private static int _idGen;
	private static int getNewId() {
		return _idGen++;
	}

	public final IAudioSource source;
	public final int id;
	public final byte volume;

	private final Set<IAudioReceiver> receivers = new HashSet<IAudioReceiver>();

	public AudioPacket(IAudioSource source, byte volume) {
		this.id = getNewId();
		this.source = source;
		this.volume = volume;
	}

	public Collection<IAudioReceiver> getReceivers() {
		return Collections.unmodifiableSet(receivers);
	}

	public void addReceiver(IAudioReceiver receiver) {
		if (receiver.getSoundWorld() != null) {
			receivers.add(receiver);
		}
	}

	protected abstract void writeData(Packet p) throws IOException;

	protected boolean canHearReceiver(EntityPlayerMP playerMP, IAudioReceiver receiver) {
		if (receiver.getSoundWorld().provider.dimensionId != playerMP.worldObj.provider.dimensionId) {
			return false;
		}

		int mdSq = receiver.getSoundDistance() * receiver.getSoundDistance();
		double distSq = (receiver.getSoundX() - playerMP.posX) * (receiver.getSoundX() - playerMP.posX);
		distSq += (receiver.getSoundY() - playerMP.posY) * (receiver.getSoundY() - playerMP.posY);
		distSq += (receiver.getSoundZ() - playerMP.posZ) * (receiver.getSoundZ() - playerMP.posZ);
		return distSq <= mdSq;
	}

	@SuppressWarnings("unchecked")
	public final void sendPacket() {
		try {
			for (EntityPlayerMP playerMP : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if (playerMP == null || playerMP.worldObj == null) {
					continue;
				}

				Set<IAudioReceiver> receiversLocal = new HashSet<IAudioReceiver>();

				for (IAudioReceiver receiver : receivers) {
					if (canHearReceiver(playerMP, receiver)) {
						receiversLocal.add(receiver);
					}
				}

				if (receiversLocal.size() > 0) {
					Packet pkt = Computronics.packet.create(PacketType.AUDIO_DATA.ordinal())
							.writeShort((short) AudioPacketRegistry.INSTANCE.getId(this.getClass()))
							.writeInt(id).writeInt(source.getSourceId());

					writeData(pkt);

					pkt.writeShort((short) receivers.size());

					for (IAudioReceiver receiver : receivers) {
						pkt.writeInt(receiver.getSoundWorld() != null ? receiver.getSoundWorld().provider.dimensionId : 0);
						pkt.writeInt(receiver.getSoundX()).writeInt(receiver.getSoundY()).writeInt(receiver.getSoundZ())
								.writeShort((short) receiver.getSoundDistance()).writeByte(volume);
					}

					Computronics.packet.sendTo(pkt, playerMP);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
