package pl.asie.computronics;

import java.io.IOException;

import net.minecraft.world.World;

import cpw.mods.fml.common.registry.EntityRegistry;

import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.lib.network.Packet;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void registerAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(AudioPacketDFPWM.class);
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "computronics.itemTape", 1, Computronics.instance, 64, 20, true);
	}

	public void registerRenderers() {
		//NO-OP
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}

	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
		//NO-OP
	}
}
