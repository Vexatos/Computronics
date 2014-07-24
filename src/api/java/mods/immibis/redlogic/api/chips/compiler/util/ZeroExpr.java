package mods.immibis.redlogic.api.chips.compiler.util;

import mods.immibis.redlogic.api.chips.compiler.ICompilableExpression;
import mods.immibis.redlogic.api.chips.compiler.ICompileContext;

import org.objectweb.asm.Opcodes;

/**
 * An expression that always returns false.
 * 
 * You may test for input expressions that are instances of this and optimize accordingly.
 * You are not required to do this.
 */
public class ZeroExpr implements ICompilableExpression {
	@Override
	public boolean alwaysInline() {
		return true;
	}
	
	@Override
	public void compile(ICompileContext ctx) {
		ctx.getCodeVisitor().visitInsn(Opcodes.ICONST_0);
	}
}