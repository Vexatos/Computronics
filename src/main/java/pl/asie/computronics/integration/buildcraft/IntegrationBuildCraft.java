/*package pl.asie.computronics.integration.buildcraft;

/**
 * @author Vexatos
 * /
public class IntegrationBuildCraft {

	//public ItemDroneStation droneStationItem;
	//public ItemDockingUpgrade dockingUpgrade;

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
			//MinecraftForgeClient.registerItemRenderer(this.droneStationItem, new DroneStationRenderer.ItemRenderer());
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void postInitOC() {
		PipeManager.registerPipePluggable(DroneStationPluggable.class, "computronics.droneStation");

		net.minecraft.item.Item item = Item.itemRegistry.getObject(new ResourceLocation(Mods.BuildCraftTransport, "robotStation"));
		ItemStack robotStation;
		if(item != null) {
			robotStation = new ItemStack(item, 1);
		} else {
			robotStation = new ItemStack(Items.ender_pearl, 1, 0);
		}
		RecipeUtils.addShapedRecipe(new ItemStack(droneStationItem, 1, 0),
			" a ", "tst", " c ", 's', robotStation, 'a', "oc:circuitChip1",
			'c', "oc:cable", 't', "oc:materialTransistor"
		);
		item = Item.itemRegistry.getObject(new ResourceLocation(Mods.BuildCraftTransport, "item.buildcraftPipe.pipeitemsquartz"));
		ItemStack pipe;
		if(item != null) {
			pipe = new ItemStack(item, 1);
		} else {
			pipe = "oc:cable";
		}
		RecipeUtils.addShapedRecipe(new ItemStack(dockingUpgrade, 1, 0),
			" a ", "tst", " c ", 's', new ItemStack(droneStationItem, 1, 0), 'a', "oc:circuitChip1",
			'c', p, 't', "oc:materialTransistor"
		);
	}
}*/
