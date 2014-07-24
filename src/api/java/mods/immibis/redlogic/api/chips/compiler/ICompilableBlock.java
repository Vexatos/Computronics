package mods.immibis.redlogic.api.chips.compiler;

import mods.immibis.redlogic.api.chips.scanner.IScannedInput;
import mods.immibis.redlogic.api.chips.scanner.IScannedOutput;


/**
 * A "function block" that takes some inputs and produces some outputs.
 */
public interface ICompilableBlock {
	public IScannedInput[] getInputs();
	public IScannedOutput[] getOutputs();
	public ICompilableExpression[] compile(ICompileContext ctx, ICompilableExpression[] inputs);
}
