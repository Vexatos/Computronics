package pl.asie.computronics.integration.buildcraft.statements;

import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.StatementManager;
import pl.asie.computronics.integration.buildcraft.statements.parameters.ActionParameterLampColor;

/**
 * @author Vexatos
 */
public enum StatementParameters {
	Lamp_Color("lamp_color", ActionParameterLampColor.class);

	public static final StatementParameters[] VALUES = values();
	public final String name;
	private Class<? extends IStatementParameter> paramClass;

	StatementParameters(String name, Class<? extends IStatementParameter> paramClass) {
		this.name = name;
		this.paramClass = paramClass;
	}

	public static void initialize() {
		for(StatementParameters param : VALUES) {
			StatementManager.registerParameterClass(param.paramClass);
		}
	}
}
