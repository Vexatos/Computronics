package pl.asie.lib.chat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.Packets;
import pl.asie.lib.api.chat.INicknameHandler;
import pl.asie.lib.network.Packet;

import java.util.HashMap;

public class NicknameNetworkHandler implements INicknameHandler {

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		if(event.player != null) {
			AsieLibMod.nick.updateNickname(event.player.getCommandSenderName());
		}

		HashMap<String, String> names = new HashMap<String, String>();
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if(o == null || !(o instanceof EntityPlayer)) {
				continue;
			}
			EntityPlayer e = (EntityPlayer) o;
			String username = e.getCommandSenderName();
			String nickname = AsieLibMod.nick.getNickname(username);
			if(!nickname.equals(username)) {
				names.put(username, nickname);
			}
		}
		sendNicknameSyncPacket(names, event.player);
	}

	private void sendNicknameSyncPacket(HashMap<String, String> names, EntityPlayer target) {
		try {
			Packet packet = AsieLibMod.packet.create(Packets.NICKNAME_SYNC)
				.writeInt(names.size());

			for(String username : names.keySet()) {
				String nickname = names.get(username);
				packet.writeString(username)
					.writeString(nickname);
			}

			if(target == null) {
				AsieLibMod.packet.sendToAll(packet);
			} else {
				AsieLibMod.packet.sendTo(packet, (EntityPlayerMP) target);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void sendNicknamePacket(String realname, String nickname) {
		//AsieLibMod.log.info("Trying to change " + realname + " to " + nickname);
		try {
			Packet packet = AsieLibMod.packet.create(Packets.NICKNAME_CHANGE)
				.writeString(realname)
				.writeString(nickname);

			AsieLibMod.packet.sendToAll(packet);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNicknameUpdate(String realname, String nickname) {
		//AsieLibMod.log.info("Network Handler Nickname Update: " + realname + " to " + nickname);
		sendNicknamePacket(realname, nickname);
	}
}
