package pl.asie.computronics.tape;

import pl.asie.computronics.tile.TapeDriveState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class PortableTapeDriveManager {
	private static HashMap<Integer, TapeDriveState> map = new HashMap<Integer, TapeDriveState>();

	public static TapeDriveState getOrMakeState(int id) {
		if(id != 0 && map.containsKey(id)){
			return map.get(id);
		}
		//TODO make this.
		TapeDriveState state = new TapeDriveState();
		return state;
	}
}
