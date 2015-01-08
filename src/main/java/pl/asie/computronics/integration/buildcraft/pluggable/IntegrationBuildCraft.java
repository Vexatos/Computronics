package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.PipeManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationBuildCraft {

	public ItemDroneStation droneStationItem;
	public ItemDockingUpgrade dockingUpgrade;
	private IItemRenderer droneStationItemRenderer;

	public void preInitOC() {
		Computronics.log.info("Registering Drone Docking Station for OpenComputers");
		droneStationItem = new ItemDroneStation();
		GameRegistry.registerItem(droneStationItem, "computronics.droneStation");
		dockingUpgrade = new ItemDockingUpgrade();
		GameRegistry.registerItem(dockingUpgrade, "computronics.dockingUpgrade");
		Driver.add(dockingUpgrade);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void initOC() {
		if(Computronics.proxy.isClient()) {
			droneStationItemRenderer = new DroneStationRenderer.ItemRenderer();
			MinecraftForgeClient.registerItemRenderer(this.droneStationItem, droneStationItemRenderer);
		}
	}

	public void postInitOC() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		ItemStack robotStation = GameRegistry.findItemStack(Mods.BuildCraftTransport, "robotStation", 1);
		if(robotStation == null || robotStation.getItem() == null) {
			robotStation = new ItemStack(Items.ender_pearl, 1, 0);
		}
		GameRegistry.addShapedRecipe(new ItemStack(droneStationItem, 1, 0),
			" a ", "tst", " c ", 's', robotStation, 'a', li.cil.oc.api.Items.get("chip1").createItemStack(1),
			'c', li.cil.oc.api.Items.get("cable").createItemStack(1), 't', li.cil.oc.api.Items.get("transistor").createItemStack(1)
		);
		ItemStack pipe = GameRegistry.findItemStack(Mods.BuildCraftTransport, "item.buildcraftPipe.pipeitemsquartz", 1);
		if(pipe == null || pipe.getItem() == null) {
			pipe = li.cil.oc.api.Items.get("cable").createItemStack(1);
		}
		GameRegistry.addShapedRecipe(new ItemStack(dockingUpgrade, 1, 0),
			" a ", "tst", " c ", 's', new ItemStack(droneStationItem, 1, 0), 'a', li.cil.oc.api.Items.get("chip1").createItemStack(1),
			'c', pipe, 't', li.cil.oc.api.Items.get("transistor").createItemStack(1)
		);
	}

	@SubscribeEvent
	public void textureHook(TextureStitchEvent.Pre event) {
		if(event.map.getTextureType() == 0) {
			for(Textures t : Textures.VALUES) {
				t.registerIcons(event.map);
			}
		}
	}

	public static enum Textures {
		DRONE_STATION_TOP("drone_station_top"),
		DRONE_STATION_BOTTOM("drone_station_bottom"),
		DRONE_STATION_SIDE("drone_station_side");

		private IIcon icon;
		private final String location;
		public static final Textures[] VALUES = values();

		private Textures(String location) {
			this.location = location;
		}

		public IIcon getIcon() {
			return icon;
		}

		public void registerIcons(IIconRegister iconRegister) {
			this.icon = new WrappedIcon(iconRegister.registerIcon("computronics:buildcraft/pluggable/" + location));
		}
	}

	private static class WrappedIcon implements IIcon {

		private IIcon icon;
		private final int size;

		private WrappedIcon(IIcon icon) {
			this(icon, 2);
		}

		private WrappedIcon(IIcon icon, int size) {
			this.icon = icon;
			this.size = size;
		}

		@Override
		public int getIconWidth() {
			return icon.getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return icon.getIconHeight();
		}

		@Override
		public float getMinU() {
			return size > 0 ? icon.getMinU() - (icon.getMaxU() - icon.getMinU()) * size / 4F : icon.getMinU();
		}

		@Override
		public float getMaxU() {
			return size > 0 ? icon.getMaxU() + (icon.getMaxU() - icon.getMinU()) * size / 4F : icon.getMaxU();
		}

		@Override
		public float getInterpolatedU(double par1) {
			float f = this.getMaxU() - this.getMinU();
			return this.getMinU() + f * (float) par1 / 16.0F;
		}

		@Override
		public float getMinV() {
			float f = icon.getMaxV() - icon.getMinV();
			return size > 0 ? icon.getMinV() - f * size / 4F : icon.getMinV();
		}

		@Override
		public float getMaxV() {
			float f = icon.getMaxV() - icon.getMinV();
			return size > 0 ? icon.getMaxV() + f * size / 4F : icon.getMaxV();
		}

		@Override
		public float getInterpolatedV(double par1) {
			float f = this.getMaxV() - this.getMinV();
			return this.getMinV() + f * ((float) par1 / 16.0F);
		}

		@Override
		public String getIconName() {
			return icon.getIconName();
		}
	}
}
