package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitLocomotive;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverLocomotiveTrack {

	private static Object[] setMode(TrackKitLocomotive tile, Object[] arguments) {
		byte mode = ((Double) arguments[0]).byteValue();
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		data.setByte("mode", (byte) Math.abs(mode - 2));
		tile.readFromNBT(data);
		tile.sendUpdateToClient();
		return new Object[] { true };
	}

	private static Object[] getMode(TrackKitLocomotive tile) {
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		return new Object[] { data.hasKey("mode") ? Math.abs(data.getByte("mode") % EntityLocomotive.LocoMode.VALUES.length - 2) : null };
	}

	private static Object[] modes() {
		LinkedHashMap<String, Integer> modeMap = new LinkedHashMap<String, Integer>();
		for(EntityLocomotive.LocoMode mode : EntityLocomotive.LocoMode.VALUES) {
			modeMap.put(mode.name().toLowerCase(Locale.ENGLISH), Math.abs(mode.ordinal() - 2));
		}
		return new Object[] { modeMap };
	}

	public static class OCDriver extends DriverTrack<TrackKitLocomotive> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitLocomotive> {

			public InternalManagedEnvironment(TrackKitLocomotive tile) {
				super(tile, Names.Railcraft_LocomotiveTrack);
			}

			@Callback(doc = "function(mode:number):boolean; sets the Locomotive mode to the specified value; returns true on success")
			public Object[] setMode(Context c, Arguments a) {
				a.checkInteger(0);
				if(a.checkInteger(0) > 2 || a.checkInteger(0) < 0) {
					throw new IllegalArgumentException(
						"bad argument #1 (0, 1 or 2 expected, got " + a.checkInteger(0) + ")");
				}
				return DriverLocomotiveTrack.setMode(tile, a.toArray());
			}

			@Callback(doc = "function():number; returns the current Locomotive mode")
			public Object[] getMode(Context c, Arguments a) {
				return DriverLocomotiveTrack.getMode(tile);
			}

			@Callback(doc = "This is a table of every available Locomotive mode", getter = true)
			public Object[] modes(Context c, Arguments a) {
				return DriverLocomotiveTrack.modes();
			}
		}

		public OCDriver() {
			super(TrackKitLocomotive.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TrackKitLocomotive> createEnvironment(World world, BlockPos pos, EnumFacing side, TrackKitLocomotive tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitLocomotive> {

		public CCDriver() {
		}

		public CCDriver(TrackKitLocomotive track, World world, BlockPos pos) {
			super(track, Names.Railcraft_LocomotiveTrack, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitLocomotive) {
				return new CCDriver((TrackKitLocomotive) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setMode", "getMode", "modes" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					if((Double) arguments[0] > 2 || (Double) arguments[0] < 0) {
						throw new LuaException("mode needs to be between 0 and 2");
					}
					return DriverLocomotiveTrack.setMode(tile, arguments);
				}
				case 1: {
					return DriverLocomotiveTrack.getMode(tile);
				}
				case 2: {
					return DriverLocomotiveTrack.modes();
				}
			}
			return null;
		}
	}
}
