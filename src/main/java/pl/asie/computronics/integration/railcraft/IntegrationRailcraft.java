package pl.asie.computronics.integration.railcraft;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import pl.asie.computronics.block.BlockDigitalDetector;
import pl.asie.computronics.block.BlockDigitalReceiverBox;
import pl.asie.computronics.block.BlockLocomotiveRelay;
import pl.asie.computronics.item.ItemRelaySensor;
import pl.asie.computronics.tile.TileDigitalDetector;
import pl.asie.computronics.tile.TileDigitalReceiverBox;
import pl.asie.computronics.tile.TileLocomotiveRelay;

/**
 * @author Vexatos
 */
public class IntegrationRailcraft {
	public BlockLocomotiveRelay locomotiveRelay;
	public BlockDigitalDetector detector;
	public ItemRelaySensor relaySensor;
	public Block digitalBox;

	private static boolean isEnabled(Configuration config, String name, boolean def) {
		return config.get("enable.railcraft", name, def).getBoolean(def);
	}

	public IntegrationRailcraft(Configuration config) {

		if(isEnabled(config, "locomotiveRelay", true)) {
			locomotiveRelay = new BlockLocomotiveRelay();
			GameRegistry.registerBlock(locomotiveRelay, "computronics.locomotiveRelay");
			GameRegistry.registerTileEntity(TileLocomotiveRelay.class, "computronics.locomotiveRelay");

			relaySensor = new ItemRelaySensor();
			GameRegistry.registerItem(relaySensor, "computronics.relaySensor");

			//MinecraftForge.EVENT_BUS.register(this);
		}
		if(isEnabled(config, "digitalReceiverBox", true)) {
			this.digitalBox = new BlockDigitalReceiverBox();
			GameRegistry.registerBlock(digitalBox, "computronics.digitalBox");
			GameRegistry.registerTileEntity(TileDigitalReceiverBox.class, "computronics.digitalBox");
		}
		if(isEnabled(config, "digitalDetector", true)) {
			detector = new BlockDigitalDetector();
			GameRegistry.registerBlock(detector, "computronics.detector");
			GameRegistry.registerTileEntity(TileDigitalDetector.class, "computronics.detector");
		}
	}

	/*@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload e) {
		for(List entityList : e.getChunk().entityLists) {
			for(Object o : entityList) {
				if(o instanceof EntityLocomotiveElectric) {
					LinkageManager.instance().removeLinkageId((EntityLocomotiveElectric) o);
				}
			}
		}
	}*/
}
