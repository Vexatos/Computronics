package mods.immibis.redlogic.api.chips.compiler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mods.immibis.redlogic.api.chips.compiler.ICompilableExpression;
import mods.immibis.redlogic.api.chips.compiler.ICompileContext;

import org.objectweb.asm.Opcodes;

/**
 * This expression class can be used for AND, OR, XOR and perhaps other expressions.
 */
public class MergeExpr implements ICompilableExpression {
	private final ICompilableExpression[] inputs;
	private final int opcode;
	
	public static final int OPCODE_AND = Opcodes.IAND;
	public static final int OPCODE_OR = Opcodes.IOR;
	public static final int OPCODE_XOR = Opcodes.IXOR;
	
	/**
	 * Creates a MergeExpr.
	 * Prefer the createAND/createOR/createXOR methods to this as they may perform optimizations.
	 */
	public MergeExpr(int opcode, ICompilableExpression... inputs) {
		assert inputs.length > 0;
		
		this.inputs = inputs;
		this.opcode = opcode;
	}
	
	@Override
	public boolean alwaysInline() {
		return false;
	}
	
	@Override
	public void compile(ICompileContext ctx) {
		inputs[0].compile(ctx);
		for(int k = 1; k < inputs.length; k++) {
			inputs[k].compile(ctx);
			ctx.getCodeVisitor().visitInsn(opcode);
		}
	}
	
	/**
	 * Returns an expression which evaluates to the AND of all the inputs which are not null.
	 * If all elements of 'inputs' are null, or 'inputs' has length 0, returns a constant true expression.
	 */
	public static ICompilableExpression createAND(Collection<ICompilableExpression> inputs) {
		List<ICompilableExpression> valid = new ArrayList<ICompilableExpression>();
		for(ICompilableExpression e : inputs)
			if(e != null) {
				if(!(e instanceof OneExpr))
					valid.add(e);
				if(e instanceof ZeroExpr)
					return e;
			}
		
		if(valid.size() == 0)
			return new OneExpr();
		
		if(valid.size() == 1)
			return valid.get(0);
		
		return new MergeExpr(OPCODE_AND, valid.toArray(new ICompilableExpression[valid.size()]));
	}
	
	public static ICompilableExpression createAND(ICompilableExpression... inputs) {
		return createAND(Arrays.asList(inputs));
	}
	
	/**
	 * Returns an expression which evaluates to the OR of all the inputs which are not null.
	 * If all elements of 'inputs' are null, or 'inputs' has length 0, returns a constant false expression.
	 */
	public static ICompilableExpression createOR(Collection<ICompilableExpression> inputs) {
		List<ICompilableExpression> valid = new ArrayList<ICompilableExpression>();
		for(ICompilableExpression e : inputs)
			if(e != null) {
				if(!(e instanceof ZeroExpr))
					valid.add(e);
				if(e instanceof OneExpr)
					return e;
			}
		
		if(valid.size() == 0)
			return new ZeroExpr();
		
		if(valid.size() == 1)
			return valid.get(0);
		
		return new MergeExpr(OPCODE_OR, valid.toArray(new ICompilableExpression[valid.size()]));
	}
	
	public static ICompilableExpression createOR(ICompilableExpression... inputs) {
		return createOR(Arrays.asList(inputs));
	}
	
	/**
	 * Returns an expression which evaluates to the XOR of all the inputs which are not null.
	 * If all elements of 'inputs' are null, or 'inputs' has length 0, returns a constant false expression.
	 */
	public static ICompilableExpression createXOR(Collection<ICompilableExpression> inputs) {
		List<ICompilableExpression> valid = new ArrayList<ICompilableExpression>();
		for(ICompilableExpression e : inputs)
			if(e != null) {
				if(!(e instanceof ZeroExpr))
					valid.add(e);
			}
		
		if(valid.size() == 0)
			return new ZeroExpr();
		
		if(valid.size() == 1)
			return valid.get(0);
		
		return new MergeExpr(OPCODE_XOR, valid.toArray(new ICompilableExpression[valid.size()]));
	}
	
	public static ICompilableExpression createXOR(ICompilableExpression... inputs) {
		return createXOR(Arrays.asList(inputs));
	}
}