package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.PipeManager;
import cpw.mods.fml.common.registry.GameRegistry;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class IntegrationBuildCraft {

	public ItemDroneStation droneStationItem;
	public ItemDockingUpgrade dockingUpgrade;

	public void preInit() {
		droneStationItem = new ItemDroneStation();
		droneStationItem.setCreativeTab(Computronics.tab);
		GameRegistry.registerItem(droneStationItem, "computronics.droneStation");
		dockingUpgrade = new ItemDockingUpgrade();
		GameRegistry.registerItem(droneStationItem, "computronics.dockingUpgrade");
	}

	public void postInit() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		//TODO Add recipes
	}
}
