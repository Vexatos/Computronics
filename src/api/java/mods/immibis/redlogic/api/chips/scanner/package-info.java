/**
 * Here is an overview of the internal steps of the circuit scanning process:
 * <ol>
 * <li>First, CircuitScanner (an internal class) iterates over blocks in the scan area and converts them to IScannedBlocks where applicable.
 *     Each IScannedBlock exposes some <i>nodes</i> (basically connection points).
 * <li>Then CircuitScanner merges connected nodes.
 * <li>This produces the final <i>network structure</i>, where all nodes that are adjacent or connected by wire blocks
 *     are in the same network. CircuitScanner creates <i>wires</i> for each network then returns.
 *     
 * <li>At this point, the circuit may be serialized and deserialized. This is why IScannedBlock extends Serializable.
 *     
 * <li>CircuitCompiler (another internal class) does the next stage. It examines the network structure and converts it to a directed graph of <i>blocks</i>, <i>inputs</i> and <i>outputs</i>.
 *     A block here is different from a Minecraft block - TODO rename?
 * <li>Each input is connected to a single output.
 *     If a wire has multiple outputs driving it, an OR block is generated between the outputs and the inputs.
 *     If a wire has no outputs, a FALSE block is generated.
 *     If a wire has multiple inputs, sometimes a CACHE block is added to avoid evaluating the output multiple times.
 *     Then all inputs on the wire are connected to the single output on that wire.
 * 
 * <li>The blocks are topologically sorted. Loops are broken by generating DELAY IN and DELAY OUT blocks.
 * <li>Then code is generated from this <i>digraph structure</i>, by compiling each block in topological order.
 * 
 * </ol>
 */
package mods.immibis.redlogic.api.chips.scanner;