package pl.asie.computronics.integration.tis3d;

import li.cil.tis3d.api.ModuleAPI;
import li.cil.tis3d.api.SerialAPI;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.flamingo.DriverFlamingo;
import pl.asie.computronics.integration.tis3d.item.ItemModules;
import pl.asie.computronics.integration.tis3d.manual.ComputronicsPathProvider;
import pl.asie.computronics.integration.tis3d.module.ModuleBoom.BoomHandler;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;

/**
 * @author Vexatos
 */
public class IntegrationTIS3D {

	public static ItemModules itemModules;
	public static BoomHandler boomHandler;

	@Optional.Method(modid = Mods.TIS3D)
	public void preInit() {
		if(Config.TIS3D_MODULE_COLORFUL
			|| Config.TIS3D_MODULE_TAPE_READER
			|| Config.TIS3D_MODULE_BOOM) {

			itemModules = new ItemModules();
			Computronics.instance.registerItem(itemModules, "modules.tis3d");
			itemModules.registerItemModels();
			/*if(Computronics.proxy.isClient()) {
				//MinecraftForge.EVENT_BUS.register(new TextureLoader());
				//MinecraftForgeClient.registerItemRenderer(itemModules, new ComputronicsModuleRenderer().setIgnoreLighting(true));
			}*/
		}
		if(Config.TIS3D_MODULE_BOOM) {
			MinecraftForge.EVENT_BUS.register(boomHandler = new BoomHandler());
		}
	}

	@Optional.Method(modid = Mods.TIS3D)
	public void init(Compat compat) {
		ComputronicsPathProvider.initialize();
		ModuleAPI.addProvider(itemModules);

		if(Mods.isLoaded(Mods.Flamingo)) {
			if(compat.isCompatEnabled(Compat.Flamingo)) {
				SerialAPI.addProvider(new DriverFlamingo.TISInterfaceProvider());
			}
		}
	}

	@Optional.Method(modid = Mods.TIS3D)
	public void postInit() {
		if(itemModules != null) {
			if(Config.TIS3D_MODULE_COLORFUL) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemModules, 2, 0),
					"PPP", "IGI", " R ",
					'P', "paneGlassColorless",
					'I', "ingotIron",
					'R', "dustRedstone",
					'G', "dustGlowstone");
			}
			if(Config.TIS3D_MODULE_TAPE_READER) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemModules, 2, 1),
					"PPP", "IGI", " R ",
					'P', "paneGlassColorless",
					'I', "ingotIron",
					'R', "dustRedstone",
					'G', "gemDiamond");
			}
			if(Config.TIS3D_MODULE_BOOM) {
				RecipeUtils.addShapedRecipe(new ItemStack(itemModules, 2, 2),
					"PPP", "IGI", " R ",
					'P', "paneGlassColorless",
					'I', "ingotIron",
					'R', "dustRedstone",
					'G', Blocks.TNT);
			}
		}
	}

/*
	public static class TextureLoader {

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void textureHook(TextureStitchEvent.Pre event) {
			if(event.map.getTextureType() == 0) {
				for(Textures t : Textures.VALUES) {
					t.registerSprite(event.map);
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

			public IIcon getSprite() {
				return icon;
			}

			@SideOnly(Side.CLIENT)
			public void registerSprite(IIconRegister iconRegister) {
				this.icon = iconRegister.registerSprite("computronics:" + location);
			}
		}
	}*/
}
