package pl.asie.lib.chat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import pl.asie.lib.AsieLibMod;

import java.util.List;

public class CommandRealname extends CommandBase {

	@Override
	public String getCommandName() {
		return "realname";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return AsieLibMod.chat.realnameLevel;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return this.getRequiredPermissionLevel() == 0 || super.canCommandSenderUseCommand(sender);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.realname.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length > 0 && args[0].length() > 0) {
			String realname = AsieLibMod.nick.getUsername(args[0]);
			String text;
			if(realname != null) {
				text = StatCollector.translateToLocalFormatted("commands.realname.is", realname);
			} else {
				text = StatCollector.translateToLocal("commands.realname.isNot");
			}
			sender.addChatMessage(new ChatComponentText(text));
		} else {
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return ChatHandler.addTabUsernameCompletionOptions(args);
	}

	@Override
	public int compareTo(Object par1Obj) {
		return ((ICommand) par1Obj).getCommandName().compareTo(this.getCommandName());
	}
}
