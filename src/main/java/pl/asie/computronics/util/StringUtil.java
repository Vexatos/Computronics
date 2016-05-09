package pl.asie.computronics.util;

import net.minecraft.util.text.translation.I18n;

/**
 * @author Vexatos
 */
public class StringUtil {

	public static String localize(String key) {
		return I18n.translateToLocal(key).replace("\\n", "\n");
	}

	public static String localizeAndFormat(String key, Object... formatting) {
		return I18n.translateToLocalFormatted(key, formatting);
	}

	public static boolean canTranslate(String key) {
		return I18n.canTranslate(key);
	}
}
