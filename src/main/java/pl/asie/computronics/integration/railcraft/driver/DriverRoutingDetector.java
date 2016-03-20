package pl.asie.computronics.integration.railcraft.driver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.util.RoutingTableUtil;
import pl.asie.computronics.reference.Names;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverRoutingDetector {

	private static Object[] getRoutingTable(TileDetector tile) {
		if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
			&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!((DetectorRouting) tile.getDetector()).isSecure()) {
				LinkedList<LinkedList<String>> pages = ItemRoutingTable.getPages(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0));
				if(pages == null) {
					return new Object[] { false, "no valid routing table found" };
				}
				LinkedHashMap<Integer, String> pageMap = new LinkedHashMap<Integer, String>();
				int i = 1;
				for(LinkedList<String> currentPage : pages) {
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
				return new Object[] { false, "routing detector is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	private static Object[] setRoutingTable(TileDetector tile, Object[] arguments) {
		Map pageMap = (Map) arguments[0];
		if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
			&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!((DetectorRouting) tile.getDetector()).isSecure()) {
				LinkedList<LinkedList<String>> pages = new LinkedList<LinkedList<String>>();
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
				ItemRoutingTable.setPages(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0), pages);
				return new Object[] { true };
			} else {
				return new Object[] { false, "routing detector is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	public static Object[] getRoutingTableTitle(TileDetector tile) {
		if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
			&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!((DetectorRouting) tile.getDetector()).isSecure()) {
				return new Object[] { RoutingTableUtil.getRoutingTableTitle(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0)) };
			} else {
				return new Object[] { false, "routing detector is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	public static Object[] setRoutingTableTitle(TileDetector tile, Object[] arguments) {
		if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
			&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
			if(!((DetectorRouting) tile.getDetector()).isSecure()) {
				return new Object[] { RoutingTableUtil.setRoutingTableTitle(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0), (String) arguments[0]) };
			} else {
				return new Object[] { false, "routing detector is locked" };
			}
		}
		return new Object[] { false, "no routing table found" };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileDetector> {

			public InternalManagedEnvironment(TileDetector detector) {
				super(detector, Names.Railcraft_RoutingDetector);
			}

			@Callback(doc = "function():table; Returns the full routing table inside the detector, or false and an error message if there is no table or it cannot be accessed")
			public Object[] getRoutingTable(Context c, Arguments a) {
				return DriverRoutingDetector.getRoutingTable(tile);
			}

			@Callback(doc = "function(routingTable:table):boolean; Sets the routing table inside the detector; argument needs to be a table with number indices and string values; returns 'true' on success, 'false' and an error message otherwise.")
			public Object[] setRoutingTable(Context c, Arguments a) {
				a.checkTable(0);
				return DriverRoutingDetector.setRoutingTable(tile, a.toArray());
			}

			@Callback(doc = "function():string; Returns the name of the routing table inside the detector")
			public Object[] getRoutingTableTitle(Context c, Arguments a) {
				return DriverRoutingDetector.getRoutingTableTitle(tile);
			}

			@Callback(doc = "function(name:string):boolean; Sets the name of the routing table inside the detector; returns true on success")
			public Object[] setRoutingTableTitle(Context c, Arguments a) {
				a.checkString(0);
				return DriverRoutingDetector.setRoutingTableTitle(tile, a.toArray());
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileDetector.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileDetector
				&& ((TileDetector) tileEntity).getDetector().getType() == EnumDetector.ROUTING;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TileDetector) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileDetector> {

		public CCDriver() {
		}

		public CCDriver(TileDetector detector, World world, int x, int y, int z) {
			super(detector, Names.Railcraft_RoutingDetector, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileDetector && ((TileDetector) te).getDetector().getType() == EnumDetector.ROUTING) {
				return new CCDriver((TileDetector) te, world, x, y, z);
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
					return DriverRoutingDetector.getRoutingTable(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Map)) {
						throw new LuaException("first argument needs to be a table");
					}
					return DriverRoutingDetector.setRoutingTable(tile, arguments);
				}
				case 2: {
					return DriverRoutingDetector.getRoutingTableTitle(tile);
				}
				case 3: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return DriverRoutingDetector.setRoutingTableTitle(tile, arguments);
				}
			}
			return null;
		}
	}
}
