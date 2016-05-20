package pl.asie.computronics.util;

/**
 * @author Vexatos
 */
@SuppressWarnings("deprecation")
public class StringUtil {

	public static String localize(String key) {
		return net.minecraft.util.text.translation.I18n.translateToLocal(key).replace("\\n", "\n");
	}

	public static String localizeAndFormat(String key, Object... formatting) {
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, formatting);
	}

	public static boolean canTranslate(String key) {
		return net.minecraft.util.text.translation.I18n.canTranslate(key);
	}
}
