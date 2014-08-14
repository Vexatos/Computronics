package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorRouting;
import mods.railcraft.common.items.ItemRoutingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Vexatos
 */
public class DriverRoutingDetector extends DriverTileEntity {

    public class ManagedEnvironmentRoutingDetector extends ManagedEnvironment implements NamedBlock {
        private TileEntity detector;

        public ManagedEnvironmentRoutingDetector(TileEntity detector) {
            this.detector = detector;
            node = Network.newNode(this, Visibility.Network).withComponent("routing_detector", Visibility.Network).create();
        }

        @Override
        public String preferredName() {
            return "routing_detector";
        }

        @Callback()
        public Object[] getRoutingTable(Context c, Arguments a) {
            if(((DetectorRouting) ((TileDetector) detector).getDetector()).getInventory().getStackInSlot(0) != null
                && ((DetectorRouting) ((TileDetector) detector).getDetector()).getInventory().getStackInSlot(0).getItem() instanceof ItemRoutingTable) {
                if(!((DetectorRouting) ((TileDetector) detector).getDetector()).isSecure()) {
                    List<List<String>> pages = ItemRoutingTable.getPages(((DetectorRouting) ((TileDetector) detector).getDetector()).getInventory().getStackInSlot(0));
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
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
        if(world.getTileEntity(x, y, z) instanceof TileDetector
            && ((TileDetector) world.getTileEntity(x, y, z)).getDetector().getType() == EnumDetector.ROUTING) {
            return new ManagedEnvironmentRoutingDetector(world.getTileEntity(x, y, z));
        }
        return null;
    }
}
