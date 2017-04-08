package pl.asie.computronics.integration.railcraft;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.common.block.Item;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.buildcraft.IntegrationBuildCraftBuilder;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalDetector;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalReceiverBox;
import pl.asie.computronics.integration.railcraft.block.BlockLocomotiveRelay;
import pl.asie.computronics.integration.railcraft.block.BlockTicketMachine;
import pl.asie.computronics.integration.railcraft.client.SignalBoxRenderer;
import pl.asie.computronics.integration.railcraft.gui.GuiProviderTicketMachine;
import pl.asie.computronics.integration.railcraft.item.ItemBlockSignalBox;
import pl.asie.computronics.integration.railcraft.item.ItemRelaySensor;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalDetector;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalReceiverBox;
import pl.asie.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class IntegrationRailcraft {

	public BlockLocomotiveRelay locomotiveRelay;
	public BlockDigitalDetector detector;
	public ItemRelaySensor relaySensor;
	public Block digitalReceiverBox;
	public Block digitalControllerBox;
	public BlockTicketMachine ticketMachine;

	LocomotiveManager manager;
	public GuiProviderTicketMachine guiTicketMachine;

	private static boolean isEnabled(Configuration config, String name, boolean def) {
		return config.get("enable.railcraft", name, def).getBoolean(def);
	}

	public void preInit(Configuration config) {
		if(isEnabled(config, "locomotiveRelay", true)) {
			locomotiveRelay = new BlockLocomotiveRelay();
			GameRegistry.registerBlock(locomotiveRelay, "computronics.locomotiveRelay");
			GameRegistry.registerTileEntity(TileLocomotiveRelay.class, "computronics.locomotiveRelay");
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileLocomotiveRelay.class.getCanonicalName());
			IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(locomotiveRelay);

			relaySensor = new ItemRelaySensor();
			GameRegistry.registerItem(relaySensor, "computronics.relaySensor");

			manager = new LocomotiveManager();
			MinecraftForge.EVENT_BUS.register(manager);
		}
		if(isEnabled(config, "digitalReceiverBox", true)) {
			this.digitalReceiverBox = new BlockDigitalReceiverBox();
			GameRegistry.registerBlock(digitalReceiverBox, ItemBlockSignalBox.class, "computronics.digitalReceiverBox");
			GameRegistry.registerTileEntity(TileDigitalReceiverBox.class, "computronics.digitalReceiverBox");
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileDigitalReceiverBox.class.getCanonicalName());
		}
		if(isEnabled(config, "digitalControllerBox", true)) {
			this.digitalControllerBox = new BlockDigitalControllerBox();
			GameRegistry.registerBlock(digitalControllerBox, ItemBlockSignalBox.class, "computronics.digitalControllerBox");
			GameRegistry.registerTileEntity(TileDigitalControllerBox.class, "computronics.digitalControllerBox");
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileDigitalControllerBox.class.getCanonicalName());
		}
		if(isEnabled(config, "digitalDetector", true)) {
			detector = new BlockDigitalDetector();
			GameRegistry.registerBlock(detector, "computronics.detector");
			GameRegistry.registerTileEntity(TileDigitalDetector.class, "computronics.detector");
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileDigitalDetector.class.getCanonicalName());
			IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(detector);
		}
		if(isEnabled(config, "ticketMachine", true)) {
			this.guiTicketMachine = new GuiProviderTicketMachine();
			Computronics.gui.registerGuiProvider(guiTicketMachine);
			ticketMachine = new BlockTicketMachine();
			GameRegistry.registerBlock(ticketMachine, "computronics.ticketMachine");
			GameRegistry.registerTileEntity(TileTicketMachine.class, "computronics.ticketMachine");
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileTicketMachine.class.getCanonicalName());
			IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(ticketMachine);
		}
	}

	@Optional.Method(modid = Mods.Railcraft)
	public void onMessageRailcraft(Packet packet, EntityPlayer player, boolean isServer) throws IOException {
		TileEntity entity = isServer ? packet.readTileEntityServer() : packet.readTileEntity();
		if(entity instanceof TileTicketMachine) {
			TileTicketMachine machine = (TileTicketMachine) entity;
			int i = packet.readInt();
			machine.setLocked((i & 1) == 1, isServer);
			machine.setSelectionLocked(((i >> 1) & 1) == 1, isServer);
			machine.setPrintLocked((((i >> 2) & 1) == 1), isServer);
			machine.setActive((((i >> 3) & 1) == 1), isServer);
			machine.setSelectedSlot(packet.readInt(), isServer);
		}
	}

	@Optional.Method(modid = Mods.Railcraft)
	public void printTicket(Packet packet, EntityPlayer player, boolean isServer) throws IOException {
		TileEntity entity = isServer ? packet.readTileEntityServer() : packet.readTileEntity();
		if(entity instanceof TileTicketMachine) {
			((TileTicketMachine) entity).printTicket();
		}
	}

	public void remap(FMLMissingMappingsEvent e) {
		for(FMLMissingMappingsEvent.MissingMapping mapping : e.get()) {
			if(digitalReceiverBox != null && mapping.name.equals("computronics:computronics.digitalBox")) {
				switch(mapping.type) {
					case BLOCK: {
						mapping.remap(digitalReceiverBox);
						break;
					}
					case ITEM: {
						mapping.remap(Item.getItemFromBlock(digitalReceiverBox));
						break;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Mods.Railcraft)
	public void registerRenderers() {
		SignalBoxRenderer renderer = new SignalBoxRenderer(digitalReceiverBox, SignalTypes.DigitalReceiver);
		RenderingRegistry.registerBlockHandler(renderer);
		MinecraftForgeClient.registerItemRenderer(net.minecraft.item.Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
		renderer = new SignalBoxRenderer(digitalControllerBox, SignalTypes.DigitalController);
		RenderingRegistry.registerBlockHandler(renderer);
		MinecraftForgeClient.registerItemRenderer(net.minecraft.item.Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
	}
}
