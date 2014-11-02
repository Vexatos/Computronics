package pl.asie.computronics.integration.util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

/**
 * @author Vexatos
 */
public abstract class CCMultiPeripheral<T> extends CCTilePeripheral<T> {

	protected CCMultiPeripheral() {

	}

	protected CCMultiPeripheral(T tile, String name, World world, int x, int y, int z) {
		super(tile, name, world, x, y, z);
	}

	@Override
	public abstract CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side);

	public int priority() {
		return 0;
	}

	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String methodName, Object[] arguments) throws LuaException, InterruptedException {
		String[] methods = this.getMethodNames();
		if(methods != null) {
			for(int i = 0; i < methods.length; i++) {
				if(methods[i].equals(methodName)) {
					return this.callMethod(computer, context, i, arguments);
				}
			}
		}
		return null;
	}
}
