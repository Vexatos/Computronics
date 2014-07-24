package pl.asie.computronics.integration.redlogic;

import mods.immibis.redlogic.api.misc.ILampBlock;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class LampPeripheral implements IPeripheral, IPeripheralProvider {
	private ILampBlock block;
	private IBlockAccess w;
	private int x, y, z;
	
	public LampPeripheral() { }
	
	public LampPeripheral(ILampBlock block2, World world, int x2, int y2, int z2) {
		block = block2; w = world; x = x2; y = y2; z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if(block instanceof ILampBlock) return new LampPeripheral((ILampBlock)block, world, x, y, z);
		return null;
	}

	@Override
	public String getType() {
		return "lamp";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"getLampColour", "isLampPowered", "getLampType"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch(method) {
		case 0: return new Object[]{block.getColourRGB(w, x, y, z)};
		case 1: return new Object[]{block.isPowered()};
		case 2: return new Object[]{block.getType().name()};
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
		if(other instanceof LampPeripheral) {
			LampPeripheral o = (LampPeripheral)other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) return true;
		}
		
		return false;
	}
}
