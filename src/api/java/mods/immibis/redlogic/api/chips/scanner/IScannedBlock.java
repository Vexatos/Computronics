package mods.immibis.redlogic.api.chips.scanner;

import java.io.Serializable;
import java.util.Collection;

import mods.immibis.redlogic.api.chips.compiler.ICompilableBlock;


/**
 * Represents a block, during and after the scanning process (which converts a circuit in the world
 * into a circuit in a schematic).
 * 
 * Note: Wires are handled specially. There is currently no way to make a non-wire block that connects
 * around corners when scanned.
 */
public interface IScannedBlock extends Serializable {
	/**
	 * Returns the node connecting to wires in the specified side and direction.
	 * 
	 * Returns null if this block does not connect here.
	 * 
	 * Note: The circuit scanning process will poll this for every combination of wireside and dir.
	 * Do not perform slow operations here.
	 * 
	 * @see mods.immibis.redlogic.api.wiring.IConnectable
	 */
	public IScannedNode getNode(int wireside, int dir);
	
	/**
	 * UNUSED. <s>Called after a connection is made to a block.
	 * 
	 * wireside and dir specify where the connection was made on this block.</s>
	 * 
	 * @see mods.immibis.redlogic.api.wiring.IConnectable
	 * 
	 * @throws CircuitLayoutException To halt scanning with an error message.
	 */
	public void onConnect(IScannedBlock with, int wireside, int dir) throws CircuitLayoutException;
	
	/**
	 * Returns the ICompilableBlocks that this IScannedBlock transforms into.
	 * 
	 * @see mods.immibis.redlogic.api.chips.compiler.ICompilableBlock
	 */
	public Collection<ICompilableBlock> toCompilableBlocks();
}
