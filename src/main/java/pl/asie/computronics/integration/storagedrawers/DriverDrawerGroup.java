package pl.asie.computronics.integration.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverDrawerGroup {

	private static void checkValidDrawer(IDrawerGroup tile, int slot) {
		if(!tile.isDrawerEnabled(slot) || tile.getDrawer(slot) == null) {
			++slot;
			throw new IllegalArgumentException("no drawer found at slot " + slot);
		}
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IDrawerGroup> {

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
				if(stack == null || stack.getItem() == null) {
					return new Object[] { null, "there no item in this drawer slot" };
				}
				return new Object[] { stack.getUnlocalizedName() };
			}

			@Callback(doc = "function(drawerslot:number):number; Returns the damage value of the item in the specified drawer slot")
			public Object[] getItemDamage(Context c, Arguments a) {
				int slot = a.checkInteger(0) - 1;
				checkValidDrawer(tile, slot);
				ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
				if(stack == null || stack.getItem() == null) {
					return new Object[] { null, "there no item in this drawer slot" };
				}
				return new Object[] { tile.getDrawer(slot).getStoredItemPrototype().getItemDamage() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return IDrawerGroup.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((IDrawerGroup) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IDrawerGroup> {

		public CCDriver() {
		}

		public CCDriver(IDrawerGroup tile, World world, int x, int y, int z) {
			super(tile, Names.StorageDrawers_DrawerGroup, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof IDrawerGroup) {
				return new CCDriver((IDrawerGroup) te, world, x, y, z);
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
						if(stack == null || stack.getItem() == null) {
							return new Object[] { null, "there no item in this drawer slot" };
						}
						return new Object[] { stack.getUnlocalizedName() };
					}
					case 4: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						int slot = ((Number) arguments[0]).intValue() - 1;
						checkValidDrawer(tile, slot);
						ItemStack stack = tile.getDrawer(slot).getStoredItemPrototype();
						if(stack == null || stack.getItem() == null) {
							return new Object[] { null, "there no item in this drawer slot" };
						}
						return new Object[] { tile.getDrawer(slot).getStoredItemPrototype().getItemDamage() };
					}
				}
				return null;
			} catch(Exception e) {
				throw new LuaException(e.getMessage());
			}
		}
	}
}
