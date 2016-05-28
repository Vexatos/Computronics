package pl.asie.lib.integration.tool;

//import pl.asie.lib.integration.tool.appeng.ToolProviderAE2;
//import pl.asie.lib.integration.tool.cofh.ToolProviderCoFH;

import pl.asie.lib.integration.tool.enderio.ToolProviderEnderIO;
import pl.asie.lib.integration.tool.oc.ToolProviderOC;
import pl.asie.lib.reference.Mods;

import static pl.asie.lib.integration.Integration.registerToolProvider;

//import pl.asie.lib.integration.tool.mekanism.ToolProviderMekanism

/**
 * @author Vexatos
 */
public class ToolProviders {

	public static void registerToolProviders() {
		if(Mods.hasVersion(Mods.API.OpenComputersInternal, "[5.1.1,)")) {
			registerToolProvider(new ToolProviderOC());
		}
		/*if(Mods.API.hasAPI(Mods.API.BuildCraftTools)) {
			registerToolProvider(new ToolProviderBuildCraft());
		}*/
		if(Mods.API.hasAPI(Mods.API.EnderIOTools)) {
			registerToolProvider(new ToolProviderEnderIO());
		}
		/*if(Mods.API.hasAPI(Mods.API.CoFHItems)) {
			registerToolProvider(new ToolProviderCoFH());
		}*/
		/*if(Mods.isLoaded(Mods.AE2)) {
			registerToolProvider(new ToolProviderAE2());
		}
		if(Mods.isLoaded(Mods.Mekanism)) {
			registerToolProvider(new ToolProviderMekanism());
		}*/
	}
}
