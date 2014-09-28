package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class DriverRoutingDetector extends DriverTileEntity {

	public class ManagedEnvironmentRoutingDetector extends ManagedEnvironmentOCTile<TileDetector> implements NamedBlock {

		public ManagedEnvironmentRoutingDetector(TileDetector detector) {
			super(detector, "routing_detector");
		}

		@Callback(doc = "function():table; returns the full routing table inside the detector, or false and an error message if there is no table or it cannot be accessed")
		public Object[] getRoutingTable(Context c, Arguments a) {
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

		@Callback(doc = "function(routingTable:table):boolean; Sets the routing table inside the detector; argument needs to be a table with number indices and string values; returns 'true' on success, 'false' and an error message otherwise.")
		public Object[] setRoutingTable(Context c, Arguments a) {
			Map pageMap = a.checkTable(0);
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

	@Override
	public Class<?> getTileEntityClass() {
		return TileDetector.class;
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		return (tileEntity != null) && tileEntity instanceof TileDetector
			&& ((TileDetector) tileEntity).getDetector().getType() == EnumDetector.ROUTING;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentRoutingDetector((TileDetector) world.getTileEntity(x, y, z));
	}
}
