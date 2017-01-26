package pl.asie.computronics.integration.buildcraft;

import buildcraft.api.transport.PipeManager;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.buildcraft.pluggable.DroneStationPluggable;
import pl.asie.computronics.integration.buildcraft.pluggable.DroneStationRenderer;
import pl.asie.computronics.integration.buildcraft.pluggable.ItemDockingUpgrade;
import pl.asie.computronics.integration.buildcraft.pluggable.ItemDroneStation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RecipeUtils;

/**
 * @author Vexatos
 */
public class IntegrationBuildCraft {

	public ItemDroneStation droneStationItem;
	public ItemDockingUpgrade dockingUpgrade;

	@Optional.Method(modid = Mods.OpenComputers)
	public void preInitOC() {
		Computronics.log.info("Registering Drone Docking Station for OpenComputers");
		droneStationItem = new ItemDroneStation();
		GameRegistry.registerItem(droneStationItem, "computronics.droneStation");
		dockingUpgrade = new ItemDockingUpgrade();
		GameRegistry.registerItem(dockingUpgrade, "computronics.dockingUpgrade");
		Driver.add((Item) dockingUpgrade);
		Driver.add((EnvironmentProvider) dockingUpgrade);
		if(Computronics.proxy.isClient()) {
			MinecraftForge.EVENT_BUS.register(new DroneStationRenderer.TextureHandler());
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void initOC() {
		if(Computronics.proxy.isClient()) {
			MinecraftForgeClient.registerItemRenderer(this.droneStationItem, new DroneStationRenderer.ItemRenderer());
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void postInitOC() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		ItemStack robotStation = GameRegistry.findItemStack(Mods.BuildCraftTransport, "robotStation", 1);
		if(robotStation == null || robotStation.getItem() == null) {
			robotStation = new ItemStack(Items.ender_pearl, 1, 0);
		}
		RecipeUtils.addShapedRecipe(new ItemStack(droneStationItem, 1, 0),
			" a ", "tst", " c ", 's', robotStation, 'a', "oc:circuitChip1",
			'c', "oc:cable", 't', "oc:materialTransistor"
		);
		ItemStack pipe = GameRegistry.findItemStack(Mods.BuildCraftTransport, "item.buildcraftPipe.pipeitemsquartz", 1);
		Object p = pipe;
		if(pipe == null || pipe.getItem() == null) {
			p = "oc:cable";
		}
		RecipeUtils.addShapedRecipe(new ItemStack(dockingUpgrade, 1, 0),
			" a ", "tst", " c ", 's', new ItemStack(droneStationItem, 1, 0), 'a', "oc:circuitChip1",
			'c', p, 't', "oc:materialTransistor"
		);
	}
}
