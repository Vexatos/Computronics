package mods.immibis.redlogic.api.chips.compiler.util;

import mods.immibis.redlogic.api.chips.compiler.ICompilableExpression;
import mods.immibis.redlogic.api.chips.compiler.ICompileContext;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * An expression equivalent to a ternary operator in Java (select ? ifOne : ifZero).
 * Note the order of expressions is reversed compared to a ternary operator.
 */
public class SelectExpr implements ICompilableExpression {
	
	private ICompilableExpression select, ifZero, ifOne;
	
	private SelectExpr(ICompilableExpression select, ICompilableExpression ifZero, ICompilableExpression ifOne) {
		this.select = select;
		this.ifZero = ifZero;
		this.ifOne = ifOne;
	}
	
	@Override
	public boolean alwaysInline() {
		return false;
	}
	
	@Override
	public void compile(ICompileContext ctx) {
		MethodVisitor mv = ctx.getCodeVisitor();
		
		Label ifZeroLabel = new Label();
		Label endLabel = new Label();
		
		select.compile(ctx);
		mv.visitJumpInsn(Opcodes.IFEQ, ifZeroLabel);
		ifOne.compile(ctx);
		mv.visitJumpInsn(Opcodes.GOTO, endLabel);
		mv.visitLabel(ifZeroLabel);
		ifZero.compile(ctx);
		mv.visitLabel(endLabel);
	}
	
	public static ICompilableExpression createSelect(ICompilableExpression select, ICompilableExpression ifZero, ICompilableExpression ifOne) {
		return new SelectExpr(select, ifZero, ifOne);
	}
}
