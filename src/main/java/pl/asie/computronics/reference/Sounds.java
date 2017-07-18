package pl.asie.computronics.reference;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author Vexatos
 */
public enum Sounds {
	TAPE_INSERT("tape_insert"),
	TAPE_EJECT("tape_eject");

	public SoundEvent event;
	public final ResourceLocation loc;

	Sounds(String name) {
		loc = new ResourceLocation(Mods.Computronics, name);
	}

	public static void registerSounds() {
		for(Sounds sound : values()) {
			if(SoundEvent.REGISTRY.containsKey(sound.loc)) {
				sound.event = SoundEvent.REGISTRY.getObject(sound.loc);
			} else {
				sound.event = new SoundEvent(sound.loc);
				GameRegistry.findRegistry(SoundEvent.class).register(sound.event.setRegistryName(sound.loc));
			}
		}
	}
}
