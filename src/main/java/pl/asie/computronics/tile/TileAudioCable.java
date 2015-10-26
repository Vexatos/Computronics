package pl.asie.computronics.tile;

import gnu.trove.set.hash.TIntHashSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.computronics.audio.AudioPacket;
import pl.asie.computronics.audio.IAudioReceiver;
import pl.asie.computronics.audio.IAudioSource;
import pl.asie.lib.block.TileEntityBase;

public class TileAudioCable extends TileEntityBase implements IAudioReceiver {
	private final TIntHashSet packetIds = new TIntHashSet();

	public boolean connects(ForgeDirection dir) {
		TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
		return (tile instanceof IAudioSource || tile instanceof IAudioReceiver);
	}

	@Override
	public void updateEntity() {
		packetIds.clear();
	}

	@Override
	public void receivePacket(AudioPacket packet, ForgeDirection side) {
		if (packetIds.contains(packet.id)) {
			return;
		}

		packetIds.add(packet.id);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir == side) {
				continue;
			}

			if (!worldObj.blockExists(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (tile instanceof IAudioReceiver) {
				((IAudioReceiver) tile).receivePacket(packet, dir.getOpposite());
			}
		}
	}

	@Override
	public World getSoundWorld() {
		return null;
	}

	@Override
	public int getSoundX() {
		return 0;
	}

	@Override
	public int getSoundY() {
		return 0;
	}

	@Override
	public int getSoundZ() {
		return 0;
	}

	@Override
	public int getSoundDistance() {
		return 0;
	}
}
