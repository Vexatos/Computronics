package pl.asie.computronics.integration.mfr;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import mods.immibis.redlogic.api.misc.ILampBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class DeepStorageUnitPeripheral implements IPeripheral, IPeripheralProvider {
	private IDeepStorageUnit dsu;
	private IBlockAccess w;
	private int x, y, z;
	
	public DeepStorageUnitPeripheral() { }
	
	public DeepStorageUnitPeripheral(IDeepStorageUnit dsu, World world, int x2, int y2, int z2) {
		this.dsu = dsu; w = world; x = x2; y = y2; z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IDeepStorageUnit)
			return new DeepStorageUnitPeripheral((IDeepStorageUnit)te, world, x, y, z);
		return null;
	}

	@Override
	public String getType() {
		return "dsu";
	}
	
	@Override
	public String[] getMethodNames() {
		return new String[]{"getItemName", "getItemDamage", "getItemCount", "isLocked", "getMaxItemCount"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		ItemStack is = dsu.getStoredItemType();
		if(method < 4 && (is == null)) {
			if(method == 3) return new Object[]{false};
			else if(method > 0) return new Object[]{0};
			else return new Object[]{null};
		}
		switch(method) {
		case 0: return new Object[]{is.getUnlocalizedName()};
		case 1: return new Object[]{is.getItemDamage()};
		case 2: return new Object[]{is.stackSize};
		case 3: return new Object[]{is.stackSize == 0};
		case 4: return new Object[]{dsu.getMaxStoredCount()};
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
		if(other instanceof DeepStorageUnitPeripheral) {
			DeepStorageUnitPeripheral o = (DeepStorageUnitPeripheral)other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) return true;
		}
		
		return false;
	}
}
