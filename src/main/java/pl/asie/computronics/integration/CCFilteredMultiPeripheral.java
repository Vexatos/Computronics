package pl.asie.computronics.integration;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vexatos
 */
public abstract class CCFilteredMultiPeripheral<T> extends CCMultiPeripheral<T> {

	protected final List<String> methods;
	protected List<String> availableMethods;

	public CCFilteredMultiPeripheral() {
		methods = Collections.emptyList();
	}

	public CCFilteredMultiPeripheral(T tile, String name, World world, BlockPos pos) {
		super(tile, name, world, pos);
		this.methods = getAllMethods();
	}

	protected abstract List<String> getAllMethods();

	protected abstract boolean isMethodEnabled(String name);

	protected boolean isMethodEnabled(int method) {
		return isMethodEnabled(methods.get(method));
	}

	@Override
	public String[] getMethodNames() {
		availableMethods = new ArrayList<String>();
		for(String method : methods) {
			if(isMethodEnabled(method)) {
				availableMethods.add(method);
			}
		}
		return availableMethods.toArray(new String[availableMethods.size()]);
	}

	protected abstract Object[] call(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException;

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if(method < 0 || method >= availableMethods.size() || availableMethods.get(method) == null) {
			throw new LuaException("not a valid function");
		}

		final int index = methods.indexOf(availableMethods.get(method));
		if(index < 0 || index >= methods.size()) {
			throw new LuaException("not a valid function");
		}
		return call(computer, context, index, arguments);
	}
}
