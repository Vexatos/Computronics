package pl.asie.lib.api.chat;

public interface INicknameRepository {
	public void setNickname(String username, String nickname);
	public String getNickname(String username);
	public String getUsername(String nickname);
}
