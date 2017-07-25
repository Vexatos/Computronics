package pl.asie.computronics;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Sounds;
import pl.asie.lib.network.Packet;

import java.io.IOException;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void registerAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(AudioPacketDFPWM.class);
		AudioPacketRegistry.INSTANCE.registerType(SoundCardPacket.class);
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(new ResourceLocation(Mods.Computronics, "tape_item"), EntityItemIndestructable.class, "tape_item", 1, Computronics.instance, 64, 20, true);
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
		Sounds.registerSounds();
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}

	public void spawnSwarmParticle(World world, double xPos, double yPos, double zPos, int color) {
		//NO-OP
	}

	public void onServerStop() {

	}
}
