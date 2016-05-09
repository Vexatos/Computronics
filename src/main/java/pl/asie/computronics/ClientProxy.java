package pl.asie.computronics;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.AudioPacketClientHandlerDFPWM;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.oc.client.RackMountableRenderer;
import pl.asie.computronics.oc.client.UpgradeRenderer;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.boom.SelfDestruct;
import pl.asie.computronics.util.sound.Audio;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void registerAudioHandlers() {
		super.registerAudioHandlers();
		AudioPacketRegistry.INSTANCE.registerClientHandler(
			AudioPacketDFPWM.class, new AudioPacketClientHandlerDFPWM()
		);
	}

	@Override
	public void registerEntities() {
		super.registerEntities();
	}

	@Override
	public void registerItemModel(Item item, int meta, String name) {
		if(item instanceof IItemColor) {
			coloredItems.put(((IItemColor) item), item);
		}
		if(name.contains("#")) {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name.split("#")[0], name.split("#")[1]));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
		}
	}

	@Override
	public void registerItemModel(Block block, int meta, String name) {
		if(block instanceof IBlockColor) {
			coloredBlocks.put(((IBlockColor) block), block);
		}
		super.registerItemModel(block, meta, name);
	}

	private final Map<IItemColor, Item> coloredItems = new HashMap<IItemColor, Item>();
	private final Map<IBlockColor, Block> coloredBlocks = new HashMap<IBlockColor, Block>();

	@Override
	public void init() {
		Audio.init();
		registerRenderers();
		registerColors();
	}

	private void registerColors() {
		for(Map.Entry<IItemColor, Item> entry : coloredItems.entrySet()) {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<IBlockColor, Block> entry : coloredBlocks.entrySet()) {
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(entry.getKey(), entry.getValue());
		}
	}

	public void registerRenderers() {
		/*if(Computronics.colorfulLamp != null) {
			//RenderingRegistry.registerBlockHandler(new LampRender());
		}*/
		/*if(Computronics.audioCable != null) {
			RenderingRegistry.registerBlockHandler(new AudioCableRender());
		}
		if(Computronics.railcraft != null) {
			Computronics.railcraft.registerRenderers();
		}*/
		if(Mods.isLoaded(Mods.OpenComputers)) {
			registerOpenComputersRenderers();
			/*if(Computronics.forestry != null) {
				Computronics.forestry.registerOCRenderers();
			}*/
		}
		/*if(Mods.API.hasAPI(Mods.API.BuildCraftStatements)) {
			MinecraftForge.EVENT_BUS.register(new StatementTextureManager());
		}*/
	}

	@Override
	public void onServerStop() {
		Computronics.instance.audio.removeAll();
	}

	@Override
	public void goBoom(Packet p) throws IOException {
		double
			x = p.readDouble(),
			y = p.readDouble(),
			z = p.readDouble();
		float force = p.readFloat();
		boolean destroyBlocks = p.readByte() != 0;
		Minecraft minecraft = Minecraft.getMinecraft();
		SelfDestruct explosion = new SelfDestruct(minecraft.theWorld,
			null, x, y, z, force, destroyBlocks);
		int size = p.readInt();
		ArrayList<BlockPos> list = new ArrayList<BlockPos>(size);
		int i = (int) x;
		int j = (int) y;
		int k = (int) z;
		{
			int j1, k1, l1;
			for(int i1 = 0; i1 < size; ++i1) {
				j1 = p.readByte() + i;
				k1 = p.readByte() + j;
				l1 = p.readByte() + k;
				list.add(new BlockPos(j1, k1, l1));
			}
		}

		explosion.getAffectedBlockPositions().clear();
		explosion.getAffectedBlockPositions().addAll(list);
		explosion.doExplosionB(true);
		minecraft.thePlayer.motionX += (double) p.readFloat();
		minecraft.thePlayer.motionY += (double) p.readFloat();
		minecraft.thePlayer.motionZ += (double) p.readFloat();
	}

	/*@Override
	@Optional.Method(modid = Mods.Forestry)
	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) { TODO Forestry
		Computronics.forestry.spawnSwarmParticle(worldObj, xPos, yPos, zPos, color);
	}*/

	@Optional.Method(modid = Mods.OpenComputers)
	private void registerOpenComputersRenderers() {
		if(IntegrationOpenComputers.upgradeRenderer == null) {
			IntegrationOpenComputers.upgradeRenderer = new UpgradeRenderer();
		}
		MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.upgradeRenderer);

		if(IntegrationOpenComputers.mountableRenderer == null) {
			IntegrationOpenComputers.mountableRenderer = new RackMountableRenderer();
		}
		MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.mountableRenderer);
	}
}
