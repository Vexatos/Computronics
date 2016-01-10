package pl.asie.lib.chat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import pl.asie.lib.AsieLibMod;

import java.util.List;

public class CommandNick extends CommandBase {
	@Override
	public String getCommandName() {
		return "nick";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return AsieLibMod.chat.nickLevel;
	}

	public int getOpPermissionLevel() {
		return 2;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return this.getRequiredPermissionLevel() == 0 || super.canCommandSenderUseCommand(sender);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(this.getOpPermissionLevel(), this.getCommandName())
			? "commands.nick.usage.op"
			: "commands.nick.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length > 0 && args[0].length() > 0) {
			String target = args[0];
			String newName = target;
			if(args.length == 2) {
				if(sender.canCommandSenderUseCommand(this.getOpPermissionLevel(), this.getCommandName())) {
					newName = args[1];
				} else {
					throw new WrongUsageException(this.getCommandUsage(sender));
				}
			} else {
				target = sender.getCommandSenderName();
			}

			AsieLibMod.nick.setNickname(target, newName);
			sender.addChatMessage(new ChatComponentTranslation("commands.nick.done", AsieLibMod.nick.getNickname(target)));
		} else {
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if(args == null || args.length < 1) {
			return null;
		}
		if(sender.canCommandSenderUseCommand(this.getOpPermissionLevel(), this.getCommandName())) {
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		} else {
			String[] names;
			String username = sender.getCommandSenderName();
			if(AsieLibMod.nick != null && AsieLibMod.nick.nicknames != null && AsieLibMod.nick.hasNickname(username)) {
				names = new String[]{username, AsieLibMod.nick.getRawNickname(username)};
			} else {
				names = new String[]{username};
			}
			return getListOfStringsMatchingLastWord(args, names);
		}
	}

	@Override
	public int compareTo(Object par1Obj) {
		return ((ICommand) par1Obj).getCommandName().compareTo(this.getCommandName());
	}
}
