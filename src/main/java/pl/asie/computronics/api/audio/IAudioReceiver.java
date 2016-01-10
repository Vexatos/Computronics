package pl.asie.computronics.api.audio;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IAudioReceiver extends IAudioConnection {

	World getSoundWorld();

	BlockPos getSoundPos();

	int getSoundDistance();

	void receivePacket(AudioPacket packet, EnumFacing side);
}
