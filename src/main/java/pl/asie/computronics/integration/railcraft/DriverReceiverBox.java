package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class DriverReceiverBox extends DriverTileEntity {

    public class ManagedEnvironmentReceiverBox extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private TileEntity box;

        public ManagedEnvironmentReceiverBox(TileEntity box) {
            this.box = box;
            node = Network.newNode(this, Visibility.Network).withComponent("receiver_box", Visibility.Network).create();
        }

        @Override
        public String preferredName() {
            return "receiver_box";
        }

        @Callback(doc = "function():number; Returns the currently most restrictive received aspect that triggers the receiver box")
        public Object[] getSignal(Context c, Arguments a) {
            TileBoxReceiver box = (TileBoxReceiver) this.box;
            if(!box.isSecure()) {
                int signal = box.getTriggerAspect().ordinal();
                if(signal == 5) {
                    signal = -1;
                }
                return new Object[] { signal };
            } else {
                return new Object[] { null, "signal receiver box is locked" };
            }
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return TileBoxReceiver.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
        return new ManagedEnvironmentReceiverBox(world.getTileEntity(x, y, z));
    }
}
