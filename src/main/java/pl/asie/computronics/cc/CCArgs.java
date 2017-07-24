package pl.asie.computronics.cc;

import dan200.computercraft.api.lua.LuaException;

/**
 * @author Vexatos
 */
public class CCArgs {

	private final Object[] arguments;

	public CCArgs(Object[] arguments) {
		this.arguments = arguments;
	}

	@SuppressWarnings("unchecked")
	private <T> T check(int index, Class<T> clazz, String name) throws LuaException {
		if(arguments.length < index + 1 || !(clazz.isInstance(arguments[index]))) {
			throw new LuaException(String.format("bad argument #%d (expected %s)", index + 1, name));
		}
		return (T) arguments[index];
	}

	public boolean checkBoolean(int index) throws LuaException {
		return check(index, Boolean.class, "boolean");
	}

	public double checkDouble(int index) throws LuaException {
		return check(index, Number.class, "number").doubleValue();
	}

	public int checkInteger(int index) throws LuaException {
		return check(index, Number.class, "number").intValue();
	}

	public String checkString(int index) throws LuaException {
		return check(index, String.class, "string");
	}
}
