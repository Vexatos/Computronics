package pl.asie.lib.api;

import pl.asie.lib.api.chat.INicknameHandler;
import pl.asie.lib.api.chat.INicknameRepository;

public class AsieLibAPI {
	public static AsieLibAPI instance;
	
	public void registerNicknameHandler(INicknameHandler handler) { }
	public INicknameRepository getNicknameRepository() { return null; }
}
