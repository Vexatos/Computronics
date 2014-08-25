package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import mods.railcraft.common.blocks.signals.TileSwitchRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class RoutingSwitchPeripheral implements IPeripheral, IPeripheralProvider {
	private TileEntity routingSwitch;
	private IBlockAccess w;
	private int x, y, z;

	public RoutingSwitchPeripheral() {
	}

	public RoutingSwitchPeripheral(TileEntity routingSwitch, World world, int x2, int y2, int z2) {
		this.routingSwitch = routingSwitch;
		w = world;
		x = x2;
		y = y2;
		z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileSwitchRouting) {
			return new RoutingSwitchPeripheral(te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String getType() {
		return "routing_switch";
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
					if((((TileSwitchRouting) routingSwitch)).getInventory().getStackInSlot(0) != null
						&& ((TileSwitchRouting) routingSwitch).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!(((TileSwitchRouting) routingSwitch)).isSecure()) {
							List<List<String>> pages = ItemRoutingTable.getPages(((TileSwitchRouting) routingSwitch).getInventory().getStackInSlot(0));
							LinkedHashMap<Number, String> pageMap = new LinkedHashMap<Number, String>();
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
					if(((TileSwitchRouting) routingSwitch).getInventory().getStackInSlot(0) != null
						&& ((TileSwitchRouting) routingSwitch).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
						if(!((TileSwitchRouting) routingSwitch).isSecure()) {
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
							ItemRoutingTable.setPages(((TileSwitchRouting) routingSwitch).getInventory().getStackInSlot(0), pages);
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

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(other instanceof RoutingSwitchPeripheral) {
			RoutingSwitchPeripheral o = (RoutingSwitchPeripheral) other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) {
				return true;
			}
		}

		return false;
	}
}
