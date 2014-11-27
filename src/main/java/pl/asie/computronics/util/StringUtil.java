package pl.asie.computronics.util;

import net.minecraft.util.StatCollector;

/**
 * @author Vexatos
 */
public class StringUtil {

	public static String localize(String key) {
		return StatCollector.translateToLocal(key).replace("\\n", "\n");
	}

	public static String localizeAndFormat(String key, Object... formatting) {
		return StatCollector.translateToLocalFormatted(key, formatting);
	}

	public static boolean canTranslate(String key) {
		return StatCollector.canTranslate(key);
	}
}
