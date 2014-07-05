package pl.asie.computronics.oc;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.ChatBoxUtils;
import net.minecraftforge.event.ServerChatEvent;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.Container;
import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

public class RobotUpgradeChatBox extends ManagedEnvironment {
	private final Container container;
	private final Robot robot;
	
	public RobotUpgradeChatBox(Container container) {		this.container = container;		this.robot = (Robot)container;		this.node = Network.newNode(this, Visibility.Network).withConnector().withComponent("chat", Visibility.Neighbors).create();	}
	
	public void receiveChatMessage(ServerChatEvent event) {
		if(node != null)
			node.sendToReachable("computer.signal", "chat_message", event.username, event.message);
	}
	
	@Callback(direct = true, limit = 3)
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			String prefix = robot.player().getDisplayName();
			if(prefix == null) prefix = Computronics.CHATBOX_PREFIX;
			if(args.isString(0)) ChatBoxUtils.sendChatMessage(this.container, Computronics.CHATBOX_DISTANCE, "ChatBox", args.checkString(0));
		}
		return null;
	}
}