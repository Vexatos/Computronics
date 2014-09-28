package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorRouting;
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
public class RoutingDetectorPeripheral extends CCTilePeripheral<TileDetector> {

	public RoutingDetectorPeripheral() {
	}

	public RoutingDetectorPeripheral(TileDetector detector, World world, int x, int y, int z) {
		super(detector, "routing_detector", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileDetector && ((TileDetector) te).getDetector().getType() == EnumDetector.ROUTING) {
			return new RoutingDetectorPeripheral((TileDetector) te, world, x, y, z);
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
					if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
						&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!((DetectorRouting) tile.getDetector()).isSecure()) {
							List<List<String>> pages = ItemRoutingTable.getPages(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0));
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
							return new Object[] { false, "routing detector is locked" };
						}
					}
					return new Object[] { false, "no routing table found" };
				}
				case 1:{
					if(arguments.length < 1 || !(arguments[0] instanceof Map)) {
						throw new LuaException("first argument needs to be a table");
					}
					Map pageMap = (Map) arguments[0];
					if(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0) != null
						&& ((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!((DetectorRouting) tile.getDetector()).isSecure()) {
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
							ItemRoutingTable.setPages(((DetectorRouting) tile.getDetector()).getInventory().getStackInSlot(0), pages);
							return new Object[] { true };
						} else {
							return new Object[] { false, "routing detector is locked" };
						}
					}
					return new Object[] { false, "no routing table found" };
				}
			}
		}
		return null;
	}
}
