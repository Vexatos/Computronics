package pl.asie.computronics.integration.railcraft;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.client.render.tesr.TESRSignalBox;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.machine.ItemMachine;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalDetector;
import pl.asie.computronics.integration.railcraft.block.BlockDigitalSignalBox;
import pl.asie.computronics.integration.railcraft.block.BlockLocomotiveRelay;
import pl.asie.computronics.integration.railcraft.block.BlockTicketMachine;
import pl.asie.computronics.integration.railcraft.gui.GuiProviderTicketMachine;
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
	public BlockDigitalSignalBox digitalBox;
	public ItemMachine digitalBoxItem;
	public BlockTicketMachine ticketMachine;

	LocomotiveManager manager;
	public GuiProviderTicketMachine guiTicketMachine;

	private static boolean isEnabled(Configuration config, String name, boolean def) {
		return config.get("enable.railcraft", name, def).getBoolean(def);
	}

	public void preInit(Configuration config) {
		if(isEnabled(config, "locomotiveRelay", true)) {
			locomotiveRelay = new BlockLocomotiveRelay();
			Computronics.instance.registerBlockWithTileEntity(locomotiveRelay, TileLocomotiveRelay.class, "locomotive_relay");
			//IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(locomotiveRelay); TODO BuildCraft

			relaySensor = new ItemRelaySensor();
			Computronics.instance.registerItem(relaySensor, "relay_sensor");

			manager = new LocomotiveManager();
			MinecraftForge.EVENT_BUS.register(manager);
		}
		{
			SignalTypes.DigitalReceiver.enabled = isEnabled(config, "digitalReceiverBox", true);
			SignalTypes.DigitalController.enabled = isEnabled(config, "digitalControllerBox", true);
			digitalBox = new BlockDigitalSignalBox();
			GameRegistry.findRegistry(Block.class).register(digitalBox.setRegistryName(new ResourceLocation(Mods.Computronics, "digital_box")));
			digitalBoxItem = new ItemMachine(digitalBox) {
				@Override
				public String getTranslationKey() {
					return this.block.getTranslationKey();
				}

				@Override
				public String getTranslationKey(ItemStack stack) {
					IVariantEnum variant = this.getVariant(stack);
					if(variant == null) {
						return this.getTranslationKey();
					} else if(variant instanceof IVariantEnumBlock) {
						return ((IVariantEnumBlock) variant).getLocalizationTag();
					} else {
						return "tile.computronics." + variant.getResourcePathSuffix();
					}
				}
			};
			digitalBoxItem.setRegistryName(digitalBox.getRegistryName());
			GameRegistry.findRegistry(Item.class).register(digitalBoxItem);
			TileEntity.register("digital_controller_box", TileDigitalControllerBox.class);
			TileEntity.register("digital_receiver_box", TileDigitalReceiverBox.class);
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileDigitalControllerBox.class.getCanonicalName());
			FMLInterModComms.sendMessage(Mods.AE2, "whitelist-spatial", TileDigitalReceiverBox.class.getCanonicalName());
		}
		if(isEnabled(config, "digitalDetector", true)) {
			detector = new BlockDigitalDetector();
			Computronics.instance.registerBlockWithTileEntity(detector, TileDigitalDetector.class, "digital_detector");
			//IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(detector); TODO BuildCraft
		}
		if(isEnabled(config, "ticketMachine", true)) {
			this.guiTicketMachine = new GuiProviderTicketMachine();
			Computronics.gui.registerGuiProvider(guiTicketMachine);
			ticketMachine = new BlockTicketMachine();
			Computronics.instance.registerBlockWithTileEntity(ticketMachine, TileTicketMachine.class, "ticket_machine");
			//IntegrationBuildCraftBuilder.INSTANCE.registerBlockBaseSchematic(ticketMachine); TODO BuildCraft
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

	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Mods.Railcraft)
	public void registerRenderers() {
		digitalBox.initializeClient();
		for(SignalTypes type : SignalTypes.VALUES) {
			ItemStack stack = digitalBox.getStack(type);
			if(stack != null) {
				digitalBox.registerItemModel(stack, type);
			}
		}
		digitalBoxItem.initializeClient();
		ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalReceiverBox.class, new TESRSignalBox());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalControllerBox.class, new TESRSignalBox());
		ModelBakery.registerItemVariants(relaySensor, new ResourceLocation(Mods.Computronics + ":relay_sensor_off"));
		ModelBakery.registerItemVariants(relaySensor, new ResourceLocation(Mods.Computronics + ":relay_sensor_on"));
		ModelLoader.setCustomMeshDefinition(relaySensor, new ItemRelaySensor.MeshDefinition());
	}
}
