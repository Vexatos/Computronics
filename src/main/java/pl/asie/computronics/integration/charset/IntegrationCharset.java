package pl.asie.computronics.integration.charset;

import pl.asie.computronics.integration.charset.wires.IntegrationCharsetWires;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharset {

	public static IntegrationCharsetWires wires;
	// public static IntegrationCharsetAudio audio; //TODO Charset Audio

	public void preInit() {
		if(Mods.API.hasAPI(Mods.API.CharsetWires)) {
			wires = new IntegrationCharsetWires();
			wires.preInit();
		}
		/*if(Mods.isLoaded(Mods.CharsetAudio)) { //TODO Charset Audio
			audio = new IntegrationCharsetAudio();
			audio.preInit();
		}*/
	}
}
