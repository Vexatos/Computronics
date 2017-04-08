package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverSteamTurbine {

	public static class CCDriver extends CCMultiPeripheral<TileSteamTurbine> {

		public CCDriver() {
			super();
		}

		public CCDriver(TileSteamTurbine tile, World world, int x, int y, int z) {
			super(tile, Names.Railcraft_SteamTurbine, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileSteamTurbine) {
				return new CCDriver((TileSteamTurbine) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getTurbineOutput", "getTurbineRotorStatus" };
		}

		//Yes, this is mostly stolen from Sangar's Steam Turbine Driver.
		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getOutput() };
				}
				case 1: {
					final IInventory inventory = tile.getInventory();
					if(inventory != null && inventory.getSizeInventory() > 0) {
						final ItemStack itemStack = inventory.getStackInSlot(0);
						if(itemStack != null) {
							return new Object[] { 100 - (int) (itemStack.getItemDamage() * 100.0 / itemStack.getMaxDamage()) };
						}
					}
					return new Object[] { 0 };
				}
			}
			return null;
		}
	}
}
