package pl.asie.computronics.integration.fsp;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import flaxbeard.steamcraft.api.ISteamTransporter;

public class SteamTransporterPeripheral implements IPeripheral, IPeripheralProvider {
	private ISteamTransporter block;
	private IBlockAccess w;
	private int x, y, z;
	
	public SteamTransporterPeripheral() { }
	
	public SteamTransporterPeripheral(ISteamTransporter block2, World world, int x2, int y2, int z2) {
		block = block2; w = world; x = x2; y = y2; z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if(block instanceof ISteamTransporter) return new SteamTransporterPeripheral((ISteamTransporter)block, world, x, y, z);
		return null;
	}

	@Override
	public String getType() {
		return "steam_transporter";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"getSteamPressure", "getSteamCapacity", "getSteamAmount"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch(method) {
		case 0: return new Object[]{new Double(block.getPressure())};
		case 1: return new Object[]{block.getCapacity()};
		case 2: return new Object[]{block.getSteam()};
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) { }
	@Override
	public void detach(IComputerAccess computer) { }

	@Override
	public boolean equals(IPeripheral other) {
		if(other == null) return false;
		if(this == other) return true;
		if(other instanceof SteamTransporterPeripheral) {
			SteamTransporterPeripheral o = (SteamTransporterPeripheral)other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) return true;
		}
		
		return false;
	}
}
