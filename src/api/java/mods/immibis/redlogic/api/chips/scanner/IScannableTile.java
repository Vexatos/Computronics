package mods.immibis.redlogic.api.chips.scanner;

/**
 * Implement this in your tile entity to allow it to be scanned.
 * 
 * Ignored for air blocks.
 */ 
public interface IScannableTile {
	/**
	 * Returns the IScannedBlock to use for this block, or null.
	 */
	public IScannedBlock getScannedBlock(IScanProcess process) throws CircuitLayoutException;
}
