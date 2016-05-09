package pl.asie.lib.util;

public class ChatUtils {

	private static int[] dyeToChatArray = {
		0, 4, 2, 0, 1, 5, 3, 7, 8, 13, 10, 14, 9, 5, 6, 15
	};

	public static String color(String chat) {
		return chat.replaceAll("&(?=[0-9A-FK-ORa-fk-or])", "\u00a7");
	}

	public static String stripColors(String chat) {
		return chat.replaceAll("[&§][0-9A-FK-ORa-fk-or]", "");
	}

	public static int dyeToChat(int dyeColor) {
		return dyeToChatArray[dyeColor % 16];
	}

	public static int woolToChat(int woolColor) {
		return dyeToChat(15 - woolColor);
	}
}
