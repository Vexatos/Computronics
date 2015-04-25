package pl.asie.computronics.integration.railcraft;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import pl.asie.computronics.block.BlockDigitalDetector;
import pl.asie.computronics.block.BlockDigitalReceiverBox;
import pl.asie.computronics.block.BlockLocomotiveRelay;
import pl.asie.computronics.item.ItemRelaySensor;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileDigitalDetector;
import pl.asie.computronics.tile.TileDigitalReceiverBox;
import pl.asie.computronics.tile.TileLocomotiveRelay;
import pl.asie.computronics.tile.TileTicketMachine;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class IntegrationRailcraft {
	public BlockLocomotiveRelay locomotiveRelay;
	public BlockDigitalDetector detector;
	public ItemRelaySensor relaySensor;
	public Block digitalBox;

	LocomotiveManager manager;

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

			manager = new LocomotiveManager();
			MinecraftForge.EVENT_BUS.register(manager);
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

	@Optional.Method(modid = Mods.Railcraft)
	public void onMessageRailcraft(Packet packet, EntityPlayer player) throws IOException {
		TileEntity entity = packet.readTileEntityServer();
		if(entity instanceof TileTicketMachine) {
			TileTicketMachine machine = (TileTicketMachine) entity;
			int i = packet.readInt();
			machine.setLocked((i & 1) == 1);
			machine.setSelectionLocked(((i >> 1) & 1) == 1);
			machine.setPrintLocked((((i >> 2) & 1) == 1));
		}
	}
}
