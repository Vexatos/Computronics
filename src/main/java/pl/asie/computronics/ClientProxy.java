package pl.asie.computronics;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.MinecraftForgeClient;
import pl.asie.computronics.client.LampRender;
import pl.asie.computronics.client.SignalBoxRenderer;
import pl.asie.computronics.gui.GuiCipherBlock;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.tile.ContainerCipherBlock;
import pl.asie.computronics.tile.ContainerTapeReader;
import pl.asie.computronics.util.boom.SelfDestruct;
import pl.asie.lib.gui.GuiHandler;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;

public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void registerGuis(GuiHandler gui) {
		gui.registerGui(ContainerTapeReader.class, GuiTapePlayer.class);
		gui.registerGui(ContainerCipherBlock.class, GuiCipherBlock.class);
	}

	@Override
	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityItemIndestructable.class, "computronics.itemTape", 1, Computronics.instance, 64, 20, true);
	}

	@Override
	public void registerRenderers() {
		if(Computronics.colorfulLamp != null) {
			RenderingRegistry.registerBlockHandler(new LampRender());
		}
		if(Computronics.railcraft != null) {
			SignalBoxRenderer renderer = new SignalBoxRenderer();
			RenderingRegistry.registerBlockHandler(renderer);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
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
}
