package pl.asie.computronics.api.audio;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
		if(receiver.getSoundWorld() != null) {
			receivers.add(receiver);
		}
	}

	protected abstract void writeData(Packet p) throws IOException;

	protected boolean canHearReceiver(EntityPlayerMP playerMP, IAudioReceiver receiver) {
		if(receiver.getSoundWorld() != null && receiver.getSoundWorld().provider.getDimension() != playerMP.world.provider.getDimension()) {
			return false;
		}

		int mdSq = receiver.getSoundDistance() * receiver.getSoundDistance();
		final Vec3d pos = receiver.getSoundPos();
		double distSq = (pos.x - playerMP.posX) * (pos.x - playerMP.posX);
		distSq += (pos.y - playerMP.posY) * (pos.y - playerMP.posY);
		distSq += (pos.z - playerMP.posZ) * (pos.z - playerMP.posZ);
		return distSq <= mdSq;
	}

	public final void sendPacket() {
		try {
			for(EntityPlayerMP playerMP : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
				if(playerMP == null || playerMP.world == null) {
					continue;
				}

				Set<IAudioReceiver> receiversLocal = new HashSet<IAudioReceiver>();

				for(IAudioReceiver receiver : receivers) {
					if(canHearReceiver(playerMP, receiver)) {
						receiversLocal.add(receiver);
					}
				}

				if(receiversLocal.size() > 0) {
					Packet pkt = Computronics.packet.create(PacketType.AUDIO_DATA.ordinal())
						.writeShort((short) AudioPacketRegistry.INSTANCE.getId(this.getClass()))
						.writeInt(id).writeInt(source.getSourceId());

					writeData(pkt);

					pkt.writeShort((short) receivers.size());

					for(IAudioReceiver receiver : receivers) {
						pkt.writeInt(receiver.getSoundWorld() != null ? receiver.getSoundWorld().provider.getDimension() : 0);
						final Vec3d pos = receiver.getSoundPos();
						pkt.writeFloat((float) pos.x).writeFloat((float) pos.y).writeFloat((float) pos.z)
							.writeShort((short) receiver.getSoundDistance()).writeByte(volume);
					}

					Computronics.packet.sendTo(pkt, playerMP);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
