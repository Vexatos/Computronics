package pl.asie.computronics.integration.charset.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.charset.api.audio.AudioPacket;
import pl.asie.charset.api.audio.IAudioReceiver;
import pl.asie.computronics.tile.TileAudioCable;

public class AudioReceiverCable implements IAudioReceiver {

	private final TileAudioCable cable;
	private final EnumFacing side;

	public AudioReceiverCable(TileAudioCable cable, EnumFacing side) {
		this.cable = cable;
		this.side = side;
	}

	@Override
	public boolean receive(AudioPacket packet) {
		if(!cable.receivePacketID(packet)) {
			return false;
		}

		World worldObj = cable.getWorld();
		boolean sent = false;

		for(EnumFacing dir : EnumFacing.VALUES) {
			if(dir == side || !cable.connectsAudio(dir)) {
				continue;
			}

			BlockPos pos = cable.getPos().offset(dir);
			if(!worldObj.isBlockLoaded(pos)) {
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(pos);
			if(tile != null && tile.hasCapability(IntegrationCharsetAudio.RECEIVER_CAPABILITY, dir.getOpposite())) {
				sent |= tile.getCapability(IntegrationCharsetAudio.RECEIVER_CAPABILITY, dir.getOpposite()).receive(packet);
			}
		}

		return sent;
	}
}
