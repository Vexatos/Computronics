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
		for(Textures t : Textures.VALUES) {
			if(event.map.getTextureType() == t.getType()) {
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
		DRONE_STATION_BOTTOM("drone_station_bottom");
		//DRONE_STATION_SIDE("machine_top", 0, "computronics:"),
		//DRONE_STATION_NOOK_TOP("machine_top", 0, "computronics:"),
		//DRONE_STATION_NOOK_SIDE("machine_top", 0, "computronics:");

		private IIcon icon;
		private final String location;
		private final int type;
		private final String path;
		public static final Textures[] VALUES = values();

		private Textures(String location) {
			this(location, 1);
		}

		private Textures(String location, int type) {
			this(location, type, "computronics:buildcraft/pluggable/");
		}

		private Textures(String location, int type, String path) {
			this.location = location;
			this.type = type;
			this.path = path;
		}

		public int getType() {
			return this.type;
		}

		public IIcon getIcon() {
			return icon;
		}

		public void registerIcons(IIconRegister iconRegister) {
			this.icon = iconRegister.registerIcon(path + location);
		}
	}
}
