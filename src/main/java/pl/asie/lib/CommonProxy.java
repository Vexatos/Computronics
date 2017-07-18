package pl.asie.lib;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

import javax.annotation.Nullable;
import java.io.File;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public File getMinecraftDirectory() {
		return new File(".");
	}

	@Nullable
	public World getWorld(int dimensionId) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimensionId);
	}

	public int getCurrentClientDimension() {
		return -9001;
	}

	public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
		try {
			if(server != null) {
				server.onMessage(packet, handler, ((NetHandlerPlayServer) handler).player);
			}
		} catch(Exception e) {
			AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
			e.printStackTrace();
		}
	}
}
