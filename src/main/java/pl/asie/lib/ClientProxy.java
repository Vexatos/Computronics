package pl.asie.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import java.io.File;

public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public File getMinecraftDirectory() {
		return Minecraft.getMinecraft().gameDir;
	}

	@Override
	public World getWorld(int dimensionId) {
		if(getCurrentClientDimension() != dimensionId) {
			return null;
		} else {
			return Minecraft.getMinecraft().world;
		}
	}

	@Override
	public int getCurrentClientDimension() {
		return Minecraft.getMinecraft().world != null ? Minecraft.getMinecraft().world.provider.getDimension() : super.getCurrentClientDimension();
	}

	@Override
	public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
		try {
			switch(FMLCommonHandler.instance().getEffectiveSide()) {
				case CLIENT:
					if(client != null) {
						client.onMessage(packet, handler, Minecraft.getMinecraft().player);
					}
					break;
				case SERVER:
					super.handlePacket(client, server, packet, handler);
					break;
			}
		} catch(Exception e) {
			AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
			e.printStackTrace();
		}
	}
}
