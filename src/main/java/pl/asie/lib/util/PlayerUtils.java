package pl.asie.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class PlayerUtils {
	public static EntityPlayer find(String name) {
		//AsieLibMod.log.info("trying to find player " + name);
		if(MinecraftServer.getServer() == null){
			//AsieLibMod.log.info("No server found, trying client");
			if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().theWorld != null) {
				//AsieLibMod.log.info(player != null ? "client player found: " + player.toString() : "No client player found");
				return Minecraft.getMinecraft().theWorld.getPlayerEntityByName(name);
			}
			//AsieLibMod.log.info("nothing found");
			return null;
		}

		//AsieLibMod.log.info("Server found! It is " + MinecraftServer.getServer().toString());
		for(Object o: MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if(o instanceof EntityPlayer) {
				EntityPlayer target = (EntityPlayer)o;
				//AsieLibMod.log.info("Server player found: " + target.toString());
				if(target.getCommandSenderName().equals(name)) return target;
			}
		}
		//AsieLibMod.log.info(" [2] could not find player " + name + " on server");
		return null;
	}
}
