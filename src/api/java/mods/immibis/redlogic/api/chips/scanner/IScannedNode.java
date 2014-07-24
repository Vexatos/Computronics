package mods.immibis.redlogic.api.chips.scanner;

import java.io.Serializable;

/**
 * Holds a pointer to some IScannedWires, which can be updated internally
 * as networks are merged.
 * 
 * Create one of these (through IScanProcess) for each "connection point" on your block.
 * 
 * Do not implement this. Instances may be obtained from IScanProcess.createNode(type)
 */
public interface IScannedNode extends Serializable {
	/**
	 * Merges this node with the given node.
	 * @throws IllegalStateException if finished scanning.
	 */
	public void mergeWith(IScannedNode node);
	
	/**
	 * Gets number of wires (e.g. 16 for bundled nodes)
	 * @throws IllegalStateException if still scanning.
	 */
	public int getNumWires();
	
	/**
	 * Gets a wire.
	 * @throws IllegalStateException if still scanning.
	 */
	public IScannedWire getWire(int index);

	public IScannedNode getSubNode(int wire);
}
