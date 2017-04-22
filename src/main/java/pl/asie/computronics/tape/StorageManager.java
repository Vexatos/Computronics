package pl.asie.computronics.tape;

import net.minecraftforge.common.DimensionManager;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.util.MiscUtils;

import java.io.File;
import java.util.Random;

public class StorageManager {

	// Map
	private static Random rand = new Random();

	private File saveDir() {
		File currentSaveRootDirectory = DimensionManager.getCurrentSaveRootDirectory();
		if(currentSaveRootDirectory == null) {
			Computronics.log.error("COULD NOT CREATE SAVE DIRECTORY: No parent save directory found!");
		}
		File saveDir = new File(currentSaveRootDirectory, Mods.Computronics);
		if(!saveDir.exists() && !saveDir.mkdir()) {
			Computronics.log.error("COULD NOT CREATE SAVE DIRECTORY: " + saveDir.getAbsolutePath());
		}
		return saveDir;
	}

	private String filename(String storageName) {
		return storageName + ".dsk";
	}

	public TapeStorage newStorage(int size) {
		String storageName;
		while(true) {
			byte[] nameHex = new byte[16];
			rand.nextBytes(nameHex);
			storageName = MiscUtils.asHexString(nameHex);
			if(!exists(storageName)) {
				break;
			}
		}
		return get(storageName, size, 0);
	}

	public boolean exists(String name) {
		return new File(saveDir(), filename(name)).exists();
	}

	public TapeStorage get(String name, int size, int position) {
		return new TapeStorage(name, new File(saveDir(), filename(name)), size, position);
	}
}
