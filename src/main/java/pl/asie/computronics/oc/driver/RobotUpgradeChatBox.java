package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.api.chat.ChatAPI;
import pl.asie.computronics.api.chat.IChatListener;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.ChatBoxUtils;
import pl.asie.computronics.util.OCUtils;

import java.util.Map;

public class RobotUpgradeChatBox extends AbstractManagedEnvironment implements DeviceInfo, IChatListener {

	private final EnvironmentHost container;
	private int distance;
	private String name = "";

	public RobotUpgradeChatBox(EnvironmentHost container) {
		this.container = container;
		distance = Config.CHATBOX_DISTANCE;
		this.setNode(Network.newNode(this, Visibility.Network).withConnector().withComponent("chat", Visibility.Neighbors).create());
	}

	@Override
	public void receiveChatMessage(ServerChatEvent event) {
		if(!Config.CHATBOX_MAGIC && (event.getPlayer().world != this.container.world()
			|| event.getPlayer().getDistanceSq(container.xPosition(), container.yPosition(), container.zPosition()) > distance * distance)) {
			return;
		}

		if(this.node() != null) {
			this.node().sendToReachable("computer.signal", "chat_message", event.getUsername(), event.getMessage());
		}
	}

	@Override
	public boolean isValid() {
		return this.node() != null;
	}

	@Override
	public void onConnect(Node node) {
		super.onConnect(node);
		if(node == this.node()) {
			ChatAPI.registry.registerChatListener(this);
		}
	}

	@Override
	public void onDisconnect(final Node node) {
		super.onDisconnect(node);
		if(node == this.node()) {
			ChatAPI.registry.unregisterChatListener(this);
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int dist) {
		if(dist > 32767) {
			dist = 32767;
		}

		this.distance = Math.min(Config.CHATBOX_DISTANCE, dist);
		if(this.distance < 0) {
			this.distance = Config.CHATBOX_DISTANCE;
		}
	}

	@Callback(doc = "function():number; Returns the chat distance the chat box is currently set to", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[] { distance };
	}

	@Callback(doc = "function(distance:number):number; Sets the distance of the chat box. Returns the new distance", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDistance(Context context, Arguments args) {
		setDistance(args.checkInteger(0));
		return new Object[] { distance };
	}

	@Callback(doc = "function():string; Returns the name of the chat box", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		return new Object[] { name };
	}

	@Callback(doc = "function(name:string):string; Sets the name of the chat box. Returns the new name", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setName(Context context, Arguments args) {
		this.name = args.checkString(0);
		return new Object[] { this.name };
	}

	@Callback(doc = "function(text:string [, distance:number]):boolean; "
		+ "Makes the robot say some text with the currently set or the specified distance. Returns true on success", direct = true, limit = 3)
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			//String prefix = robot.player().getDisplayName();
			//if(prefix == null) prefix = Computronics.CHATBOX_PREFIX;
			//if(args.isString(0)) ChatBoxUtils.sendChatMessage(this.container, Computronics.CHATBOX_DISTANCE, "ChatBox", args.checkString(0));
			int d = distance;
			if(args.count() >= 1) {
				if(args.isInteger(1)) {
					d = Math.min(Config.CHATBOX_DISTANCE, args.checkInteger(1));
					if(d <= 0) {
						d = distance;
					}
				}
				if(args.isString(0)) {
					sendChatMessage(this.container, d, name.length() > 0 ? name : Config.CHATBOX_PREFIX, args.checkString(0));
					return new Object[] { true };
				}
			}
			return new Object[] { false };
		}
		return null;
	}

	public static void sendChatMessage(EnvironmentHost c, int d, String prefix, String string) {
		if(c == null) {
			return;
		}
		ChatBoxUtils.sendChatMessage(c.world(), c.xPosition(), c.yPosition(), c.zPosition(), d, prefix, string);
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			return deviceInfo = new OCUtils.Device(
				DeviceClass.Multimedia,
				"Chat interface",
				OCUtils.Vendors.NSA,
				"[CLASSIFIED]"
			).deviceInfo();
		}
		return deviceInfo;
	}
}
