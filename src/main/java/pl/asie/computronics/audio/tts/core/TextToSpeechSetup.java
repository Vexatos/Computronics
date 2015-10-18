package pl.asie.computronics.audio.tts.core;

import cpw.mods.fml.relauncher.IFMLCallHook;

import java.util.Map;

/**
 * @author Vexatos
 */
public class TextToSpeechSetup implements IFMLCallHook {
	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public Void call() throws Exception {
		TextToSpeechLoader.INSTANCE.preInit();
		return null;
	}
}
