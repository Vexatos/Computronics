package pl.asie.computronics.integration.railcraft;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import pl.asie.computronics.Computronics;
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

	public IntegrationRailcraft(Computronics computronics) {

		if(computronics.isEnabled("railcraftLocomotiveRelay", true)) {
			locomotiveRelay = new BlockLocomotiveRelay();
			GameRegistry.registerBlock(locomotiveRelay, "computronics.locomotiveRelay");
			GameRegistry.registerTileEntity(TileLocomotiveRelay.class, "computronics.locomotiveRelay");

			relaySensor = new ItemRelaySensor();
			GameRegistry.registerItem(relaySensor, "computronics.relaySensor");
		}
		if(computronics.isEnabled("railcraftDigitalReceiverBox", true)) {
			this.digitalBox = new BlockDigitalReceiverBox();
			GameRegistry.registerBlock(digitalBox, "computronics.digitalBox");
			GameRegistry.registerTileEntity(TileDigitalReceiverBox.class, "computronics.digitalBox");
		}
		if(computronics.isEnabled("railcraftDigitalDetector", true)) {
			detector = new BlockDigitalDetector();
			GameRegistry.registerBlock(detector, "computronics.detector");
			GameRegistry.registerTileEntity(TileDigitalDetector.class, "computronics.detector");
		}
	}
}
