package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitLauncher;
import mods.railcraft.common.modules.ModuleExtras;
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
public class DriverLauncherTrack {

	private static Object[] getForce(TrackKitLauncher tile) {
		return new Object[] { ((int) tile.getLaunchForce()) };
	}

	private static Object[] setForce(TrackKitLauncher tile, Object[] arguments) {
		int force = ((Double) arguments[0]).intValue();
		if(force >= 5 && force <= ModuleExtras.config.maxLaunchTrackForce) {
			tile.setLaunchForce(force);
			tile.sendUpdateToClient();
			return new Object[] { true };
		}
		return new Object[] { false, "not a valid force value, needs to be between 5 and " + ModuleExtras.config.maxLaunchTrackForce };
	}

	public static class OCDriver extends DriverTrack<TrackKitLauncher> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitLauncher> {

			public InternalManagedEnvironment(TrackKitLauncher tile) {
				super(tile, Names.Railcraft_LauncherTrack);
			}

			@Callback(doc = "function():number; returns the current force of the track")
			public Object[] getForce(Context c, Arguments a) {
				return DriverLauncherTrack.getForce(tile);
			}

			@Callback(doc = "function():boolean; sets the force of the track; returns true on success")
			public Object[] setForce(Context c, Arguments a) {
				a.checkInteger(0);
				return DriverLauncherTrack.setForce(tile, a.toArray());
			}
		}

		public OCDriver() {
			super(TrackKitLauncher.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TrackKitLauncher> createEnvironment(World world, BlockPos pos, EnumFacing side, TrackKitLauncher tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitLauncher> {

		public CCDriver() {
		}

		public CCDriver(TrackKitLauncher track, World world, BlockPos pos) {
			super(track, Names.Railcraft_LauncherTrack, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitLauncher) {
				return new CCDriver((TrackKitLauncher) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getForce", "setForce" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverLauncherTrack.getForce(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverLauncherTrack.setForce(tile, arguments);
				}
			}
			return null;
		}
	}
}
