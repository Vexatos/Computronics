package pl.asie.computronics.tile;

import java.util.HashSet;

import com.sun.imageio.plugins.common.I18N;

import pl.asie.computronics.util.ChatBoxUtils;
import net.minecraftforge.event.ServerChatEvent;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TileChatBoxCC15 extends TileChatBoxBase implements IPeripheral {
	// ComputerCraft API

	@Override
	public String getType() {
		return "chat_box";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"say", "getDistance", "setDistance"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws Exception {
		if(method == 0) {
			if(arguments.length >= 1 && arguments[0] instanceof String) {
				ChatBoxUtils.sendChatMessage(this, distance, I18N.getString("computronics.chatBox.prefix"), (String)arguments[0]);
			}
		} else if(method == 1) {
			return new Object[]{distance};
		} else if(method == 2) {
			if(arguments.length >= 1 && arguments[0] instanceof Integer)
				this.setDistance(((Integer)arguments[0]).intValue());
		}
		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	private final HashSet<IComputerAccess> ccComputers = new HashSet<IComputerAccess>();

	@Override
	public void attach(IComputerAccess computer) {
		ccComputers.add(computer);
	}


	@Override
	public void detach(IComputerAccess computer) {
		ccComputers.remove(computer);
	}

	@Override
	public void receiveChatMessageCC(ServerChatEvent event) {
		// Send CC event
		for(IComputerAccess computer: ccComputers) {
			computer.queueEvent("chat_message", new Object[]{event.username, event.message.replace("\u00a7", "&")});
		}
	}
}
