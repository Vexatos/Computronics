package pl.asie.computronics.util;

/**
 * @author Vexatos
 */
public class LampUtil {

	private static Class coloredLights;
	private static boolean initialized = false;

	/**
	 * I do not think there is a better way to check for Colored Lights to be present
	 */
	public static boolean shouldColorLight() {
		if(!initialized) {
			try {
				coloredLights = Class.forName("coloredlightscore.src.api.CLApi");
			} catch(ClassNotFoundException e) {
				coloredLights = null;
			}
			initialized = true;
		}
		return coloredLights != null;
	}

	public static int toBrightness(int color) {
		return Math.round(brightness(color) * 15F);
	}

	public static float brightness(int color) {
		int r = (color >> 10) & 0x1F,
			g = (color >> 5) & 0x1F,
			b = color & 0x1F;
		return (r + g + b) / (0x1F * 3F);
	}
}
