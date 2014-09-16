package pl.asie.computronics.integration.fsp;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import flaxbeard.steamcraft.api.ISteamTransporter;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

public class SteamTransporterPeripheral extends CCTilePeripheral<ISteamTransporter> {

	public SteamTransporterPeripheral() {
	}

	public SteamTransporterPeripheral(ISteamTransporter block, World world, int x, int y, int z) {
		super(block, "steam_transporter", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if(block instanceof ISteamTransporter) {
			return new SteamTransporterPeripheral((ISteamTransporter) block, world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getSteamPressure", "getSteamCapacity", "getSteamAmount" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		switch(method){
			case 0:
				return new Object[] { (double) tile.getPressure() };
			case 1:
				return new Object[] { tile.getCapacity() };
			case 2:
				return new Object[] { tile.getSteam() };
		}
		return null;
	}
}
