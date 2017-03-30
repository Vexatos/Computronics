package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitPriming;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class DriverPrimingTrack {

	private static Object[] getFuse(TrackKitPriming tile) {
		return new Object[] { tile.getFuse() };
	}

	private static Object[] setFuse(TrackKitPriming tile, Object[] arguments) {
		int fuse = ((Double) arguments[0]).intValue();
		if(fuse >= 0 && fuse <= 500) {
			tile.setFuse((short) fuse);
			tile.sendUpdateToClient();
			return new Object[] { true };
		}
		return new Object[] { false, "not a valid fuse time value, needs to be between 0 and 500" };
	}

	public static class OCDriver extends DriverTrack<TrackKitPriming> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitPriming> {

			public InternalManagedEnvironment(TrackKitPriming tile) {
				super(tile, Names.Railcraft_PrimingTrack);
			}

			@Callback(doc = "function():number; returns the current fuse time, in ticks, of the track")
			public Object[] getFuse(Context c, Arguments a) {
				return DriverPrimingTrack.getFuse(tile);
			}

			@Callback(doc = "function():boolean; sets the fuse time, in ticks,  of the track; returns true on success")
			public Object[] setFuse(Context c, Arguments a) {
				a.checkInteger(0);
				return DriverPrimingTrack.setFuse(tile, a.toArray());
			}
		}

		public OCDriver() {
			super(TrackKitPriming.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TrackKitPriming> createEnvironment(World world, BlockPos pos, EnumFacing side, TrackKitPriming tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitPriming> {

		public CCDriver() {
		}

		public CCDriver(TrackKitPriming track, World world, BlockPos pos) {
			super(track, Names.Railcraft_PrimingTrack, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitPriming) {
				return new CCDriver((TrackKitPriming) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getFuse", "setFuse" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverPrimingTrack.getFuse(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverPrimingTrack.setFuse(tile, arguments);
				}
			}
			return null;
		}
	}
}
