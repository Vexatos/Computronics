package mods.immibis.redlogic.api.chips.scanner;

public enum NodeType {
	SINGLE_WIRE(1),
	BUNDLED(16);
	
	public final int numWires;
	
	private NodeType(int numWires) {
		this.numWires = numWires;
	}
}
