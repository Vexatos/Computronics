package pl.asie.computronics.audio;

import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

public interface IAudioReceiver {
	World getSoundWorld();
	int getSoundX();
	int getSoundY();
	int getSoundZ();
	int getSoundDistance();
	byte getSoundVolume(); // 0-127
	void receivePacket(AudioPacket packet, ForgeDirection side);
}
