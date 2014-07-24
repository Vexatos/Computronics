package mods.immibis.redlogic.api.chips.compiler;

public interface ICompilableExpression {

	/**
	 * Emits code to push the value of the expression onto the stack.
	 */
	public void compile(ICompileContext ctx);
	
	/**
	 * If this returns true the expression's value will not be cached in a local variable, and instead the expression
	 * compile() will be called each time it is used. (it will not be wrapped in a CacheCBlock)
	 * 
	 * If the expression uses inputs, return false.
	 * If compile() emits code that does any computation more expensive than a local variable load, return false.
	 * If you don't understand the effect of returning true, return false.
	 */
	public boolean alwaysInline();

}
