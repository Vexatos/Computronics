package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverReceiverBox {

	private static Object[] getSignal(TileBoxReceiver tile) {
		if(!tile.isSecure()) {
			int signal = tile.getTriggerAspect().ordinal();
			if(signal == 5) {
				signal = -1;
			}
			return new Object[] { signal };
		} else {
			return new Object[] { null, "signal receiver box is locked" };
		}
	}

	private static Object[] aspects() {
		LinkedHashMap<String, Integer> aspectMap = new LinkedHashMap<String, Integer>();
		for(SignalAspect aspect : SignalAspect.VALUES) {
			aspectMap.put(aspect.name().toLowerCase(Locale.ENGLISH), aspect.ordinal());
		}
		return new Object[] { aspectMap };
	}

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileBoxReceiver> {

			public InternalManagedEnvironment(TileBoxReceiver box) {
				super(box, Names.Railcraft_ReceiverBox);
			}

			@Callback(doc = "function():number; Returns the currently most restrictive received aspect that triggers the receiver box")
			public Object[] getSignal(Context c, Arguments a) {
				return DriverReceiverBox.getSignal(tile);
			}

			@Callback(doc = "This is a list of every available Signal Aspect in Railcraft", getter = true)
			public Object[] aspects(Context c, Arguments a) {
				return DriverReceiverBox.aspects();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileBoxReceiver.class;
		}

		@Override
		public li.cil.oc.api.network.ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileBoxReceiver) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCTilePeripheral<TileBoxReceiver> {

		public CCDriver() {
		}

		public CCDriver(TileBoxReceiver box, World world, int x, int y, int z) {
			super(box, Names.Railcraft_ReceiverBox, world, x, y, z);
		}

		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileBoxReceiver) {
				return new CCDriver((TileBoxReceiver) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getSignal", "aspects" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			if(method < getMethodNames().length) {
				switch(method){
					case 0:{
						return DriverReceiverBox.getSignal(tile);
					}
					case 1:{
						return DriverReceiverBox.aspects();
					}
				}
			}
			return null;
		}
	}
}
