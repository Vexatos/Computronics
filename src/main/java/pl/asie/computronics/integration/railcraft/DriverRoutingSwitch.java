package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.signals.TileSwitchRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Vexatos
 */
public class DriverRoutingSwitch extends DriverTileEntity {

    public class ManagedEnvironmentRoutingSwitch extends ManagedEnvironment implements NamedBlock {
        private TileEntity routingSwitch;

        public ManagedEnvironmentRoutingSwitch(TileEntity routingSwitch) {
            this.routingSwitch = routingSwitch;
            node = Network.newNode(this, Visibility.Network).withComponent("routing_detector", Visibility.Network).create();
        }

        @Override
        public String preferredName() {
            return "routing_switch";
        }

        @Callback(doc = "function():table; returns the full routing table inside the switch motor, or false and an error message if the table is empty or cannot be accessed")
        public Object[] getRoutingTable(Context c, Arguments a) {
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
    }

    @Override
    public Class<?> getTileEntityClass() {
        return TileSwitchRouting.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
        if((world.getTileEntity(x, y, z)) instanceof TileSwitchRouting) {
            return new ManagedEnvironmentRoutingSwitch(world.getTileEntity(x, y, z));
        }
        return null;
    }
}
