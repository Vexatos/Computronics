package mods.immibis.redlogic.api.chips.compiler.util;

import mods.immibis.redlogic.api.chips.compiler.ICompilableExpression;
import mods.immibis.redlogic.api.chips.compiler.ICompileContext;

import org.objectweb.asm.Opcodes;

public class NotExpr implements ICompilableExpression {
	private final ICompilableExpression input;
	
	private NotExpr(ICompilableExpression input) {
		this.input = input;
	}
	
	@Override
	public boolean alwaysInline() {
		return input.alwaysInline();
	}
	
	@Override
	public void compile(ICompileContext ctx) {
		input.compile(ctx);
		ctx.getCodeVisitor().visitInsn(Opcodes.ICONST_1);
		ctx.getCodeVisitor().visitInsn(Opcodes.IXOR);
	}
	
	/**
	 * Creates an expression which evaluates to the NOT of the specified expression.
	 */
	public static ICompilableExpression createNOT(ICompilableExpression input) {
		if(input instanceof NotExpr)
			return ((NotExpr)input).input;
		if(input instanceof OneExpr)
			return new ZeroExpr();
		if(input instanceof ZeroExpr)
			return new OneExpr();
		return new NotExpr(input);
	}
}
