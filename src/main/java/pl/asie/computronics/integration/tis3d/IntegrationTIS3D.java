package pl.asie.computronics.integration.tis3d;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.ModuleAPI;
import li.cil.tis3d.api.prefab.client.SimpleModuleRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;
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
			MinecraftForgeClient.registerItemRenderer(Computronics.itemParts, new SimpleModuleRenderer().setIgnoreLighting(true));
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
			CASING("tis3d/casingModule"),
			TAPE_READER_OFF("tape_drive_front");

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
				this.icon = iconRegister.registerIcon("computronics:" + location);
			}
		}
	}
}
