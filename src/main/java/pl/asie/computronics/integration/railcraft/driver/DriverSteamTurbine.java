package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.common.blocks.logic.SteamTurbineLogic;
import mods.railcraft.common.blocks.structures.TileSteamTurbine;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

import java.util.Optional;

/**
 * @author Vexatos
 */
public class DriverSteamTurbine {

	public static class CCDriver extends CCMultiPeripheral<TileSteamTurbine> {

		public CCDriver() {
			super();
		}

		public CCDriver(TileSteamTurbine tile, World world, BlockPos pos) {
			super(tile, Names.Railcraft_SteamTurbine, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileSteamTurbine) {
				return new CCDriver((TileSteamTurbine) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getOperatingRatio", "getTurbineRotorStatus" };
		}

		//Yes, this is mostly stolen from Sangar's Steam Turbine Driver.
		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getLogic(SteamTurbineLogic.class).map(logic -> logic.operatingRatio).orElse(0.0) };
				}
				case 1: {
					final Optional<SteamTurbineLogic> logic = tile.getLogic(SteamTurbineLogic.class);
					if(logic.isPresent()) {
						final IInventory inventory = logic.get().getInventory();
						if(inventory != null && inventory.getSizeInventory() > 0) {
							final ItemStack itemStack = inventory.getStackInSlot(0);
							if(itemStack != null && itemStack != ItemStack.EMPTY) {
								return new Object[] { 100 - (int) (itemStack.getItemDamage() * 100.0 / itemStack.getMaxDamage()) };
							}
						}
					}
					return new Object[] { 0 };
				}
			}
			return null;
		}
	}
}
