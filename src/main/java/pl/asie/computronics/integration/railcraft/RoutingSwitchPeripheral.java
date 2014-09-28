package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import mods.railcraft.common.blocks.signals.TileSwitchRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class RoutingSwitchPeripheral extends CCTilePeripheral<TileSwitchRouting> {

	public RoutingSwitchPeripheral() {
	}

	public RoutingSwitchPeripheral(TileSwitchRouting routingSwitch, World world, int x, int y, int z) {
		super(routingSwitch, "routing_switch", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileSwitchRouting) {
			return new RoutingSwitchPeripheral((TileSwitchRouting) te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getRoutingTable", "setRoutingTable" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		if(method < 2) {
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
			}
		}
		return null;
	}
}
