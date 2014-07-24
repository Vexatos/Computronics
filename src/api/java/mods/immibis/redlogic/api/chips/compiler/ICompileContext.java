package mods.immibis.redlogic.api.chips.compiler;

import org.objectweb.asm.MethodVisitor;

public interface ICompileContext {
	/**
	 * Allocates a local variable.
	 * 
	 * @param desc The type of the local variable.
	 * @return The local variable index.
	 */
	public int createLocal(String desc);
	
	/**
	 * Allocates a field.
	 * 
	 * @param desc The type of the field.
	 * @return The name of the field.
	 */
	public String createField(String desc);
	
	/**
	 * Allocates a field with a default value.
	 * 
	 * NOT CURRENTLY IMPLEMENTED, throws UnsupportedOperationException.
	 * 
	 * @param desc The type of the field.
	 * @param initializer An expression which leaves on the stack the value to initialize the field with.
	 * @return The name of the field.
	 */
	public String createField(String desc, ICompilableExpression initializer);
	
	/**
	 * Returns the method visitor used to emit instructions.
	 * Frames do not need to be emitted; they are computed automatically.
	 */
	public MethodVisitor getCodeVisitor();

	/**
	 * Emits code to push the boolean value of an input onto the stack.
	 */
	public void loadInput(int dir, int wire);
	
	/**
	 * Emits code to pop a boolean value off the stack and assign it to an output.
	 */
	public void storeOutput(int dir, int wire);

	/**
	 * Returns internal-format (with slashes) class name.
	 */
	public String getClassNameInternal();
	
	/**
	 * Emits code to load a field in the circuit object.
	 */
	public void loadField(String name, String desc);
	
	/**
	 * Emits code to store a field in the circuit object.
	 */
	public void storeField(String name, String desc);
	
	/**
	 * Emits code to push a constant integer.
	 */
	public void pushInt(int i);
	
	/**
	 * Emits code to detect rising edges.
	 * Expects a boolean on the stack, and leaves another boolean on the stack after it runs.
	 * This is used to do something only once when a signal turns on, instead of every tick
	 * while the signal is on.
	 * See GateCounter for an example use.
	 * 
	 * To detect a falling edge, invert the signal first.
	 */
	public void detectRisingEdge();
}
