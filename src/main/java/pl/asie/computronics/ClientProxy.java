package pl.asie.computronics;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Optional;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import pl.asie.computronics.client.AudioCableRender;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.client.SignalBoxRenderer;
import pl.asie.computronics.client.UpgradeRenderer;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.boom.SelfDestruct;
import pl.asie.lib.network.Packet;

public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void registerEntities() {
		super.registerEntities();
	}

	@Override
	public void registerRenderers() {
		if(Computronics.colorfulLamp != null) {
			RenderingRegistry.registerBlockHandler(new LampRender());
		}
		if(Computronics.audioCable != null) {
			RenderingRegistry.registerBlockHandler(new AudioCableRender());
		}
		if(Computronics.railcraft != null && Computronics.railcraft.digitalBox != null) {
			SignalBoxRenderer renderer = new SignalBoxRenderer();
			RenderingRegistry.registerBlockHandler(renderer);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			registerOpenComputersRenderers();
			if(Mods.hasVersion(Mods.Forestry, "[4.0.8.36,)")) {
				Computronics.forestry.registerOCRenderers();
			}
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
		ArrayList<ChunkPosition> list = new ArrayList<ChunkPosition>(size);
		int i = (int) x;
		int j = (int) y;
		int k = (int) z;
		{
			int j1, k1, l1;
			for(int i1 = 0; i1 < size; ++i1) {
				j1 = p.readByte() + i;
				k1 = p.readByte() + j;
				l1 = p.readByte() + k;
				list.add(new ChunkPosition(j1, k1, l1));
			}
		}
		explosion.affectedBlockPositions = list;
		explosion.doExplosionB(true);
		minecraft.thePlayer.motionX += (double) p.readFloat();
		minecraft.thePlayer.motionY += (double) p.readFloat();
		minecraft.thePlayer.motionZ += (double) p.readFloat();
	}

	@Override
	@Optional.Method(modid = Mods.Forestry)
	public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
		Computronics.forestry.spawnSwarmParticle(worldObj, xPos, yPos, zPos, color);
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void registerOpenComputersRenderers() {
		if(IntegrationOpenComputers.upgradeRenderer == null) {
			IntegrationOpenComputers.upgradeRenderer = new UpgradeRenderer();
		}
		MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.upgradeRenderer);
	}
}
