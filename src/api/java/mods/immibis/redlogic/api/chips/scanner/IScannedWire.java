package mods.immibis.redlogic.api.chips.scanner;

/**
 * Represents a single wire - like a red alloy wire or a single strand of bundled cable -
 * in the network structure.
 * 
 * Do not implement this. Instances may be obtained from IScannedNode after the scanning phase is complete.
 */
public interface IScannedWire {
	public void addInput(IScannedInput input);
	public void addOutput(IScannedOutput output);
}
