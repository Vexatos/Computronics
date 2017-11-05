package pl.asie.computronics.api.audio;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IAudioReceiver extends IAudioConnection {

	@Nullable
	World getSoundWorld();

	Vec3d getSoundPos();

	int getSoundDistance();

	void receivePacket(AudioPacket packet, @Nullable EnumFacing side);
}
