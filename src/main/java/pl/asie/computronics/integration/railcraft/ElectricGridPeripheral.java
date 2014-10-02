package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import mods.railcraft.api.electricity.GridTools;
import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

/**
 * @author Vexatos
 */
public class ElectricGridPeripheral extends CCTilePeripheral<IElectricGrid> {

	public ElectricGridPeripheral() {
		super();
	}

	public ElectricGridPeripheral(IElectricGrid tile, String name, World world, int x, int y, int z) {
		super(tile, name, world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		if(GridTools.getGridObjectAt(world, x, y, z) != null) {
			return new ElectricGridPeripheral(GridTools.getGridObjectAt(world, x, y, z), "electric_tile", world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getCharge", "getCapacity", "getLoss" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch(method){
			case 0:{
				return new Object[] { tile.getChargeHandler().getCharge() };
			}
			case 1:{
				return new Object[] { tile.getChargeHandler().getCapacity() };
			}
			case 2:{
				return new Object[] { tile.getChargeHandler().getLosses() };
			}
		}
		return null;
	}
}
