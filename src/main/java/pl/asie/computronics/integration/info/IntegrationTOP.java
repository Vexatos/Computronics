package pl.asie.computronics.integration.info;

import com.google.common.base.Function;
import mcjty.theoneprobe.api.ITheOneProbe;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class IntegrationTOP implements Function<ITheOneProbe, Void> {

	@Override
	public Void apply(@Nullable ITheOneProbe probe) {
		if(probe == null) {
			return null;
		}
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		probe.registerProvider(provider);
		return null;
	}
}
