package pl.asie.computronics.util.chat;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.api.chat.ChatAPI;
import pl.asie.computronics.api.chat.IChatListener;
import pl.asie.computronics.api.chat.IChatListenerRegistry;

import java.util.WeakHashMap;

public class ChatHandler implements IChatListenerRegistry {

	private final WeakHashMap<IChatListener, Object> listeners = new WeakHashMap<IChatListener, Object>();
	private final WeakHashMap<IChatListener, Object> invalidated = new WeakHashMap<IChatListener, Object>();
	private static final Object EMPTY = new Object();

	public ChatHandler() {
		ChatAPI.registry = this;
	}

	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		for(IChatListener l : listeners.keySet()) {
			if(!l.isValid()) {
				invalidated.put(l, EMPTY);
			} else {
				l.receiveChatMessage(event);
			}
		}
		for(IChatListener l : invalidated.keySet()) {
			listeners.remove(l);
		}
		invalidated.clear();
	}

	@Override
	public void registerChatListener(IChatListener listener) {
		listeners.put(listener, EMPTY);
	}

	@Override
	public void unregisterChatListener(IChatListener listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean isListenerRegistered(IChatListener listener) {
		return listeners.containsKey(listener);
	}
}
