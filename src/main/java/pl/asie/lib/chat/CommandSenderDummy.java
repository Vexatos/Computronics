package pl.asie.lib.chat;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandSenderDummy implements ICommandSender {
	private final ICommandSender main;
	private final String name;
	
	public CommandSenderDummy(ICommandSender main, String name) {
		this.main = main;
		this.name = name;
	}
	@Override
	public String getCommandSenderName() { return name; }

	@Override
	public IChatComponent func_145748_c_() { return main.func_145748_c_(); }

	@Override
	public void addChatMessage(IChatComponent var1) { main.addChatMessage(var1); }

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return main.canCommandSenderUseCommand(var1, var2);
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return main.getPlayerCoordinates();
	}

	@Override
	public World getEntityWorld() {
		return main.getEntityWorld();
	}
}
