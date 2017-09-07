package pl.asie.computronics.api.audio;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
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
		if(receiver.getSoundWorld().provider.getDimension() != playerMP.worldObj.provider.getDimension()) {
			return false;
		}

		int mdSq = receiver.getSoundDistance() * receiver.getSoundDistance();
		final BlockPos pos = receiver.getSoundPos();
		double distSq = (pos.getX() - playerMP.posX) * (pos.getX() - playerMP.posX);
		distSq += (pos.getY() - playerMP.posY) * (pos.getY() - playerMP.posY);
		distSq += (pos.getZ() - playerMP.posZ) * (pos.getZ() - playerMP.posZ);
		return distSq <= mdSq;
	}

	public final void sendPacket() {
		try {
			for(EntityPlayerMP playerMP : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
				if(playerMP == null || playerMP.worldObj == null) {
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
						final BlockPos pos = receiver.getSoundPos();
						pkt.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ())
							.writeShort((short) receiver.getSoundDistance()).writeByte(volume).writeByte((byte) (receiver.canMove() ? 1 : 0));
					}

					Computronics.packet.sendTo(pkt, playerMP);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
