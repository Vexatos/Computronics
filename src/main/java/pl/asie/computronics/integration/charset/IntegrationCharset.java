package pl.asie.computronics.integration.charset;

import pl.asie.computronics.integration.charset.audio.IntegrationCharsetAudio;
import pl.asie.computronics.integration.charset.wires.IntegrationCharsetWires;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharset {

	public static IntegrationCharsetWires wires;
	public static IntegrationCharsetAudio audio;

	public void preInit() {
		if(Mods.API.hasAPI(Mods.API.CharsetWires)) {
			wires = new IntegrationCharsetWires();
		}
	}

	public void postInit() {
		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
			audio = new IntegrationCharsetAudio();
			audio.postInit();
		}
	}
}
