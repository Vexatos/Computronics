package pl.asie.computronics.oc.block;

import li.cil.oc.api.network.Environment;

/**
 * @author Vexatos
 */
public interface IComputronicsEnvironmentBlock {

	public Class<? extends Environment> getTileEntityClass(int meta);
}
