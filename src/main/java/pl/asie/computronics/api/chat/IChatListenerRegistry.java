package pl.asie.computronics.api.chat;

public interface IChatListenerRegistry {
	void registerChatListener(IChatListener listener);
	void unregisterChatListener(IChatListener listener);
	boolean isListenerRegistered(IChatListener listener);
}
