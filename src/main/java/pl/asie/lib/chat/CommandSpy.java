package pl.asie.lib.chat;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

public class CommandSpy extends CommandBase {
	public static final Set<String> SPYING_USERS = new HashSet<String>();

	@Override
	public String getCommandName() {
		return "spy";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/spy";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (SPYING_USERS.contains(sender.getCommandSenderName())) {
			SPYING_USERS.remove(sender.getCommandSenderName());
			sender.addChatMessage(new ChatComponentTranslation("command.spy.disabled"));
		} else {
			SPYING_USERS.add(sender.getCommandSenderName());
			sender.addChatMessage(new ChatComponentTranslation("command.spy.enabled"));
		}
	}
}
