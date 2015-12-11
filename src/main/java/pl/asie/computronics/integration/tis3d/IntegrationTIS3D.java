package pl.asie.computronics.integration.tis3d;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.ModuleAPI;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationTIS3D {

	@Optional.Method(modid = Mods.TIS3D)
	public void preInit() {
		if(Computronics.proxy.isClient()) {
			MinecraftForge.EVENT_BUS.register(new TextureLoader());
		}
	}

	@Optional.Method(modid = Mods.TIS3D)
	public void init() {
		ModuleAPI.addProvider(new ModuleProviderColorful());
		ModuleAPI.addProvider(new ModuleProviderTapeReader());
	}

	public static class TextureLoader {

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void textureHook(TextureStitchEvent.Pre event) {
			if(event.map.getTextureType() == 0) {
				for(Textures t : Textures.VALUES) {
					t.registerIcon(event.map);
				}
			}
		}

		enum Textures {
			TAPE_READER_BACK("drone_station_top"),
			TAPE_READER_OFF("drone_station_bottom"),
			TAPE_READER_ON("drone_station_side");

			@SideOnly(Side.CLIENT)
			private IIcon icon;
			private final String location;
			public static final Textures[] VALUES = values();

			Textures(String location) {
				this.location = location;
			}

			public IIcon getIcon() {
				return icon;
			}

			@SideOnly(Side.CLIENT)
			public void registerIcon(IIconRegister iconRegister) {
				this.icon = iconRegister.registerIcon("computronics:buildcraft/pluggable/" + location);
			}
		}
	}
}
