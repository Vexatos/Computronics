package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

import java.util.LinkedHashMap;
import java.util.Locale;

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

		@Callback(doc = "This is a list of every available Signal Aspect in Railcraft", getter = true)
		public Object[] aspects(Context c, Arguments a) {
			LinkedHashMap<String, Integer> aspectMap = new LinkedHashMap<String, Integer>();
			for(SignalAspect aspect : SignalAspect.VALUES) {
				aspectMap.put(aspect.name().toLowerCase(Locale.ENGLISH), aspect.ordinal());
			}
			return new Object[] { aspectMap };
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
