package pl.asie.lib.chat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;

import pl.asie.lib.AsieLibMod;
import pl.asie.lib.reference.Mods;
import pl.asie.lib.util.ChatUtils;

public class ChatHandler {
	private final HashMap<String, String> actions = new HashMap<String, String>();
	public final boolean enableChatFeatures, enableShout, enableGreentext, enableColor, enableChatLog, spyOnByDefault;
	public final int CHAT_RADIUS, nickLevel, realnameLevel;
	public final String colorAction, messageFormat, shoutPrefix;
	public final Logger chatlog;

	public ChatHandler(Configuration config) {
		CHAT_RADIUS = config.get("chat", "chatRadius", 0).getInt();
		spyOnByDefault = config.get("chat", "spyOnByDefault", true, "Admins will have spy mode on by default after connecting. (Only matters if chatRadius > 0)").getBoolean(true);
		enableShout = config.get("chat", "enableShout", true).getBoolean(true);
		shoutPrefix = config.get("chat", "shoutPrefix", "[Shout]").getString();
		enableChatFeatures = config.get("base", "enableChatTweaks", false).getBoolean(false);
		enableColor = config.get("base", "enableColor", true).getBoolean(true);
		enableGreentext = config.get("chat", "enableGreentext", false, ">implying anyone will ever turn this on").getBoolean(false);
		nickLevel = config.get("chat", "nicknamesForEveryone", true, "Disable to make changing the own nickname require Op rights on a server").getBoolean(true) ? 0 : 2;
		realnameLevel = config.get("chat", "realnamesForEveryone", true, "Disable to make looking up the real name of others require Op rights on a server").getBoolean(true) ? 0 : 2;
		colorAction = config.get("chat", "colorMe", "5").getString();
		messageFormat = config.get("chat", "formatMessage", "<%u> %m", "%u - username; %m - message; %w - dimension; %H - hours; %M - minutes; %S - seconds").getString();
		enableChatLog = config.get("chat", "chatLog", true, "Enable this to log server chat to console").getBoolean(true);
		chatlog = LogManager.getLogger("Chat");
	}

	public void registerCommands(FMLServerStartingEvent event) {
		if(enableChatFeatures) {
			event.registerServerCommand(new CommandMe());
			event.registerServerCommand(new CommandNick());
			event.registerServerCommand(new CommandRealname());
			if (CHAT_RADIUS > 0) {
				event.registerServerCommand(new CommandSpy());
			}
		}
	}

	private static String pad(int t) {
		if(t < 10) {
			return "0" + t;
		} else {
			return "" + t;
		}
	}

	@SubscribeEvent
	public void loggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player.canCommandSenderUseCommand(2, "spy") && spyOnByDefault) {
			CommandSpy.SPYING_USERS.add(event.player.getCommandSenderName());
		}
	}

	@SubscribeEvent
	public void loggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		CommandSpy.SPYING_USERS.remove(event.player.getCommandSenderName());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void chatEvent(ServerChatEvent event) {
		if(CHAT_RADIUS < 0) { // Chat disabled altogether
			event.setCanceled(true);
			return;
		}

		IChatComponent chat, chatSpy = null;
		boolean disableRadius = false;
		String username = ChatUtils.color(AsieLibMod.nick.getNickname(event.username)) + EnumChatFormatting.RESET;
		String message = event.message;
		int dimensionId = event.player.worldObj.provider.dimensionId;

		if(enableShout && event.message.startsWith("!")) {
			message = message.substring(1);
		}

		if(enableGreentext && message.startsWith(">")) {
			message = EnumChatFormatting.GREEN + message;
		}

		Calendar now = Calendar.getInstance();
		String formattedMessage = EnumChatFormatting.RESET + messageFormat;
		try {
			formattedMessage = formattedMessage.replaceAll("%u", username)
				.replaceAll("%m", message)
				.replaceAll("%w", event.player.worldObj.provider.getDimensionName())
				.replaceAll("%H", pad(now.get(Calendar.HOUR_OF_DAY)))
				.replaceAll("%M", pad(now.get(Calendar.MINUTE)))
				.replaceAll("%S", pad(now.get(Calendar.SECOND)));
		} catch(Exception e) {
			e.printStackTrace();
			formattedMessage = EnumChatFormatting.RESET + "<" + username + "" + EnumChatFormatting.RESET + "> " + message;
		}

		if(enableColor) {
			try {
				formattedMessage = ChatUtils.color(formattedMessage);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		if(enableShout && event.message.startsWith("!")) {
			chat = ForgeHooks.newChatWithLinks(EnumChatFormatting.YELLOW + shoutPrefix + " " + formattedMessage);
			disableRadius = true;
		} else {
			chat = ForgeHooks.newChatWithLinks(formattedMessage);
			chatSpy = ForgeHooks.newChatWithLinks(EnumChatFormatting.GRAY + "[Spy] " + formattedMessage);
		}

		boolean useRadius = CHAT_RADIUS > 0 && !disableRadius;
		event.setCanceled(true); // Override regular sending

		if(!useRadius && Mods.API.hasAPI("EiraIRC|API")) {
			ChatHandlerEiraIRC.eiraircRelay(event.player, username, message);
		}

		if(MinecraftServer.getServer() == null) {
			return;
		}
		if(enableChatLog) {
			chatlog.info(ChatUtils.stripColors(formattedMessage));
		}
		for (WorldServer ws : MinecraftServer.getServer().worldServers) {
			for(Object o : ws.playerEntities) {
				if(o instanceof EntityPlayer) {
					EntityPlayer target = (EntityPlayer) o;

					if ((!useRadius || event.player == target || event.player.getDistanceToEntity(target) <= CHAT_RADIUS)
							&& ws.provider.dimensionId == dimensionId) {
						target.addChatMessage(chat);
					} else if (chatSpy != null && CommandSpy.SPYING_USERS.contains(target.getCommandSenderName())) {
						target.addChatMessage(chatSpy);
					}
				}
			}
		}
	}

	public static List addTabUsernameCompletionOptions(String[] args) {
		if(args == null || args.length < 1) {
			return null;
		}
		String[] names = MinecraftServer.getServer().getAllUsernames().clone();
		if(AsieLibMod.nick != null && AsieLibMod.nick.nicknames != null) {
			for(int i = 0; i < names.length; i++) {
				names[i] = AsieLibMod.nick.getNickname(names[i]);
			}
		}
		return CommandBase.getListOfStringsMatchingLastWord(args, names);
	}
}
