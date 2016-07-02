package pl.asie.computronics;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.audio.codec.Codec;
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
		AudioPacketRegistry.INSTANCE.registerCodec(Codec.DFPWM);
		AudioPacketRegistry.INSTANCE.registerCodec(Codec.DFPWM1a);
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "itemTape", 1, Computronics.instance, 64, 20, true);
	}

	public void registerItemModel(Item item, int meta, String name) {

	}

	public void registerItemModel(Block block, int meta, String name) {
		registerItemModel(Item.getItemFromBlock(block), meta, name);
	}

	public void preInit() {
		registerAudioHandlers();
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
