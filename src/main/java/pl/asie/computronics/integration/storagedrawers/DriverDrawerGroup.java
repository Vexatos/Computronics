package pl.asie.computronics.integration.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverDrawerGroup {

	private static void checkValidDrawer(IDrawerGroup tile, int slot) {
		if(!tile.getDrawer(slot).isEnabled()) {
			++slot;
			throw new IllegalArgumentException("no drawer found at slot " + slot);
		}
	}

	public static class OCDriver extends DriverSpecificTileEntity<IDrawerGroup> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<IDrawerGroup> {

			public InternalManagedEnvironment(IDrawerGroup tile) {
				super(tile, Names.StorageDrawers_DrawerGroup);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function(drawerslot:number):number; Returns the number of items in the specified drawer slot.")
			public Object[] getItemCount(Context c, Arguments a) {
				int slot = a.checkInteger(0) - 1;
				checkValidDrawer(tile, slot);
				return new Object[] { tile.getDrawer(slot).getStoredItemCount() };
			}

			@Callback(doc = "function(drawerslot:number):number; Returns the maximum number of items that can be stored in the specified drawer slot.")
			public Object[] getMaxCapacity(Context c, Arguments a) {
				int slot = a.checkInteger(0) - 1;
				checkValidDrawer(tile, slot);
				return new Object[] { tile.getDrawer(slot).getMaxCapacity() };
			}

			@Callback(doc = "function():number; Returns the number of drawers in this block.")
			public Object[] getDrawerCount(Context c, Arguments a) {
				return new Object[] { tile.getDrawerCount() };
			}

			@Callback(doc = "function(drawerslot:number):string; Returns the name of the item in the specified drawer slot")
			public Object[] getItemName(Context c, Arguments a) {
				int slot = a.checkInteger(0) - 1;
				checkValidDrawer(tile, slot);
				ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
				if(stack.isEmpty()) {
					return new Object[] { null, "there no item in this drawer slot" };
				}
				return new Object[] { stack.getTranslationKey() };
			}

			@Callback(doc = "function(drawerslot:number):number; Returns the damage value of the item in the specified drawer slot")
			public Object[] getItemDamage(Context c, Arguments a) {
				int slot = a.checkInteger(0) - 1;
				checkValidDrawer(tile, slot);
				ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
				if(stack.isEmpty()) {
					return new Object[] { null, "there no item in this drawer slot" };
				}
				return new Object[] { tile.getDrawer(slot).getStoredItemPrototype().getItemDamage() };
			}
		}

		public OCDriver() {
			super(IDrawerGroup.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, IDrawerGroup tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IDrawerGroup> {

		public CCDriver() {
		}

		public CCDriver(IDrawerGroup tile, World world, BlockPos pos) {
			super(tile, Names.StorageDrawers_DrawerGroup, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IDrawerGroup) {
				return new CCDriver((IDrawerGroup) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getItemCount", "getMaxCapacity", "getDrawerCount", "getItemName", "getItemDamage" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			try {
				switch(method) {
					case 0: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						int slot = ((Number) arguments[0]).intValue() - 1;
						checkValidDrawer(tile, slot);
						return new Object[] { tile.getDrawer(slot).getStoredItemCount() };
					}
					case 1: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						int slot = ((Number) arguments[0]).intValue() - 1;
						checkValidDrawer(tile, slot);
						return new Object[] { tile.getDrawer(slot).getMaxCapacity() };
					}
					case 2: {
						return new Object[] { tile.getDrawerCount() };
					}
					case 3: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						int slot = ((Number) arguments[0]).intValue() - 1;
						checkValidDrawer(tile, slot);
						ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
						if(stack.isEmpty()) {
							return new Object[] { null, "there no item in this drawer slot" };
						}
						return new Object[] { stack.getTranslationKey() };
					}
					case 4: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						int slot = ((Number) arguments[0]).intValue() - 1;
						checkValidDrawer(tile, slot);
						ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
						if(stack.isEmpty()) {
							return new Object[] { null, "there no item in this drawer slot" };
						}
						return new Object[] { tile.getDrawer(slot).getStoredItemPrototype().getItemDamage() };
					}
				}
				return new Object[] {};
			} catch(Exception e) {
				throw new LuaException(e.getMessage());
			}
		}
	}
}
