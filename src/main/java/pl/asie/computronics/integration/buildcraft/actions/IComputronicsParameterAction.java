package pl.asie.computronics.integration.buildcraft.actions;

import buildcraft.api.statements.IStatementParameter;

/**
 * @author Vexatos
 */
public interface IComputronicsParameterAction extends IComputronicsAction {

	public IStatementParameter createParameter(int index);

}
