package pl.asie.computronics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.AudioPacketRegistry;
import pl.asie.computronics.audio.AudioPacketClientHandlerDFPWM;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.oc.client.UpgradeRenderer;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.beep.Audio;
import pl.asie.computronics.util.boom.SelfDestruct;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;

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
		if(name.contains("#")) {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name.split("#")[0], name.split("#")[1]));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
		}
	}

	@Override
	public void init() {
		Audio.init();
		registerRenderers();
	}

	public void registerRenderers() {
		if(Computronics.colorfulLamp != null) {
			//RenderingRegistry.registerBlockHandler(new LampRender()); TODO Proper Lamp Renderer
		}
		/*if(Computronics.railcraft != null && Computronics.railcraft.digitalBox != null) {
			SignalBoxRenderer renderer = new SignalBoxRenderer();
			RenderingRegistry.registerBlockHandler(renderer);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
		}*/
		if(Mods.isLoaded(Mods.OpenComputers)) {
			registerOpenComputersRenderers();
			/*if(Computronics.forestry != null) {
				Computronics.forestry.registerOCRenderers();
			}*/
		}
	}

	@Override
	public void goBoom(Packet p) throws IOException {
		double
			x = p.readDouble(),
			y = p.readDouble(),
			z = p.readDouble();
		float force = p.readFloat();
		Minecraft minecraft = Minecraft.getMinecraft();
		SelfDestruct explosion = new SelfDestruct(minecraft.theWorld,
			null, x,
			y,
			z,
			force);
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
	}
}
