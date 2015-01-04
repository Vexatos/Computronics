package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.PipeManager;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
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
	}

	public void postInitOC() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		//TODO Add recipes
	}
}
