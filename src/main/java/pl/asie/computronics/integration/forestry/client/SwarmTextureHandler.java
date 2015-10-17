package pl.asie.computronics.integration.forestry.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

/**
 * @author Vexatos
 */
public class SwarmTextureHandler {
	@SubscribeEvent
	public void textureHook(TextureStitchEvent.Pre event) {
		if(event.map.getTextureType() == 1) {
			for(Textures t : Textures.VALUES) {
				t.registerIcon(event.map);
			}
		}
	}

	public enum Textures {
		BEE_FX("forestry:particles/swarm_bee");

		private IIcon icon;
		private final String location;
		public static final Textures[] VALUES = values();

		Textures(String location) {
			this.location = location;
		}

		public IIcon getIcon() {
			return icon;
		}

		public void registerIcon(IIconRegister iconRegister) {
			this.icon = iconRegister.registerIcon(location);
		}
	}
}
