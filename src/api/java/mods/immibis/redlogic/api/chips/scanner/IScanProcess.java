package mods.immibis.redlogic.api.chips.scanner;

/**
 * Interface to "global" facilities while scanning.
 * 
 * Do not implement this.
 * 
 * The IScanProcess implementation must be Serializable. It is safe to serialize a reference to the IScanProcess from
 * within your IScannedBlocks.
 */
public interface IScanProcess {
	public IScannedNode createNode(NodeType type);
	
	public IScannedNode getInputNode(int dir, NodeType type);
	public IScannedNode getOutputNode(int dir, NodeType type);

	public IScannedOutput createOutput();
	public IScannedInput createInput();
}
