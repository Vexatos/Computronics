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
}
