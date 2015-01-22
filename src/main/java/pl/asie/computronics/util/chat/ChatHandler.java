package pl.asie.computronics.util.chat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.api.chat.ChatAPI;
import pl.asie.computronics.api.chat.IChatListener;
import pl.asie.computronics.api.chat.IChatListenerRegistry;

import java.util.HashSet;

public class ChatHandler implements IChatListenerRegistry {
	private final HashSet<IChatListener> listeners = new HashSet<IChatListener>();
	private final HashSet<IChatListener> invalidated = new HashSet<IChatListener>();

	public ChatHandler() {
		ChatAPI.registry = this;
	}

	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		for (IChatListener l : listeners) {
			if (!l.isValid()) {
				invalidated.add(l);
			} else {
				l.receiveChatMessage(event);
			}
		}
		listeners.removeAll(invalidated);
		invalidated.clear();
	}

	@Override
	public void registerChatListener(IChatListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterChatListener(IChatListener listener) {
		listeners.remove(listener);
	}
}
