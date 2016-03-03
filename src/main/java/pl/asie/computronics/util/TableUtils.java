package pl.asie.computronics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Vexatos
 */
public class TableUtils {

	public static <T> Map<Integer, T> convertSetToMap(Set<T> set) {
		HashMap<Integer, T> map = new HashMap<Integer, T>();
		int i = 1;
		for(T m : set) {
			map.put(i++, m);
		}
		return map;
	}
}
