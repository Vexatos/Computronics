package pl.asie.computronics.integration.factorization;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import factorization.api.IChargeConductor;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

public class ChargeConductorPeripheral extends CCTilePeripheral<IChargeConductor> {

	public ChargeConductorPeripheral() {
	}

	public ChargeConductorPeripheral(IChargeConductor block, World world, int x, int y, int z) {
		super(block, "charge_conductor", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if(block instanceof IChargeConductor) {
			return new ChargeConductorPeripheral((IChargeConductor) block, world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getCharge" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		if(tile.getCharge() == null) {
			return null;
		}
		switch(method){
			case 0:
				return new Object[] { tile.getCharge().getValue() };
		}
		return null;
	}
}
