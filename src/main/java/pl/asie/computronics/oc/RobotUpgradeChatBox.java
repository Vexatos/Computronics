package pl.asie.computronics.oc;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.ChatBoxUtils;

public class RobotUpgradeChatBox extends ManagedEnvironment {
	private final EnvironmentHost container;
	private int distance;
	private String name = "";

	public RobotUpgradeChatBox(EnvironmentHost container) {
		this.container = container;
		distance = Config.CHATBOX_DISTANCE;
		this.setNode(Network.newNode(this, Visibility.Network).withConnector().withComponent("chat", Visibility.Neighbors).create());
	}

	public void receiveChatMessage(ServerChatEvent event) {
		if(this.node() != null)
			this.node().sendToReachable("computer.signal", "chat_message", event.username, event.message);
	}

	public int getDistance() { return distance; }

	public void setDistance(int dist) {
		if(dist > 32767) dist = 32767;

		this.distance = Math.min(Config.CHATBOX_DISTANCE, dist);
		if(this.distance < 0) this.distance = Config.CHATBOX_DISTANCE;
	}

	@Callback(direct = true)
	@Optional.Method(modid= Mods.OpenComputers)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[]{ distance };
	}

	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] setDistance(Context context, Arguments args) {
		if(args.count() == 1) {
			if(args.isInteger(0)) {
				setDistance(args.checkInteger(0));
				return new Object[]{ true };
			}
		}
		return new Object[] { false };
	}

	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		return new Object[]{name};
	}

	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] setName(Context context, Arguments args) {
		if(args.count() == 1) {
			if(args.isString(0)){
				this.name = args.checkString(0);
				return new Object[]{ true };
			}
		}
		return new Object[]{ false };
	}

	@Callback(direct = true, limit = 3)
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			//String prefix = robot.player().getDisplayName();
			//if(prefix == null) prefix = Computronics.CHATBOX_PREFIX;
			//if(args.isString(0)) ChatBoxUtils.sendChatMessage(this.container, Computronics.CHATBOX_DISTANCE, "ChatBox", args.checkString(0));
			int d = distance;
			if(args.count() >= 1) {
				if(args.isInteger(1)) {
					d = Math.min(Config.CHATBOX_DISTANCE, args.checkInteger(1));
					if(d <= 0) d = distance;
				}
				if(args.isString(0)){
					ChatBoxUtils.sendChatMessage(this.container, d, name.length() > 0 ? name : Config.CHATBOX_PREFIX, args.checkString(0));
					return new Object[] { true };
				}
			}
			return new Object[] { false };
		}
		return null;
	}
}
