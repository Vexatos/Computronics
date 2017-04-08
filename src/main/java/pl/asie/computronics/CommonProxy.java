package pl.asie.computronics;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.world.World;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void registerAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(AudioPacketDFPWM.class);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			registerOpenComputersAudioHandlers();
		}
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "computronics.itemTape", 1, Computronics.instance, 64, 20, true);
	}

	public void init() {
		//NO-OP
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}

	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
		//NO-OP
	}

	public void onServerStop() {

	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void registerOpenComputersAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(SoundCardPacket.class);
	}
}
