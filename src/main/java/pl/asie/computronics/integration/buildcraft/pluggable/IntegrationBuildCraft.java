package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.PipeManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class IntegrationBuildCraft {

	public ItemDroneStation droneStationItem;
	public ItemDockingUpgrade dockingUpgrade;

	public void preInitOC() {
		Computronics.log.info("Registering Drone Docking Station for OpenComputers");
		droneStationItem = new ItemDroneStation();
		GameRegistry.registerItem(droneStationItem, "computronics.droneStation");
		dockingUpgrade = new ItemDockingUpgrade();
		GameRegistry.registerItem(dockingUpgrade, "computronics.dockingUpgrade");
		Driver.add(dockingUpgrade);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void textureHook(TextureStitchEvent.Pre event) {
		if(event.map.getTextureType() == 0) {
			for(Textures t : Textures.VALUES) {
				t.registerIcons(event.map);
			}
		}
	}

	public void postInitOC() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		//TODO Add recipes
	}

	public static enum Textures {
		DRONE_STATION_TOP("drone_station_top"),
		DRONE_STATION_BOTTOM("drone_station_bottom"),
		DRONE_STATION_SIDE("drone_station_side");
		//DRONE_STATION_NOOK_TOP("machine_top", 0, "computronics:"),
		//DRONE_STATION_NOOK_SIDE("machine_top", 0, "computronics:");

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
			return this.getMinU() + f * (float)par1 / 16.0F;
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
			return this.getMinV() + f * ((float)par1 / 16.0F);
		}

		@Override
		public String getIconName() {
			return icon.getIconName();
		}
	}
}
