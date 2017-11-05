package pl.asie.computronics.api.audio;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IAudioReceiver extends IAudioConnection {
	World getSoundWorld();
	Vec3 getSoundPos();
	int getSoundDistance();
	void receivePacket(AudioPacket packet, ForgeDirection side);

	String getID();
}
