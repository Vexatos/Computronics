package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

/**
 * @author Vexatos
 */
public class DriverReceiverBox extends DriverTileEntity {

	public class ManagedEnvironmentReceiverBox extends ManagedEnvironmentOCTile<TileBoxReceiver> {

		public ManagedEnvironmentReceiverBox(TileBoxReceiver box) {
			super(box, "receiver_box");
		}

		@Callback(doc = "function():number; Returns the currently most restrictive received aspect that triggers the receiver box")
		public Object[] getSignal(Context c, Arguments a) {
			TileBoxReceiver box = this.tile;
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
		return new ManagedEnvironmentReceiverBox((TileBoxReceiver) world.getTileEntity(x, y, z));
	}
}
