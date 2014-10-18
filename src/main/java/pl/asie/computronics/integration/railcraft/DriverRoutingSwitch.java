package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.signals.TileSwitchRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.util.RoutingTableUtil;
import pl.asie.computronics.reference.Names;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverRoutingSwitch {

	public static class OCDriver extends DriverTileEntity {
		private class ManagedEnvironmentRoutingSwitch extends ManagedEnvironmentOCTile<TileSwitchRouting> implements NamedBlock {

			public ManagedEnvironmentRoutingSwitch(TileSwitchRouting routingSwitch) {
				super(routingSwitch, Names.RoutingSwitch);
			}

			@Callback(doc = "function():table; returns the full routing table inside the switch motor, or false and an error message if the table is empty or cannot be accessed")
			public Object[] getRoutingTable(Context c, Arguments a) {
				if((((TileSwitchRouting) tile)).getInventory().getStackInSlot(0) != null
					&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
					if(!(((TileSwitchRouting) tile)).isSecure()) {
						List<List<String>> pages = ItemRoutingTable.getPages(tile.getInventory().getStackInSlot(0));
						LinkedHashMap<Integer, String> pageMap = new LinkedHashMap<Integer, String>();
						int i = 1;
						for(List<String> currentPage : pages) {
							for(String currentLine : currentPage) {
								pageMap.put(i, currentLine);
								i++;
							}
							pageMap.put(i, "{newpage}");
							i++;
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

			@Callback(doc = "function(routingTable:table):boolean; Sets the routing table inside the switch; argument needs to be a table with number indices and string values, every value being a new line, for a new page, use '{newline}' as a value; returns 'true' on success, 'false' and an error message otherwise")
			public Object[] setRoutingTable(Context c, Arguments a) {
				Map pageMap = a.checkTable(0);
				if(tile.getInventory().getStackInSlot(0) != null
					&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
					if(!tile.isSecure()) {
						List<List<String>> pages = new ArrayList<List<String>>();
						pages.add(new ArrayList<String>());
						int pageIndex = 0;
						for(Object key : pageMap.keySet()) {
							Object line = pageMap.get(key);
							if(line instanceof String) {
								if(((String) line).toLowerCase().equals("{newline}")) {
									pages.add(new ArrayList<String>());
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

			@Callback(doc = "function():string; Returns the name of the routing table inside the switch motor")
			public Object[] getRoutingTableTitle(Context c, Arguments a) {
				if((((TileSwitchRouting) tile)).getInventory().getStackInSlot(0) != null
					&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
					if(!(((TileSwitchRouting) tile)).isSecure()) {
						return new Object[] { RoutingTableUtil.getRoutingTableTitle(tile.getInventory().getStackInSlot(0)) };
					} else {
						return new Object[] { false, "routing switch is locked" };
					}
				}
				return new Object[] { false, "no routing table found" };
			}

			@Callback(doc = "function(name:string):boolean; Sets the name of the routing table inside the switch motor; returns true on success")
			public Object[] setRoutingTableTitle(Context c, Arguments a) {
				if(tile.getInventory().getStackInSlot(0) != null
					&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
					if(!tile.isSecure()) {
						return new Object[] { RoutingTableUtil.setRoutingTableTitle(tile.getInventory().getStackInSlot(0), a.checkString(0)) };
					} else {
						return new Object[] { false, "routing switch is locked" };
					}
				}
				return new Object[] { false, "no routing table found" };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileSwitchRouting.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new ManagedEnvironmentRoutingSwitch((TileSwitchRouting) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCTilePeripheral<TileSwitchRouting> {

		public CCDriver() {
		}

		public CCDriver(TileSwitchRouting routingSwitch, World world, int x, int y, int z) {
			super(routingSwitch, Names.RoutingSwitch, world, x, y, z);
		}

		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileSwitchRouting) {
				return new CCDriver((TileSwitchRouting) te, world, x, y, z);
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
			switch(method){
				case 0:{
					if(tile.getInventory().getStackInSlot(0) != null
						&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!tile.isSecure()) {
							List<List<String>> pages = ItemRoutingTable.getPages(tile.getInventory().getStackInSlot(0));
							LinkedHashMap<Integer, String> pageMap = new LinkedHashMap<Integer, String>();
							int i = 1;
							for(List<String> currentPage : pages) {
								for(String currentLine : currentPage) {
									pageMap.put(i, currentLine);
									i++;
								}
								pageMap.put(i, "{newpage}");
								i++;
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
				case 1:{
					if(arguments.length < 1 || !(arguments[0] instanceof Map)) {
						throw new LuaException("first argument needs to be a table");
					}
					Map pageMap = (Map) arguments[0];
					if(tile.getInventory().getStackInSlot(0) != null
						&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!tile.isSecure()) {
							List<List<String>> pages = new ArrayList<List<String>>();
							pages.add(new ArrayList<String>());
							int pageIndex = 0;
							for(Object key : pageMap.keySet()) {
								Object line = pageMap.get(key);
								if(line instanceof String) {
									if(((String) line).toLowerCase().equals("{newline}")) {
										pages.add(new ArrayList<String>());
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
				case 2:{
					if((((TileSwitchRouting) tile)).getInventory().getStackInSlot(0) != null
						&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!(((TileSwitchRouting) tile)).isSecure()) {
							return new Object[] { RoutingTableUtil.getRoutingTableTitle(tile.getInventory().getStackInSlot(0)) };
						} else {
							return new Object[] { false, "routing switch is locked" };
						}
					}
					return new Object[] { false, "no routing table found" };
				}
				case 3:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					if(tile.getInventory().getStackInSlot(0) != null
						&& tile.getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!tile.isSecure()) {
							return new Object[] { RoutingTableUtil.setRoutingTableTitle(tile.getInventory().getStackInSlot(0), (String) arguments[0]) };
						} else {
							return new Object[] { false, "routing switch is locked" };
						}
					}
					return new Object[] { false, "no routing table found" };
				}
			}
			return null;
		}
	}
}
