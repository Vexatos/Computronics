package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.TileActuatorRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.integration.util.RoutingTableUtil;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverRoutingActuator {

	private static Object[] getRoutingTable(TileActuatorRouting tile) {
		if(tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!tile.isSecure()) {
				List<List<String>> pages = ItemRoutingTable.getPages(tile.getInventory().getStackInSlot(0));
				if(pages == null) {
					return new Object[] { false, "no valid routing table found" };
				}
				LinkedHashMap<Integer, String> pageMap = new LinkedHashMap<Integer, String>();
				int i = 1;
				for(List<String> currentPage : pages) {
					for(String currentLine : currentPage) {
						pageMap.put(i++, currentLine);
					}
					pageMap.put(i++, "{newpage}");
				}
				if(pageMap.get(i - 1).equals("{newpage}")) {
					pageMap.remove(i - 1);
				}
				return new Object[] { pageMap };
			} else {
				return new Object[] { false, "routing switch is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	private static Object[] setRoutingTable(TileActuatorRouting tile, Object[] arguments) {
		Map pageMap = (Map) arguments[0];
		if(tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!tile.isSecure()) {
				LinkedList<List<String>> pages = new LinkedList<List<String>>();
				pages.add(new LinkedList<String>());
				int pageIndex = 0;
				for(Object line : pageMap.values()) {
					//Object line = pageMap.get(key);
					if(line instanceof String) {
						if(((String) line).toLowerCase().equals("{newline}")) {
							pages.add(new LinkedList<String>());
							pageIndex++;
						} else {
							pages.get(pageIndex).add((String) line);
						}
					}
				}
				ItemRoutingTable.setPages(tile.getInventory().getStackInSlot(0), pages);
				return new Object[] { true };
			} else {
				return new Object[] { false, "routing switch is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	private static Object[] getRoutingTableTitle(TileActuatorRouting tile) {
		if(tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!(((TileActuatorRouting) tile)).isSecure()) {
				return new Object[] { RoutingTableUtil.getRoutingTableTitle(tile.getInventory().getStackInSlot(0)) };
			} else {
				return new Object[] { false, "routing switch is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	private static Object[] setRoutingTableTitle(TileActuatorRouting tile, Object[] arguments) {
		if(tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!tile.isSecure()) {
				return new Object[] { RoutingTableUtil.setRoutingTableTitle(tile.getInventory().getStackInSlot(0), (String) arguments[0]) };
			} else {
				return new Object[] { false, "routing switch is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	public static class OCDriver extends DriverSpecificTileEntity<TileActuatorRouting> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TileActuatorRouting> {

			public InternalManagedEnvironment(TileActuatorRouting routingSwitch) {
				super(routingSwitch, Names.Railcraft_RoutingSwitch);
			}

			@Callback(doc = "function():table; returns the full routing table inside the switch motor, or false and an error message if the table is empty or cannot be accessed")
			public Object[] getRoutingTable(Context c, Arguments a) {
				return DriverRoutingActuator.getRoutingTable(tile);
			}

			@Callback(doc = "function(routingTable:table):boolean; Sets the routing table inside the switch; argument needs to be a table with number indices and string values, every value being a new line, for a new page, use '{newline}' as a value; returns 'true' on success, 'false' and an error message otherwise")
			public Object[] setRoutingTable(Context c, Arguments a) {
				a.checkTable(0);
				return DriverRoutingActuator.setRoutingTable(tile, a.toArray());
			}

			@Callback(doc = "function():string; Returns the name of the routing table inside the switch motor")
			public Object[] getRoutingTableTitle(Context c, Arguments a) {
				return DriverRoutingActuator.getRoutingTableTitle(tile);
			}

			@Callback(doc = "function(name:string):boolean; Sets the name of the routing table inside the switch motor; returns true on success")
			public Object[] setRoutingTableTitle(Context c, Arguments a) {
				a.checkString(0);
				return DriverRoutingActuator.setRoutingTableTitle(tile, a.toArray());
			}
		}

		public OCDriver() {
			super(TileActuatorRouting.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TileActuatorRouting> createEnvironment(World world, BlockPos pos, EnumFacing side, TileActuatorRouting tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileActuatorRouting> {

		public CCDriver() {
		}

		public CCDriver(TileActuatorRouting routingSwitch, World world, BlockPos pos) {
			super(routingSwitch, Names.Railcraft_RoutingSwitch, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileActuatorRouting) {
				return new CCDriver((TileActuatorRouting) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getRoutingTable", "setRoutingTable", "getRoutingTableTitle", "setRoutingTableTitle" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0: {
					return DriverRoutingActuator.getRoutingTable(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Map)) {
						throw new LuaException("first argument needs to be a table");
					}
					return DriverRoutingActuator.setRoutingTable(tile, arguments);
				}
				case 2: {
					return DriverRoutingActuator.getRoutingTableTitle(tile);
				}
				case 3: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return DriverRoutingActuator.setRoutingTableTitle(tile, arguments);
				}
			}
			return null;
		}
	}
}
