package pl.asie.computronics.api.audio;

import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

public interface IAudioReceiver extends IAudioConnection {
	World getSoundWorld();
	int getSoundX();
	int getSoundY();
	int getSoundZ();
	int getSoundDistance();
	void receivePacket(AudioPacket packet, ForgeDirection side);
}
